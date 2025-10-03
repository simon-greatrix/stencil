package com.pippsford.stencil.escape;

import static com.pippsford.stencil.escape.HTML.escape;
import static com.pippsford.stencil.escape.HTML.escapeOnce;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 02/01/2021.
 */
@SuppressWarnings("checkstyle:AvoidEscapedUnicodeCharacters")
class HTMLTest {

  private static final String SAFE_TEXT = "This is some safe text. Hello, World!\n£™€¢§¶≠“‘";


  @Test
  public void coverage() {
    assertNotNull(new HTML());
  }


  @Test
  public void safeAndNull() {
    assertEquals("", escape(null));
    assertEquals(SAFE_TEXT, escape(SAFE_TEXT));

    assertEquals("", escapeOnce(null));
    assertEquals(SAFE_TEXT, escapeOnce(SAFE_TEXT));
  }


  @Test
  public void testC0() {
    assertEquals("C0 \t IS GOOD", HTML.escape("C0 \t IS GOOD"));
    assertEquals("C0 \t IS GOOD", HTML.escapeOnce("C0 \t IS GOOD"));

    assertEquals("C0  IS BAD", HTML.escape("C0 \u001f\u0001 IS BAD"));
    assertEquals("C0  IS BAD", HTML.escapeOnce("C0 \u001f\u0001 IS BAD"));
  }


  @Test
  public void testC1() {
    assertEquals("C1  IS BAD", HTML.escape("C1 \u009f\u0081 IS BAD"));
    assertEquals("C1  IS BAD", HTML.escapeOnce("C1 \u009f\u0081 IS BAD"));
  }


  @Test
  public void testCRLF() {
    assertEquals("foo\nbar", escape("foo\r\nbar"));
    assertEquals("foo\nbar", escape("foo\nbar"));
    assertEquals("foo\nbar", escape("foo\rbar"));
    assertEquals("foo\n\nbar", escape("foo\n\rbar"));
  }


  @Test
  public void testDEL() {

    assertEquals("DEL  IS BAD", HTML.escapeOnce("DEL &#x7f; IS BAD"));
    assertEquals("DEL  IS BAD", HTML.escape("DEL \u007f IS BAD"));
    assertEquals("DEL  IS BAD", HTML.escapeOnce("DEL \u007f IS BAD"));
  }


  @Test
  public void testDangerous() {
    assertEquals("a &#43; &#61; &#39; &#34; &#96; &amp; &#64; b", HTML.escapeOnce("a + = ' \" ` & @ b"));
    assertEquals("a &#43; &#61; &#39; &#34; &#96; &amp; &#64; b", HTML.escape("a + = ' \" ` & @ b"));
    assertEquals("a &#43; &#61; &#39; &#34; &#96; &amp; &#64; b", HTML.escapeOnce("a &#43; &#61; &#39; &#34; &#96; &amp; &#64; b"));
  }


  @Test
  public void testNonCharacters() {
    assertEquals("NC  NC", HTML.escapeOnce("NC &#xfddd; NC"));
    assertEquals("NC  NC", HTML.escape("NC \ufddd NC"));
    assertEquals("NC  NC", HTML.escapeOnce("NC \ufddd NC"));
    assertEquals("DEL  IS BAD", HTML.escape("DEL \u007f IS BAD"));
    assertEquals("DEL  IS BAD", HTML.escapeOnce("DEL \u007f IS BAD"));

    assertEquals("NC  NC", HTML.escape("NC " + Character.toString(0xfffe) + " NC"));
  }


  @Test
  public void testQuote() {
    assertEquals("This is &#34;quoted text&#34;.", HTML.escape("This is \"quoted text\"."));
    assertEquals("This is &#34;quoted text&#34;.", HTML.escapeOnce("This is \"quoted text\"."));
    assertEquals("This is &amp;quot;quoted text&amp;#34;.", HTML.escape("This is &quot;quoted text&#34;."));
    assertEquals("This is &#34;quoted text&#34;.", HTML.escapeOnce("This is &quot;quoted text&#34;."));
  }


  @Test
  public void testSupplemental() {
    assertEquals("foo&#x23456;bar", escape("foo" + Character.toString(0x23456) + "bar"));
  }


  @Test
  @Disabled("""
      The OWASP sanitizer produces illegal HTML in this case.
      The HTML standard https://html.spec.whatwg.org/multipage/syntax.html#character-references says:
      
      The numeric character reference forms described above are allowed to reference any code point excluding U+000D CR, noncharacters, and controls other
      than ASCII whitespace.
      
      So non-characters are forbidden, but OWASP includes supplemental non-characters as numeric entities.
      """)
  public void testSupplementalNonCharacter() {
    assertEquals("NC  NC", HTML.escapeOnce("NC " + Character.toString(0x1fffe) + " NC"));
  }


  @Test
  public void testSurrogates() {
    assertEquals("SP &#x1f921; IS GOOD", HTML.escape("SP " + Character.toString(0x1f921) + " IS GOOD"));
    assertEquals("SP &#x1f921; IS GOOD", HTML.escapeOnce("SP " + Character.toString(0x1f921) + " IS GOOD"));

    // isolated surrogates are removed
    assertEquals("HS  IS BAD", HTML.escape("HS " + Character.highSurrogate(0x1f921) + " IS BAD"));
    assertEquals("LS  IS BAD", HTML.escape("LS " + Character.lowSurrogate(0x1f921) + " IS BAD"));
    assertEquals("HS  IS BAD", HTML.escapeOnce("HS " + Character.highSurrogate(0x1f921) + " IS BAD"));
    assertEquals("LS  IS BAD", HTML.escapeOnce("LS " + Character.lowSurrogate(0x1f921) + " IS BAD"));
  }


  @Test
  public void testTag() {
    assertEquals("This is &lt;quoted&gt; text.", HTML.escape("This is <quoted> text."));
    assertEquals("This is &lt;quoted&gt; text.", HTML.escapeOnce("This is <quoted> text."));

    // try with actual tag
    assertEquals("This is &lt;p&gt; text.", HTML.escape("This is <p> text."));
    assertEquals("This is &lt;p&gt; text.", HTML.escapeOnce("This is <p> text."));

    assertEquals("This is &amp;lt;quoted&amp;gt; text.", HTML.escape("This is &lt;quoted&gt; text."));
    assertEquals("This is &lt;quoted text&amp;#gt;.", HTML.escapeOnce("This is &lt;quoted text&#gt;."));
  }

}
