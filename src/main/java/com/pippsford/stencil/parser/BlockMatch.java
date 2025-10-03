package com.pippsford.stencil.parser;

import java.util.regex.Matcher;

import com.pippsford.stencil.blocks.BlockTypes;

/**
 * A match to a block type during page compilation.
 */
class BlockMatch extends Match {

  /**
   * The matcher that matches this type.
   */
  final Matcher matcher;

  /**
   * The matched subgroups from the pattern.
   */
  String[] groups;


  /**
   * Create a match for a given block type.
   *
   * @param type   the type to match
   * @param source the source page to compile
   */
  BlockMatch(BlockTypes type, String source) {
    super(type, source);
    matcher = this.type.matcher(source);
    start = -1;
    update(0);
  }


  /**
   * Update this match to find the next match at or after the specified position.
   *
   * @param pos the new position
   */
  void update(int pos) {
    if (pos <= start) {
      return;
    }

    if (matcher.find(pos)) {
      start = matcher.start();
      end = matcher.end();
      text = matcher.group();
      int gc = matcher.groupCount() + 1;
      if (groups == null || (groups.length != gc)) {
        groups = new String[gc];
      }
      for (int i = 0; i < gc; i++) {
        groups[i] = matcher.group(i);
      }
    } else {
      start = Integer.MAX_VALUE;
      end = Integer.MAX_VALUE;
      text = null;
    }
  }

}
