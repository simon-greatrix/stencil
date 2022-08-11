package com.pippsford.stencil.escape;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.parser.StencilParseFailedException;

/**
 * @author Simon Greatrix on 12/01/2021.
 */
class EscapeResolverTest {

  static final TestEscape TEST = new TestEscape();

  static class TestEscape implements Escape {

    int handlerCallCount = 0;

    @Override
    public String escape(String input) {
      throw new UnsupportedOperationException("Just for testing");
    }

    @Override
    public boolean isHandlerFor(String name) {
      handlerCallCount++;
      return "rot13".equalsIgnoreCase(name);
    }

  }


  @Test
  void register() throws StencilParseFailedException {
    EscapeResolver escapeResolver = new EscapeResolver();
    assertThrows(StencilParseFailedException.class, () -> escapeResolver.forName("rot13", StandardEscape.NO_ESCAPE));
    escapeResolver.registerEscapeStyle(TEST);
    int hcc = TEST.handlerCallCount;
    assertSame(TEST, escapeResolver.forName("rot13", StandardEscape.NO_ESCAPE));
    assertSame(TEST, escapeResolver.forName("rot13", StandardEscape.NO_ESCAPE));
    assertSame(TEST, escapeResolver.forName("ROT13", StandardEscape.NO_ESCAPE));
    assertSame(TEST, escapeResolver.forName("Rot13", StandardEscape.NO_ESCAPE));
    assertSame(TEST, escapeResolver.forName("ROT13", StandardEscape.NO_ESCAPE));
    assertEquals(hcc + 3, TEST.handlerCallCount);
  }


  @Test
  void testDefaults() throws StencilParseFailedException {
    EscapeResolver escapeResolver = new EscapeResolver();
    for (StandardEscape es : StandardEscape.values()) {
      assertSame(es, escapeResolver.forName(es.name(), TEST));
      assertSame(es, escapeResolver.forName(es.name().toLowerCase(Locale.ROOT), TEST));
    }
  }

}