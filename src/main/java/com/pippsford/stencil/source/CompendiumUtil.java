package com.pippsford.stencil.source;

import static com.pippsford.stencil.source.CompendiumSourceProvider.HERE;
import static com.pippsford.stencil.source.CompendiumSourceProvider.KEY;
import static com.pippsford.stencil.source.CompendiumSourceProvider.readHereEntry;
import static com.pippsford.stencil.source.CompendiumSourceProvider.readNormalEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import com.pippsford.common.MutableBoolean;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Utility command-line for working with compendium files. It provides two command-line modes for tidying compendium files.
 *
 * <h2>Sort</h2>
 *
 * <pre>java CompendiumUtil sort my/file/to/process.comp</pre>
 *
 * <p>Sorts the entries in the specified file into their natural String order.
 *
 * <h2>Scan</h2>
 *
 * <pre>java CompendiumUtil scan my/folder "glob:*.comp"</pre>
 * <pre>java CompendiumUtil scan my/folder "regex:[^g].*\.comp"</pre>
 *
 * <p>Recursively scans a folder and sorts all the files that match the specified path matcher. See Java NIO FileSystem.getPathMatcher for details.
 *
 * <p>Sorts the entries in the specified file into their natural String order.
 *
 * @author Simon Greatrix on 20/01/2021.
 */
public class CompendiumUtil {

  private static TreeMap<String, String> checkData(Map<String, String> dataIn) {
    TreeMap<String, String> data = new TreeMap<>();
    dataIn.forEach((k, v) -> {
      if (!KEY.matcher(k).matches()) {
        throw new IllegalArgumentException("Invalid key: " + k);
      }
      if (!v.endsWith("\n")) {
        v = v + "\n";
      }
      data.put(k, v);
    });
    return data;
  }


  private static String createMarker(String[] lines) {
    TreeSet<String> set = new TreeSet<>(Arrays.asList(lines));
    // use EOT if possible
    if (!set.contains("EOT")) {
      return "EOT";
    }

    // Section marker
    if (!set.contains("§§§")) {
      return "§§§";
    }

    // Construct a numeric marker
    int len = 0;
    int hc = 1;
    for (String s : lines) {
      hc = hc * 31 + s.hashCode();
      len += s.length();
    }
    hc = 0x7fffffff & hc;
    len = 0x7fffffff & len;
    String m = String.format("§§§:%04d:%04d", len % 10000, hc % 10000);
    if (!set.contains(m)) {
      return m;
    }

    // Construct a marker that differs from the n'th line at the n'th position.
    StringBuilder builder = new StringBuilder();
    for (int l = 0; l < lines.length; l++) {
      m = builder.toString();
      if (l > 2 && !set.contains(m)) {
        return m;
      }
      if (lines[l].length() < l) {
        builder.append('§');
      } else {
        char ch = lines[l].charAt(l);
        char c2 = (char) ('a' + (ch % 26));
        builder.append(c2);
      }
    }
    return builder.toString();
  }


  /**
   * Entry point.
   *
   * @param args command line
   *
   * @throws Exception if it fails
   */
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE", justification = "False positive")
  public static void main(String[] args) throws Exception {
    if (args.length == 2 && args[0].equals("sort")) {
      URI cwd = Paths.get("").toUri();
      URI uri = cwd.resolve(URI.create(args[1]));
      Path path = Paths.get(uri);

      SortedMap<String, String> data = readCompendium(path);
      writeCompendium(path, data);
      return;
    }

    if (args.length == 3 && args[0].equals("scan")) {
      URI cwd = Paths.get("").toUri();
      URI uri = cwd.resolve(URI.create(args[1]));
      Path path = Paths.get(uri);
      uri = new URI(uri.getScheme(), "/", null);
      PathMatcher pathMatcher = FileSystems.getFileSystem(uri).getPathMatcher(args[2]);
      MutableBoolean abort = new MutableBoolean(false);
      try (Stream<Path> stream = Files.walk(path)) {
        stream.filter(pathMatcher::matches).forEach(p -> {
          if (abort.get()) {
            return;
          }
          System.out.println("Processing " + p);
          try {
            SortedMap<String, String> data = readCompendium(p);
            writeCompendium(p, data);
          } catch (IOException | StencilStorageException e) {
            e.printStackTrace();
            abort.set(true);
          }
        });
      }
      return;
    }

    System.err.println("Usage:\n"
        + "\tjava " + CompendiumUtil.class.getName() + " sort <file>\n"
        + "\tjava " + CompendiumUtil.class.getName() + " scan <root> <syntaxAndPattern>");
    System.exit(1);
  }


