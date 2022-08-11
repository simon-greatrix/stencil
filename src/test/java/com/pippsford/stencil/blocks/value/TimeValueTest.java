package com.pippsford.stencil.blocks.value;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.source.MemorySourceProvider;

/**
 * @author Simon Greatrix on 29/12/2020.
 */
class TimeValueTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void testDefault() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, time}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 23:31:30", output);
  }


  @Test
  public void testLong() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, time, LONG}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 23:31:30 GMT", output);
  }


  @Test
  public void testMedium() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, time, medium}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 23:31:30", output);
  }


  @Test
  public void testShort() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, time, short}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 23:31", output);
  }


  @Test
  public void testShort2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, time, short}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 11:31 PM", output);
  }

}