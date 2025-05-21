package com.pippsford.stencil;

import static org.junit.jupiter.api.Assertions.*;

import com.pippsford.stencil.source.StencilNotFoundException;
import org.junit.jupiter.api.Test;
class SourceTest {

  @Test
  void testCleanPath() throws StencilNotFoundException {
    assertEquals("/foo", Source.cleanPath("/foo"));
    assertEquals("/foo", Source.cleanPath("foo"));
    assertEquals("/foo", Source.cleanPath("foo/"));
    assertEquals("/foo/bar", Source.cleanPath("foo//bat/.././bar/"));
    assertEquals("/foo/bar", Source.cleanPath("../..//foo//bat//../bar/"));
    assertThrows(StencilNotFoundException.class, () -> Source.cleanPath("/"));
    assertThrows(StencilNotFoundException.class, () -> Source.cleanPath(""));
    assertThrows(StencilNotFoundException.class, () -> Source.cleanPath(null));
  }
}
