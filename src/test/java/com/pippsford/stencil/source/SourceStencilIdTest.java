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
class SourceStencilIdTest {

  @Test
  public void test() throws StencilNotFoundException {
    MemorySourceProvider provider1 = new MemorySourceProvider("foo");

    SourceStencilId i1 = new SourceStencilId(provider1, "n1", Locale.ENGLISH, null, StandardEscape.NO_ESCAPE);
    SourceStencilId i2 = new SourceStencilId(provider1, "n2", Locale.ENGLISH, null, StandardEscape.NO_ESCAPE);

    assertEquals(i1, i1);
    assertNotEquals(i1, i2);
    assertFalse(i1.equals(null));
    assertFalse(i1.equals(""));
  }

}