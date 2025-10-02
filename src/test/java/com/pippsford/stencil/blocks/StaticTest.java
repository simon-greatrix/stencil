package com.pippsford.stencil.blocks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.source.MemorySourceProvider;
import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 21/01/2021.
 */
class StaticTest {

  Map map = Map.of();

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void test1() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set escape=url]\n\n[set escape=no]");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("", output);
  }


  @Test
  public void test2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set escape=no]\n\n");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("", output);
  }


  @Test
  public void test3() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "\n\n[set escape=none]\n\n[set bundle=foo]\n\n");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("", output);
  }


  @Test
  public void test4() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "\n\n[set escape=html_safe]\n\n[set bundle=bim]{var}\n\n");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("\n\n", output);
  }


  @Test
  public void test5() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "\n\n[set escape=java]\n {v} \n[set escape=no]{v}\n\n");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("\n  \n\n\n", output);
  }


  @Test
  public void test6() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "\n\n[set escape=url]\n {>>x}   {x}\n[set bundle=bim]{v}\n\n");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("\n +++\n\n\n", output);
  }

  @Test
  public void test7() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "\n\n[set escape=url]\n {no: >>x}   {x}\n[set bundle=bim]{v}\n\n");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("\n    \n\n\n", output);
  }
}
