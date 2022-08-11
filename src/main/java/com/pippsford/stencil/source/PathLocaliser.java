package com.pippsford.stencil.source;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle.Control;

/**
 * A standard mechanism for localising a stencil's path. This uses the same algorithm as Java ResourceBundles do.
 *
 * @author Simon Greatrix on 13/02/2021.
 */
public class PathLocaliser {

  /** We need an instance of Control to provide the locale search order. */
  static final Control CONTROL = new Control() {
    // do nothing - this appears to be the only way to get an instance of Control.
  };


  /**
   * Get the priority ordered locales that need to be searched to match a target locale. The first locale in the list will be the target. The last locale in
   * the list will be the root locale.
   *
   * @param target the target locale
   *
   * @return the ordered search list.
   */
  static List<Locale> getCandidateLocales(Locale target) {
    return CONTROL.getCandidateLocales("", target);
  }


  /**
   * Get a search name for a path and locale. The file
   */
  static String getSearchName(String path, String suffix, Locale locale) {
    return CONTROL.toBundleName(path, locale) + suffix;
  }

}