  private static String markerFor(String value) {
    String[] lines = value.split("\n");

    // if has trailing whitespace, needs marker
    int length = value.length();
    if (length > 1 && Character.isWhitespace(value.charAt(length - 2))) {
      return createMarker(lines);
    }

    // if any line matches the 'key' pattern, needs marker
    for (String l : lines) {
      if (KEY.matcher(l).matches() || HERE.matcher(l).matches()) {
        return createMarker(lines);
      }
    }

    return null;
  }


  /**
   * Read a compendium from a file.
   *
   * @param path the file's path
   *
   * @return the data in the compendium
   *
   * @throws StencilStorageException if the compendium cannot be read
   */
  public static SortedMap<String, String> readCompendium(Path path) throws StencilStorageException {
    URI uri = path.toUri();
    TreeMap<String, String> data = new TreeMap<>();
    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      PushbackLineReader lineReader = new PushbackLineReader(uri, reader);
      String line;
      while ((line = lineReader.readLine()) != null) {
        Matcher matcher = HERE.matcher(line);
        if (matcher.matches()) {
          String entry = readHereEntry(uri, lineReader, matcher.group(2));
          data.put(matcher.group(1), entry);
        } else {
          matcher = KEY.matcher(line);
          if (matcher.matches()) {
            String entry = readNormalEntry(lineReader);
            data.put(line, entry);
          }
        }
      }
    } catch (IOException ioException) {
      throw new StencilStorageException("Unable to read from: " + path, ioException);
    }
    return data;
  }


  /**
   * Write a compendium to a file.
   *
   * @param path   the path for the file
   * @param dataIn the data in the compendium
   *
   * @throws IOException if writing the file fails
   */
  public static void writeCompendium(Path path, Map<String, String> dataIn) throws IOException {
    try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      writeCompendium(writer, dataIn);
    }
  }


  public static void writeCompendium(Writer writer, Map<String, String> dataIn) throws IOException {
    TreeMap<String, String> data = checkData(dataIn);
    writeCompendiumInternal(writer, data);
  }


  /**
   * Write a compendium to a String.
   *
   * @param dataIn the compendium data
   *
   * @return the data as a String
   */
  public static String writeCompendium(Map<String, String> dataIn) {
    StringWriter writer = new StringWriter();
    try {
      writeCompendium(writer, dataIn);
    } catch (IOException ioException) {
      throw new UncheckedIOException("I/O Exception without I/O", ioException);
    }
    return writer.toString();
  }


  private static void writeCompendiumInternal(Writer writer, TreeMap<String, String> data) throws IOException {
    boolean isFirst = true;
    for (Entry<String, String> entry : data.entrySet()) {
      if (isFirst) {
        isFirst = false;
      } else {
        writer.write("\n\n\n");
      }
      String marker = markerFor(entry.getValue());
      if (marker == null) {
        writer.write(entry.getKey());
        writer.write('\n');
        writer.write(entry.getValue());
      } else {
        writer.write(entry.getKey());
        writer.write(" >> ");
        writer.write(marker);
        writer.write('\n');
        writer.write(entry.getValue());
        writer.write(marker);
        writer.write('\n');
      }
    }
    writer.flush();
  }

}
