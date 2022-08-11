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
class DateTimeValueTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void testDefault() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = Feb 13, 2009, 11:31:30 PM", output);
  }


  @Test
  public void testFormat() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, EEEE, HH:mm:ss.SSS 'Q'qq GGGG}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = Friday, 23:31:30.123 Q01 Anno Domini", output);
  }


  @Test
  public void testFormatHere() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, >> xx}EEEE, HH:mm:ss.SSS 'Q'qq GGGG{xx}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = Friday, 23:31:30.123 Q01 Anno Domini", output);
  }


  @Test
  public void testFull() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, Full}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = Friday, February 13, 2009 at 11:31:30 PM Greenwich Mean Time", output);
  }


  @Test
  public void testLong() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, LONG}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = February 13, 2009 at 11:31:30 PM GMT", output);
  }


  @Test
  public void testMedium() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, medium}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = Feb 13, 2009, 11:31:30 PM", output);
  }


  @Test
  public void testShort() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, short}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("Europe/London"), map);
    assertEquals("DT = 2/13/09, 11:31 PM", output);
  }

}