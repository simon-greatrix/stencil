package com.pippsford.stencil.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The row and column of a position in a block of text.
 */
class Location {

  private static final Pattern END_OF_LINE = Pattern.compile("\\R");

  private int column;

  private int row;


  /**
   * Derive the location.
   *
   * @param text the text
   * @param pos  the character position
   */
  Location(String text, int pos) {
    if (pos >= 0 && pos < text.length()) {
      text = text.substring(0, pos);
    }
    Matcher matcher = END_OF_LINE.matcher(text);
    row = 1;
    column = pos;
    while (matcher.find()) {
      row++;
      column = pos - matcher.end();
    }
  }


  public int getColumn() {
    return column;
  }


  public int getRow() {
    return row;
  }

}
