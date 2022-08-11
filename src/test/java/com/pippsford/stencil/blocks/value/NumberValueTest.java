package com.pippsford.stencil.blocks.value;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.source.MemorySourceProvider;

/**
 * @author Simon Greatrix on 29/12/2020.
 */
class NumberValueTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void testCurrency() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Value = {var, number, currency}");
    Map<String, Object> map = Map.of("var", 123.456);
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Value = £123.46", output);
  }


  @Test
  public void testDecimal() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Value = {var, number, number}");
    Map<String, Object> map = Map.of("var", 123.456);
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Value = 123.456", output);
  }


  @Test
  public void testDefault() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Value = {var, number}");
    Map<String, Object> map = Map.of("var", 123.456);
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Value = 123.456", output);
  }


  @Test
  public void testFormat() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Value = {var, number, #,##0.00 ¤;(#,##0.00) ¤}");
    Map<String, Object> map = Map.of("var", 123.456);
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Value = 123.46 £", output);
  }


  @Test
  public void testFormatHere() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Value = {var, number, >>xx}#,##0.00 ¤;(#,##0.00) ¤{xx}");
    Map<String, Object> map = Map.of("var", 123.456);
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Value = 123.46 £", output);
  }


  @Test
  public void testInteger() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Value = {var, number, integer}");
    Map<String, Object> map = Map.of("var", 123.456);
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Value = 123", output);
  }


  @Test
  public void testNull() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Value = {var, number}");
    Map<String, Object> map = new HashMap<>();
    map.put("var", null);
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Value = ", output);
  }


  @Test
  public void testNumber() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Value = {var, number, number}");
    Map<String, Object> map = Map.of("var", 123.456);
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Value = 123.456", output);
  }


  @Test
  public void testPercent() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Value = {var, number, percent}");
    Map<String, Object> map = Map.of("var", 123.456);
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Value = 12,346%", output);
  }

}