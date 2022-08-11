package com.pippsford.stencil.parser;

/**
 * An immutable copy of a parsing match result.
 *
 * @author Simon Greatrix on 22/12/2020.
 */
class FixMatch extends Match {

  /**
   * The matched sub-groups from the pattern.
   */
  final String[] groups;


  FixMatch(Match original) {
    super(original);
    if ((original instanceof BlockMatch) && ((BlockMatch) original).groups != null) {
      groups = ((BlockMatch) original).groups.clone();
    } else {
      groups = new String[0];
    }
  }

}
