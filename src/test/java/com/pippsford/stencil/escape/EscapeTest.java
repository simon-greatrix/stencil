package com.pippsford.stencil.escape;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 02/01/2021.
 */
class EscapeTest {

  @Test
  public void testNull() {
    for (StandardEscape es : StandardEscape.values()) {
      assertEquals("", es.escape(null));
    }
  }


  @Test
  public void testSafe() {
    for (StandardEscape es : StandardEscape.values()) {
      assertEquals("This.is.safe.text", es.escape("This.is.safe.text"));
    }
  }

}