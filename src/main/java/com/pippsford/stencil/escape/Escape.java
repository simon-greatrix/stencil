package com.pippsford.stencil.escape;

/**
 * Handle escaping for output.
 *
 * @author Simon Greatrix on 01/01/2021.
 */
public interface Escape {

  /**
   * Perform the escaping on an input string. If the input is null, returns an empty string.
   *
   * @param input the input
   *
   * @return the escaped input
   */
  String escape(String input);

  /**
   * Check if this implement can handle the named escaping style.
   *
   * @param name the style's name
   *
   * @return true if this handles the named style
   */
  boolean isHandlerFor(String name);

}
