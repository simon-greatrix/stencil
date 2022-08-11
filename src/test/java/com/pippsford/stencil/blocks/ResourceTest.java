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
import com.pippsford.stencil.source.StencilNotFoundException;

/**
 * @author Simon Greatrix on 05/01/2021.
 */
class ResourceTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void test1() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set bundle = test_messages][{hello}]");
    Map<String, Object> map = Map.of("name", "Karl");
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("(FR) Bonjour Karl.", output);
  }


  @Test
  public void test2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set bundle= wrong_resource][{test_messages, hello}]");
    Map<String, Object> map = Map.of("name", "Karl");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("(ROOT) Hello Karl", output);
  }


  @Test
  public void test3() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set bundle=wrong_resource][{test_messages, hello}]");
    Map<String, Object> map = Map.of("name", "Karl");
    assertThrows(StencilParseFailedException.class, () -> stencils.write("test.txt", Locale.GERMANY, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test4() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set bundle =wrong_resource][{hello}]");
    Map<String, Object> map = Map.of("name", "Karl");
    assertThrows(StencilNotFoundException.class, () -> stencils.write("test.txt", Locale.GERMANY, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test5() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set bundle=test_messages][{wrong_key}]");
    Map<String, Object> map = Map.of("name", "Karl");
    assertThrows(StencilNotFoundException.class, () -> stencils.write("test.txt", Locale.GERMANY, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test6() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[{wrong_key}]");
    Map<String, Object> map = Map.of("name", "Karl");
    assertThrows(StencilParseFailedException.class, () -> stencils.write("test.txt", Locale.GERMANY, ZoneId.of("Europe/London"), map));
  }

}