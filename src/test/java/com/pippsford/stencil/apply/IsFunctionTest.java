package com.pippsford.stencil.apply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.source.MemorySourceProvider;

/**
 * @author Simon Greatrix on 21/01/2021.
 */
class IsFunctionTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  private String doTest(Object o1, String op, Object o2) throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply b=is(o1,op,o2)]{b}");
    Map<String, Object> map = new HashMap<>();
    map.put("o1", o1);
    map.put("op", op);
    map.put("o2", o2);
    map.put("is", IsFunction.INSTANCE);
    return stencils.write("test.txt", Locale.ROOT, ZoneId.of("Europe/London"), map);
  }


  @Test
  public void testEq() throws IOException, StencilException {
    assertEquals("false", doTest("foo", "EQ", "bar"));
    assertEquals("true", doTest("foo", "EQ", "foo"));
    assertEquals("true", doTest("100", "EQ", "00100"));
    assertEquals("true", doTest("1E2", "EQ", "100.0"));
    assertEquals("true", doTest(null, "EQ", null));
  }


  @Test
  public void testErr() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply b=is(o1,o2)]");
    Map map = Map.of("is", IsFunction.INSTANCE);
    assertThrows(IllegalArgumentException.class, () -> stencils.write("test.txt", Locale.ROOT, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void testErr2() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply b=is(o1,o2,a,b)]{b}");
    Map map = Map.of("is", IsFunction.INSTANCE);
    assertThrows(IllegalArgumentException.class, () -> stencils.write("test.txt", Locale.ROOT, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void testErr3() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply b=is(o1,op,o2)]{b}");
    Map map = Map.of("is", IsFunction.INSTANCE, "op", "kettle");
    assertThrows(IllegalArgumentException.class, () -> stencils.write("test.txt", Locale.ROOT, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void testErr4() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply b=is(o1,op,o2)]{b}");
    Map map = Map.of("is", IsFunction.INSTANCE);
    assertThrows(IllegalArgumentException.class, () -> stencils.write("test.txt", Locale.ROOT, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void testGe() throws IOException, StencilException {
    assertEquals("true", doTest("foo", "GE", "bar"));
    assertEquals("true", doTest("foo", "GE", "foo"));
    assertEquals("false", doTest("100", "GE", "001001"));
    assertEquals("true", doTest(null, "GE", "null"));
    assertEquals("true", doTest("null", "GE", "null"));
  }


  @Test
  public void testGt() throws IOException, StencilException {
    assertEquals("true", doTest("foo", "GT", "bar"));
    assertEquals("false", doTest("foo", "GT", "foo"));
    assertEquals("false", doTest("100", "GT", "001001"));
    assertEquals("true", doTest(null, "GT", "null"));
    assertEquals("false", doTest("null", "GT", "null"));
  }


  @Test
  public void testLe() throws IOException, StencilException {
    assertEquals("false", doTest("foo", "LE", "bar"));
    assertEquals("true", doTest("foo", "LE", "foo"));
    assertEquals("true", doTest("100", "LE", "001001"));
    assertEquals("false", doTest(null, "LE", "null"));
    assertEquals("true", doTest("null", "LE", "null"));
  }


  @Test
  public void testLt() throws IOException, StencilException {
    assertEquals("false", doTest("foo", "LT", "bar"));
    assertEquals("false", doTest("foo", "LT", "foo"));
    assertEquals("true", doTest("100", "LT", "001001"));
    assertEquals("false", doTest(null, "LT", "null"));
    assertEquals("false", doTest("null", "LT", "null"));
  }


  @Test
  public void testNe() throws IOException, StencilException {
    assertEquals("true", doTest("foo", "NE", "bar"));
    assertEquals("false", doTest("foo", "NE", "foo"));
    assertEquals("false", doTest("100", "NE", "00100"));
    assertEquals("true", doTest(null, "NE", "null"));
  }

}