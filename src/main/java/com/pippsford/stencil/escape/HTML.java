package com.pippsford.stencil.escape;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.owasp.html.Encoding;

/**
 * HTML sanitization using the OWASP HTML sanitizer library.
 *
 * @author Simon Greatrix on 12/01/2021.
 */
public class HTML {

  /**
   * Check a character for eliding. The return value indicates what was seen:
   *
   * <p>1 : Good BMP character</p>
   *
   * <p>2 : Good surrogate pair</p>
   *
   * <p>0 : Bad BMP character</p>
   *
   * <p>-1 : Bad surrogate pair</p>
   *
   * @param builder the output
   * @param index   the character's index
   *
   * @return see above
   */
  private static int checkCharacter(StringBuilder builder, int index) {
    char c0 = builder.charAt(index);

    // Check for CR, Delete, C1 codes, and BMP non-characters
    if (isBadChar(c0)) {
      // These characters must be elided
      return 0;
    }

    if (c0 == '\r') {
      int i1 = index + 1;
      if (i1 < builder.length() && builder.charAt(i1) == '\n') {
        // Seen CRLF, so elide the CR
        return 0;
      }

      // replace CR with LF and say it is OK
      builder.setCharAt(index, '\n');
      return 1;
    }

    // Check for non-BMP non-character. Isolated surrogates have already been removed.
    if (Character.isHighSurrogate(c0)) {
      // OWASP sanitizer replaces all supplemental code-points with numerical escapes, so this does not happen
      int cp = builder.codePointAt(index);
      // Elide if a non-character, otherwise the surrogate pair is OK
      return (cp & 0xfffe) == 0xfffe ? -1 : 2;
    }

    return 1;
  }


  /**
   * Escape and sanitize some HTML text. The output will be valid HTML characters only.
   *
   * @param input the input text to escape
   *
   * @return the sanitized and escaped HTML
   */
  public static String escape(String input) {
    if (input == null || input.isEmpty()) {
      return "";
    }

    StringBuilder builder = new StringBuilder(input.length());
    try {
      Encoding.encodeRcdataOnto(input, builder);
    } catch (IOException ioException) {
      // Unreachable code
      throw new UncheckedIOException("I/O Exception without I/O", ioException);
    }

    // The OWASP encoder leaves C1 escapes, deletes, and non-characters in the output. These are partially forbidden in HTML in that you cannot use a numeric
    // entity to represent them, but can insert them directly.
    //
    // I also want to prevent the use of CR to trigger a carriage return when the output is viewed as source. A carriage return can lead to some data being
    // hidden. Therefore, I canonicalize all new-line indicators ( CR, LF, CRLF ) to LF.

    // Fast check.
    int index = escapeFastCheck(builder);
    if (index == -1) {
      return builder.toString();
    }

    // need to fix something
    escapeFix(builder, index);
    return builder.toString();
  }


  private static int escapeFastCheck(StringBuilder builder) {
    final int length = builder.length();
    int index = 0;
    while (index < length) {
      int check = checkCharacter(builder, index);
      if (check <= 0) {
        return index;
      }
      index += check;
    }
    return -1;
  }


  private static void escapeFix(StringBuilder builder, int index) {
    final int length = builder.length();
    int out = index;
    while (index < length) {
      int check = checkCharacter(builder, index);
      switch (check) {
        case -1:
          index += 2;
          break;
        case 0:
          index++;
          break;
        case 1:
          builder.setCharAt(out, builder.charAt(index));
          index++;
          out++;
          break;
        case 2:
          // OWASP sanitizer replaces all supplemental code-points with numerical escapes, so this does not happen
          builder.setCharAt(out, builder.charAt(index));
          builder.setCharAt(out + 1, builder.charAt(index + 1));
          index += 2;
          out += 2;
          break;
        default:
          throw new IllegalStateException();
      }
    }

    builder.setLength(out);
  }


  /**
   * Escape HTML only if needed.
   *
   * @param input the HTML
   *
   * @return the sanitized HTML
   */
  public static String escapeOnce(String input) {
    if (input == null || input.isEmpty()) {
      return "";
    }

    return escape(Encoding.decodeHtml(input, false));
  }


  private static boolean isBadChar(char c) {
    // C1 codes
    if (0x7f <= c && c <= 0x9f) {
      return true;
    }

    // Non-characters
    if (0xfdd0 <= c && c <= 0xfdef) {
      return true;
    }
    if ((c == 0xfffe) || (c == 0xffff)) {
      return true;
    }

    // it is a good character
    return false;
  }


  /** New instance. */
  protected HTML() {
    // do nothing
  }

}
