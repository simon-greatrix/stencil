package com.pippsford.stencil.blocks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.source.MemorySourceProvider;
import org.junit.jupiter.api.Test;

class GlobalsTest {

  Map map = Map.of(
      "x","\"x\"",
      "y","y",
      "a", "&",
      "p", "%");

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);

  @Test
  public void test1() throws StencilException {
    // Global should change the escape prior to processing
    sourceProvider.putFile(Locale.ROOT, "test.txt", "{x}[global escape=ecma]{y}");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("\\\"x\\\"y", output);
  }

  @Test
  public void test2() throws StencilException {
    // Global can be ignored
    sourceProvider.putFile(Locale.ROOT, "test.txt", "{x}[global  ignore ignore escape=ecma]{y}");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("&#34;x&#34;[global ignore escape=ecma]y", output);
  }

  @Test
  public void test3() throws StencilException {
    // Inverted mode
    sourceProvider.putFile(Locale.ROOT, "test.txt", "x={x}[global mode=inverted] y={y} | {none: >>EOT}x={x} y={y}{EOT}");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("x={x} y={y} | x=\"x\" y=y", output);
  }

  @Test
  public void test4() throws StencilException {
    // Last mode wins
    sourceProvider.putFile(Locale.ROOT, "test.txt", "x={x} [global mode=inverted] y={y} | {none: >>EOT}x={x} y={y} {EOT} [global mode=normal]");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("x=&#34;x&#34;  y=y | x={x} y={y}  ", output);
  }


  @Test
  public void test5() throws StencilException {
    // set is preserved between blocks
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[global mode=inverted]{a}{none: >>EOT}{a}{p} [set escape=url]{p}{EOT} {>>EOT}{a}{p}{EOT}");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("{a}&% %25 %26%25", output);
  }
}
