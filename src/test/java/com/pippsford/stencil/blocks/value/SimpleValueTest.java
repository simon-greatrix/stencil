package com.pippsford.stencil.blocks.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.parser.StencilParseFailedException;
import com.pippsford.stencil.source.MemorySourceProvider;

/**
 * @author Simon Greatrix on 29/12/2020.
 */
class SimpleValueTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void test1() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = { var }");
    Map<String, Object> map = Map.of("var", "Hello, World!");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = Hello, World!", output);
  }


  @Test
  public void test10() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set escape=ecma]DT = {html_strict:var}");
    Map<String, Object> map = Map.of("var", "&lt;b&gt;Hello, World!&lt;b&gt;");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = &amp;lt;b&amp;gt;Hello, World!&amp;lt;b&amp;gt;", output);
  }


  @Test
  public void test11() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set escape=bad]DT = {html_strict:var}");
    Map<String, Object> map = Map.of("var", "&lt;b&gt;Hello, World!&lt;b&gt;");
    assertThrows(StencilParseFailedException.class, () -> stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test12() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set version=1]DT = {html_strict:var}");
    Map<String, Object> map = Map.of("var", "&lt;b&gt;Hello, World!&lt;b&gt;");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = &amp;lt;b&amp;gt;Hello, World!&amp;lt;b&amp;gt;", output);
  }


  @Test
  public void test13() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set foo=bar]DT = {html_strict:var}");
    Map<String, Object> map = Map.of("var", "&lt;b&gt;Hello, World!&lt;b&gt;");
    assertThrows(StencilParseFailedException.class, () -> stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 2009-02-13T23:31:30.123Z", output);
  }


  @Test
  public void test3() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = { var}");
    Map<String, Object> map = Map.of();
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = { var}", output);
  }


  @Test
  public void test4() throws StencilException, IOException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {HTML.SAFE:var}");
    Map<String, Object> map = Map.of("var", "<b>Hello, World!<b>");
    StringWriter writer = new StringWriter();
    stencils.write("test.txt", writer, map);
    assertEquals("DT = &lt;b&gt;Hello, World!&lt;b&gt;", writer.toString());
  }


  @Test
  public void test5() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {html.safe:var}");
    Map<String, Object> map = Map.of("var", "&lt;b&gt;Hello, World!&lt;b&gt;");
    String output = stencils.write("test.txt", map);
    assertEquals("DT = &lt;b&gt;Hello, World!&lt;b&gt;", output);
  }


  @Test
  public void test6() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {html.strict:var}");
    Map<String, Object> map = Map.of("var", "&lt;b&gt;Hello, World!&lt;b&gt;");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = &amp;lt;b&amp;gt;Hello, World!&amp;lt;b&amp;gt;", output);
  }


  @Test
  public void test7() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {NO_escape:var}");
    Map<String, Object> map = Map.of("var", "&lt;b&gt;Hello, World!&lt;b&gt;");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = &lt;b&gt;Hello, World!&lt;b&gt;", output);
  }


  @Test
  public void test8() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {foo:var}");
    Map<String, Object> map = Map.of("var", "&lt;b&gt;Hello, World!&lt;b&gt;");
    assertThrows(StencilParseFailedException.class, () -> stencils.write("test.txt", map));
  }


  @Test
  public void test9() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[set escape=html.strict]DT = {var}");
    Map<String, Object> map = Map.of("var", "&lt;b&gt;Hello, World!&lt;b&gt;");
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = &amp;lt;b&amp;gt;Hello, World!&amp;lt;b&amp;gt;", output);
  }

  @Test
  public void test14() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {cause.class} / {LOG : cause.message}");
    Map<String, Object> map = Map.of("cause", new IllegalArgumentException("Test Failure"));
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = class java.lang.IllegalArgumentException / Test Failure", output);
  }
}
