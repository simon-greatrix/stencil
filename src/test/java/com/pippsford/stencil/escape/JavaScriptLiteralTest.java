package com.pippsford.stencil.escape;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 11/01/2021.
 */
class JavaScriptLiteralTest {

  @Test
  public void sample() {
    assertEquals("", JavaScriptLiteral.JAVA.escape(null));
    assertEquals("abcd", JavaScriptLiteral.JAVA.escape("abcd"));
    assertEquals("ab\\ncd", JavaScriptLiteral.JAVA.escape("ab\ncd"));
    assertEquals("ab`cd", JavaScriptLiteral.JAVA.escape("ab`cd"));
    assertEquals("ab™cd", JavaScriptLiteral.JAVA.escape("ab™cd"));
    assertEquals("ab¤cd", JavaScriptLiteral.JAVA.escape("ab¤cd"));
    assertEquals("ab\\u2122cd", JavaScriptLiteral.JAVA_ASCII.escape("ab™cd"));
    assertEquals("ab\\u00a4cd", JavaScriptLiteral.JAVA_ASCII.escape("ab¤cd"));
  }


  @Test
  public void testAscii() {
    StringBuilder c0 = new StringBuilder();
    for (char c = ' '; c < (char) 0x80; c++) {
      c0.append(c);
    }
    assertEquals(
        " !\\\"#$%&\\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f",
        JavaScriptLiteral.JAVA.escape(c0.toString())
    );
    assertEquals(
        " !\\\"#$%&\\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\\u007f",
        JavaScriptLiteral.JAVA_ASCII.escape(c0.toString())
    );
  }


  @Test
  public void testBMP() {
    assertEquals("♠♡♢♣", JavaScriptLiteral.JAVA.escape("♠♡♢♣"));
    assertEquals("\\u2660\\u2661\\u2662\\u2663", JavaScriptLiteral.JAVA_ASCII.escape("♠♡♢♣"));
  }


  @Test
  public void testC0() {
    StringBuilder c0 = new StringBuilder();
    for (char c = '\0'; c < ' '; c++) {
      c0.append(c);
    }
    assertEquals(
        "\\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\\b\\t\\n\\u000b\\f\\r\\u000e\\u000f\\u0010\\u0011\\u0012\\u0013\\u0014\\u0015"
            + "\\u0016\\u0017\\u0018\\u0019\\u001a\\u001b\\u001c\\u001d\\u001e\\u001f",
        JavaScriptLiteral.JAVA.escape(c0.toString())
    );
    assertEquals(
        "\\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\\b\\t\\n\\u000b\\f\\r\\u000e\\u000f\\u0010\\u0011\\u0012\\u0013\\u0014\\u0015"
            + "\\u0016\\u0017\\u0018\\u0019\\u001a\\u001b\\u001c\\u001d\\u001e\\u001f",
        JavaScriptLiteral.JAVA_ASCII.escape(c0.toString())
    );
  }


  @Test
  public void testNonBMP() {
    assertEquals("🍿", JavaScriptLiteral.JAVA.escape("🍿"));
    assertEquals("\\ud83c\\udf7f", JavaScriptLiteral.JAVA_ASCII.escape("🍿"));
  }

}