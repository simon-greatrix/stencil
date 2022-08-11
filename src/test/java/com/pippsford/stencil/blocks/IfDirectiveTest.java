package com.pippsford.stencil.blocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.parser.StencilParseFailedException;
import com.pippsford.stencil.source.MemorySourceProvider;

/**
 * @author Simon Greatrix on 05/01/2021.
 */
class IfDirectiveTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  void test1() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Hello [if title]{title} [end]{name}.");
    Map<String, Object> map = Map.of("name", "Karl", "title", "Dr");
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Dr Karl.", output);
  }


  @Test
  void test2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Hello [if title]{title} [else]#[end]{name}.");
    Map<String, Object> map = Map.of("name", "Karl");
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("Hello #Karl.", output);
  }


  @Test
  void test3()  {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Hello [if title]{title}\n[use foo][else][else][end] [else]#[end]{name}.");
    Map<String, Object> map = Map.of("name", "Karl");
    assertThrows(StencilParseFailedException.class, () -> stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map));
  }


  @Test
  void test4() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Hello [if title]{title}\n[use foo][else][end]{name}.");
    Map<String, Object> map = Map.of("name", "Karl");
    assertThrows(StencilParseFailedException.class, () -> stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map));
  }


  @Test
  void test5()  {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Hello [[end]{name}.");
    Map<String, Object> map = Map.of("name", "Karl");
    assertThrows(StencilParseFailedException.class, () -> stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map));
  }

}