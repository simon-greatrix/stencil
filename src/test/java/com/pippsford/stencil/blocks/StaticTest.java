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
 * @author Simon Greatrix on 21/01/2021.
 */
class StaticTest {

  Map map = Map.of();

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void test1() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set]\n\n[set]");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("", output);
  }


  @Test
  public void test2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set]\n\n");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("", output);
  }


  @Test
  public void test3() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "\n\n[set]\n\n[set]\n\n");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("", output);
  }


  @Test
  public void test4() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "\n\n[set]\n\n[set]{var}\n\n");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("\n\n", output);
  }


  @Test
  public void test5() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "\n\n[set]\n {v} \n[set]{v}\n\n");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("\n  \n\n\n", output);
  }


  @Test
  public void test6() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "\n\n[set]\n {>>x}   {x}\n[set]{v}\n\n");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("\n    \n\n\n", output);
  }
}