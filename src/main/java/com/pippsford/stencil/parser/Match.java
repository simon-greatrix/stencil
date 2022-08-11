package com.pippsford.stencil.parser;

import com.pippsford.stencil.blocks.BlockTypes;

/**
 * A match during compilation.
 */
class Match {

  /**
   * The type of the match.
   */
  final BlockTypes type;

  /** The source text. */
  private final String source;

  /**
   * End of match.
   */
  int end;

  /**
   * Start of match.
   */
  int start;

  /**
   * Text that was matched.
   */
  String text;


  /**
   * New match.
   *
   * @param type block type to match
   */
  Match(BlockTypes type, String source) {
    this.type = type;
    this.source = source;
  }


  /**
   * Copy constructor.
   *
   * @param original the original
   */
  protected Match(Match original) {
    type = original.type;
    source = original.source;
    end = original.end;
    start = original.start;
    text = original.text;
  }


  Location getLocation() {
    return new Location(source, start);
  }

}
