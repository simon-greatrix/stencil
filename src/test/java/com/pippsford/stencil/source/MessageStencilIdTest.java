package com.pippsford.stencil.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.escape.StandardEscape;

/**
 * @author Simon Greatrix on 12/01/2021.
 */
class MessageStencilIdTest {

  @Test
  public void test() {
    MessageStencilId i1 = new MessageStencilId("b", "n1", Locale.ENGLISH, StandardEscape.NO_ESCAPE);
    MessageStencilId i2 = new MessageStencilId("b", "n2", Locale.ENGLISH, StandardEscape.NO_ESCAPE);

    assertEquals(i1, i1);
    assertNotEquals(i1, i2);
    assertFalse(i1.equals(null));
    assertFalse(i1.equals(""));
  }

}