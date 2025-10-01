package com.pippsford.stencil.source;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

import com.pippsford.stencil.Source;

/**
 * A source provider that uses a map.
 *
 * @author Simon Greatrix on 29/12/2020.
 */
public class MemorySourceProvider extends IndividualSourceProvider {

  private final String root;

  private final Map<String, String> sourceFiles = Collections.synchronizedMap(new HashMap<>());


  /** New instance rooted at "". */
  public MemorySourceProvider() {
    this("");
  }


  /**
   * New instance with the specified root.
   *
   * @param name the name for the root
   */
  public MemorySourceProvider(String name) {
    root = (name != null) ? name : "";
  }


  @Override
  public String getSourceRoot() {
    return root;
  }


  @Nullable
  @Override
  protected String handleGet(String path) {
    return sourceFiles.get(path);
  }


  /**
   * Insert a "file" (actually explicit text) into this provider.
   *
   * @param locale     the locale associated with the new entry.
   * @param path       the nominal path for the new entry
   * @param sourceText the contents of the new entry
   */
  public void putFile(Locale locale, String path, String sourceText) {
    Source sourceFile;
    try {
      sourceFile = Source.of(path);
    } catch (StencilNotFoundException e) {
      throw new IllegalArgumentException("Invalid path: " + path, e);
    }
    String key = PathLocaliser.getSearchName(sourceFile.getPath(), sourceFile.getSuffix(), locale);
    sourceFiles.put(key, sourceText);
  }


  @Override
  public String toString() {
    return String.format(
        "MemorySourceProvider(sourceFiles=%s)",
        sourceFiles.size()
    );
  }

}
