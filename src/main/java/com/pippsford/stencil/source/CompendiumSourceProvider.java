package com.pippsford.stencil.source;

import static com.pippsford.common.StringUtils.logSafe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import jakarta.annotation.Nullable;

import com.pippsford.stencil.Source;
import com.pippsford.stencil.SourceProvider;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A provider of source that provides stencils from a compendium.
 *
 * <p>A compendium is a text file that can be loaded from the class path. Each compendium can contain multiple stencils. Each stencil is preceded by its path.
 * Normally any line starting with a '/' is assumed to be a path specification. Whitespace after a stencil is ignored.</p>
 *
 * <p>A path:</p>
 *
 * <ol>
 *   <li>Starts with a '/'.</li>
 *   <li>Does not end with a '/', unless it is just "/".</li>
 *   <li>Cannot contain these characters: '{', '}', '&lt;', '&gt;', '[', ']', ':', ';', '\'</li>
 * </ol>
 *
 * <pre>
 *   /this/is/a/path
 *
 *   This is the text of a stencil.
 *
 *   It carries on until a line starts with a '/'.
 *
 *   /this/is/another/stencil &gt;&gt; END-OF
 *
 *   This is a second stencil, using a "here document".
 *
 *   /this/is/NOT/A/PATH this line does not start a new stencil because we are still within the "here document".
 *
 *   END-OF
 *
 *   /this/is/a/third/stencil
 *   A third stencil in this compendium.
 * </pre>
 *
 * @author Simon Greatrix on 20/01/2021.
 */
public class CompendiumSourceProvider implements SourceProvider {

  static final Pattern HERE = Pattern.compile(
      "(/[^{}<>\\[\\]:;\\\\/](?:[^{}<>\\[\\]:;\\\\]*[^{}<>\\[\\]:;\\\\/])?)\\s+>>\\s+([\\p{Graph}&&[^{}<>\\[\\]]]+)",
      Pattern.UNICODE_CHARACTER_CLASS + Pattern.COMMENTS + Pattern.CASE_INSENSITIVE + Pattern.DOTALL
  );

  static final Pattern KEY = Pattern.compile(
      "(/[^{}<>\\[\\]:;\\\\/](?:[^{}<>\\[\\]:;\\\\]*[^{}<>\\[\\]:;\\\\/])?)",
      Pattern.UNICODE_CHARACTER_CLASS + Pattern.COMMENTS + Pattern.CASE_INSENSITIVE + Pattern.DOTALL
  );


  private static Source initialiseSource(String path) {
    try {
      return Source.of(path);
    } catch (StencilNotFoundException e) {
      throw new IllegalArgumentException("The compendium search path must specify files, not folders", e);
    }
  }


  static String readHereEntry(URI inputSource, PushbackLineReader reader, String marker) throws StencilStorageException {
    StringBuilder entry = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      if (line.equals(marker)) {
        return entry.toString();
      }
      entry.append(line).append('\n');
    }
    throw new CompendiumMarkerMissingException("Marker \"" + marker + "\" was not found in " + inputSource.toASCIIString());
  }


  static String readNormalEntry(PushbackLineReader reader) throws StencilStorageException {
    StringBuilder entry = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      if (KEY.matcher(line).matches() || HERE.matcher(line).matches()) {
        // start of next entry
        reader.unread(line);
        trimTrailingWhitespace(entry);
        return entry.toString();
      }
      entry.append(line).append('\n');
    }
    trimTrailingWhitespace(entry);
    return entry.toString();
  }


  static void trimTrailingWhitespace(StringBuilder builder) {
    int length = builder.length();
    while (length > 0 && Character.isWhitespace(builder.charAt(length - 1))) {
      length--;
    }
    builder.setLength(length);
    builder.append('\n');
  }


  private final List<Source> sources;

  private ClassLoader classLoader;


  /**
   * New instance.
   *
   * @param paths sources to use
   */
  public CompendiumSourceProvider(List<String> paths) {
    sources = paths.stream().map(CompendiumSourceProvider::initialiseSource).collect(Collectors.toList());
    classLoader = Thread.currentThread().getContextClassLoader();
  }


  /**
   * New instance.
   *
   * @param paths sources to use
   */
  public CompendiumSourceProvider(String... paths) {
    sources = Arrays.stream(paths).map(CompendiumSourceProvider::initialiseSource).collect(Collectors.toList());
    classLoader = Thread.currentThread().getContextClassLoader();
  }


  @Nullable
  @Override
  public String getSource(Source source, Locale locale) throws StencilStorageException {
    String path = source.toString();
    for (Locale testLocale : PathLocaliser.getCandidateLocales(locale)) {
      for (Source s : sources) {
        String result = handleGet(s, testLocale, path);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }


  @Override
  public String getSourceRoot() {
    return sources.toString();
  }


  private String handleGet(Source source, Locale locale, String path) throws StencilStorageException {
    String sourcePath = PathLocaliser.getSearchName(source.getPath(), source.getSuffix(), locale);
    try {
      Enumeration<URL> inputFileUrls = classLoader.getResources(sourcePath.substring(1));
      while (inputFileUrls.hasMoreElements()) {
        URL inputUrl = inputFileUrls.nextElement();
        try (InputStream inputStream = inputUrl.openStream()) {
          String result = scanStream(inputUrl, inputStream, path);
          if (result != null) {
            return result;
          }
        }
      }
    } catch (IOException ioException) {
      throw new StencilStorageException("Error reading resources for: " + sourcePath, ioException);
    }

    // not found
    return null;
  }


  private String scanStream(URL inputUrl, InputStream inputStream, String path) throws StencilStorageException {
    URI uri;
    try {
      uri = inputUrl.toURI();
    } catch (URISyntaxException e) {
      throw new InternalError("JVM generated invalid URL for resource: " + logSafe(inputUrl.toString()));
    }
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    PushbackLineReader lineReader = new PushbackLineReader(uri, bufferedReader);
    String line;
    String markedPath = null;
    String marker = null;
    while ((line = lineReader.readLine()) != null) {
      if (marker != null) {
        if (marker.equals(line)) {
          marker = null;
        }
        continue;
      }

      if (line.equals(path)) {
        return readNormalEntry(lineReader);
      }

      Matcher matcher = HERE.matcher(line);
      if (matcher.matches()) {
        if (matcher.group(1).equals(path)) {
          return readHereEntry(uri, lineReader, matcher.group(2));
        } else {
          markedPath = matcher.group(1);
          marker = matcher.group(2);
        }
      }
    }

    if (marker != null) {
      throw new CompendiumMarkerMissingException("Marker \"" + marker + "\" was not found for " + markedPath + " in " + inputUrl);
    }
    return null;
  }


  /**
   * Set the class loader with which to load the compendium.
   *
   * @param classLoader the class loader
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public void setClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

}
