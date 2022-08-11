package com.pippsford.stencil.blocks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.source.MemorySourceProvider;

/**
 * @author Simon Greatrix on 06/01/2021.
 */
class TemplateTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  void test1() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Hello [include name.txt].");
    sourceProvider.putFile(Locale.ROOT, "name.txt", "{title} {name}");
    Map<String, Object> map = Map.of("name", "Karl", "title", "Dr");
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Dr Karl.", output);
  }


  @Test
  void test2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Hello [include /name.txt].");
    sourceProvider.putFile(Locale.ROOT, "name.txt", "{title} {name}");
    Map<String, Object> map = Map.of("name", "Karl", "title", "Dr");
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Dr Karl.", output);
  }


  @Test
  void test3() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Hello [include /aa/../name.txt].");
    sourceProvider.putFile(Locale.ROOT, "name.txt", "{title} {name}");
    Map<String, Object> map = Map.of("name", "Karl", "title", "Dr");
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Dr Karl.", output);
  }


  @Test
  void test4() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "aa/cc/test.txt", "Hello [include ../bb/name.txt].");
    sourceProvider.putFile(Locale.ROOT, "aa/bb/name.txt", "{title} {name}");
    Map<String, Object> map = Map.of("name", "Karl", "title", "Dr");
    String output = stencils.write("aa/cc/test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Dr Karl.", output);
  }

}