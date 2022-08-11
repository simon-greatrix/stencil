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
class DateValueTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void testDefault() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, date}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 13 Feb 2009", output);
  }


  @Test
  public void testLong() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, date, LONG}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 13 February 2009", output);
  }


  @Test
  public void testMedium() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, date, medium}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 13 Feb 2009", output);
  }


  @Test
  public void testShort() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, date, short}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 13/02/2009", output);
  }


  @Test
  public void testShort2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, date, short}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 2/13/09", output);
  }

}