package com.pippsford.stencil.escape;

/**
 * String literal escaping for Java, Javascript and JSON.
 *
 * @author Simon Greatrix on 27/10/2017.
 */
public class StringLiteral {

  /** The codepoints that have special escape representations. */
  private final String[] specials = new String[128];

  private final boolean toAscii;


  StringLiteral(int[] escapeChars, String[] replaceChars, boolean toAscii) {
    for (int i = 0; i < 32; i++) {
      specials[i] = String.format("\\u%04x", i);
    }
    for (int i = 0; i < escapeChars.length; i++) {
      specials[escapeChars[i]] = replaceChars[i];
    }
    if (toAscii) {
      // Hack to represent delete as an escape when in ASCII mode.
      specials[127] = "\\u007f";
    }
    this.toAscii = toAscii;
  }


  private void doAscii(StringBuilder buffer, int cp) {
    String special = specials[cp];
    if (special == null) {
      buffer.append((char) cp);
    } else {
      buffer.append(special);
    }
  }


  private void doBMP(StringBuilder buffer, int cp) {
    if (toAscii) {
      handleBMP(buffer, cp);
    } else {
      buffer.append((char) cp);
    }
  }


  private void doNonBMP(StringBuilder buffer, int cp) {
    if (toAscii) {
      handleNonBMP(buffer, cp);
    } else {
      buffer.append(Character.toChars(cp));
    }
  }


  /**
   * Escape the input. A null is returned as an empty string.
   *
   * @param input the input
   *
   * @return the escaped string
   */
  public String escape(String input) {
    if (input == null) {
      return "";
    }

    // Create an initial buffer with a small amount of extra for escaping.
    StringBuilder buffer = new StringBuilder(input.length() + 16);
    int index = 0;
    while (index < input.length()) {
      int cp = input.codePointAt(index);
      index += Character.charCount(cp);

      // check for a standard escape char
      if (cp < 0x80) {
        doAscii(buffer, cp);

      } else if (cp < 0x1_0000) {
        doBMP(buffer, cp);

      } else {
        doNonBMP(buffer, cp);
      }
    }

    return buffer.toString();
  }


  /**
   * Handle code points on the basic multilingual pane.
   *
   * @param buffer the buffer to write to
   * @param cp     the code point to write
   */
  protected void handleBMP(StringBuilder buffer, int cp) {
    buffer.append(String.format("\\u%04x", cp));
  }


  /**
   * Handle code points outside the basic multilingual pane that require a surrogate pair.
   *
   * @param buffer the buffer to write to
   * @param cp     the code point to write
   */
  protected void handleNonBMP(StringBuilder buffer, int cp) {
    buffer.append(String.format("\\u%04x\\u%04x", (int) Character.highSurrogate(cp), (int) Character.lowSurrogate(cp)));
  }

}
