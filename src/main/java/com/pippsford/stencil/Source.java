package com.pippsford.stencil;

import java.util.LinkedList;

import com.pippsford.stencil.source.StencilNotFoundException;

/**
 * The source of a stencil.
 *
 * @author Simon Greatrix on 11/01/2021.
 */
public class Source {

  /**
   * Split off a file suffix and clean up the path to create a source reference. A clean path always starts with a '/' and contains no elements like "//",
   * "/./", nor "/../".
   *
   * @param path the path
   *
   * @return the source reference
   *
   * @throws StencilNotFoundException if the path indicates an empty or illegal file name.
   */
  public static String cleanPath(String path) throws StencilNotFoundException {
    if (path == null || path.isEmpty()) {
      throw new StencilNotFoundException("A path must be specified");
    }

    String[] parts = path.split("/");
    LinkedList<String> list = new LinkedList<>();
    for (String s : parts) {
      // handle ".."
      if (s.equals("..")) {
        if (!list.isEmpty()) {
          list.removeLast();
        }
        continue;
      }

      // skip "." and empty parts
      if (!(s.isEmpty() || s.equals("."))) {
        list.add(s);
      }
    }

    if (list.isEmpty()) {
      throw new StencilNotFoundException("A path must not resolve to an empty path");
    }

    StringBuilder buf = new StringBuilder(path.length());
    for (String s : list) {
      buf.append('/').append(s);
    }
    return buf.toString();
  }


  /**
   * Split off a file suffix and clean up the path to create a source reference. A clean path always starts with a '/', and contains no elements like "//",
   * "/./", nor "/../".
   *
   * @param path the path
   *
   * @return the source reference
   *
   * @throws StencilNotFoundException if the path indicates an empty or illegal file name.
   */
  public static Source of(String path) throws StencilNotFoundException {
    String clean = cleanPath(path);

    int lastSlash = clean.lastIndexOf('/');
    String fileName = clean.substring(lastSlash + 1);
    int lastDot = fileName.lastIndexOf('.');
    String suffix;
    if (lastDot == -1) {
      // no suffix
      suffix = "";
    } else {
      suffix = fileName.substring(lastDot);
      clean = clean.substring(0, clean.length() - suffix.length());
    }

    return new Source(clean, suffix);
  }


  private final String path;

  private final String suffix;


  private Source(String path, String suffix) {
    this.path = path;
    this.suffix = suffix;
  }


  /**
   * Get the path for this source.
   *
   * @return the path
   */
  public String getPath() {
    return path;
  }


  /**
   * Get the suffix for this source.
   *
   * @return the suffix
   */
  public String getSuffix() {
    return suffix;
  }


  @Override
  public String toString() {
    return path + suffix;
  }

}
