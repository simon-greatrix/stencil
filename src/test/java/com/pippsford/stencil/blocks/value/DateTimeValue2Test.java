package com.pippsford.stencil.blocks.value;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
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
class DateTimeValue2Test {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void testLongShort() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, long, short}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = February 13, 2009, 11:31 PM", output);
  }


  @Test
  public void testMediumLong() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, medium, long}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = Feb 13, 2009, 11:31:30 PM GMT", output);
  }


  @Test
  public void testShortShort() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, short, short}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 2/13/09, 11:31 PM", output);
  }


  @Test
  public void testShortShortNull() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, short, short}");
    Map<String, Object> map = new HashMap<>();
    map.put("var", null);
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = ", output);
  }

}