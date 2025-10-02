package com.pippsford.stencil.parser;

import com.pippsford.stencil.blocks.BlockTypes;

/**
 * Parse and compile a page.
 */
public class BlockParser {

  /** The matcher for a here-value. */
  private final BlockMatch here;

  /**
   * Length of the page source in characters.
   */
  private final int length;

  /**
   * Matchers for each block type.
   */
  private final BlockMatch[] matches;

  /**
   * Source text.
   */
  private final String source;

  /**
   * A match result for static content.
   */
  private final Match staticContent;

  /**
   * Current position of compilation.
   */
  private int pos = 0;

  /**
   * Handling for the optional [else] directive. If an [end] is encountered when an [else] is expected, it is pushed back.
   */
  private FixMatch pushedBack = null;


  /**
   * New parser for specified source page.
   *
   * @param source the source
   */
  public BlockParser(String source) {
    this.source = source;
    BlockTypes[] bs = BlockTypes.values();
    matches = new BlockMatch[bs.length];
    for (int i = 0; i < bs.length; i++) {
      matches[i] = new BlockMatch(bs[i], source);
    }
    here = matches[BlockTypes.VALUE_HERE.ordinal()];

    staticContent = new Match(null, source);
    staticContent.start = 0;
    staticContent.end = source.length() - 1;
    length = source.length();
  }


  /**
   * Get the next matching block.
   *
   * @return next block, or null if finished
   */
  FixMatch next(boolean normalMode) {
    if (pushedBack != null) {
      FixMatch match = pushedBack;
      pushedBack = null;
      return match;
    }

    if (pos >= length) {
      return null;
    }

    // update all matches
    for (BlockMatch match : matches) {
      match.update(pos);
    }

    Match m;
    if (normalMode) {
      // find the next match
      m = matches[0];
      for (int i = 1; i < matches.length; i++) {
        Match mi = matches[i];
        if (mi.start < m.start) {
          m = mi;
        }
      }
    } else {
      // when inverted, we only care about here-values
      m = here;
    }

    // if matched at current position, we have match
    if (m.start == pos) {
      pos = m.end;
      return new FixMatch(m);
    }

    // if next match is some distance ahead, we have a static block
    int end = Math.min(length, m.start);
    staticContent.start = pos;
    staticContent.end = end;
    staticContent.text = source.substring(pos, end);
    pos = end;
    return new FixMatch(staticContent);
  }


  void pushback(FixMatch match) {
    pushedBack = match;
  }

}
