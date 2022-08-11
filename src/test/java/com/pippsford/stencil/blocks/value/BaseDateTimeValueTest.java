package com.pippsford.stencil.blocks.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.source.MemorySourceProvider;

/**
 * @author Simon Greatrix on 29/12/2020.
 */
class BaseDateTimeValueTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void testFormatBasic() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, iso basic}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 20090213-0800", output);
  }


  @Test
  public void testFormatDate() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, iso-Date}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2009-02-13-08:00", output);
  }


  @Test
  public void testFormatError() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, zoned}");
    Map<String, Object> map = Map.of("var", Collections.emptyList());
    assertThrows(IllegalArgumentException.class, () -> stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map));
  }


  @Test
  public void testFormatInputCalendar() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, zoned}");
    Calendar c = Calendar.getInstance();
    c.setTimeZone(TimeZone.getTimeZone("Europe/London"));
    c.setTimeInMillis(1234567890123L);

    Map<String, Object> map = Map.of("var", c);
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2009-02-13T23:31:30.123Z[Europe/London]", output);
  }


  @Test
  public void testFormatInputDate() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, instant}");
    Map<String, Object> map = Map.of("var", new Date(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2009-02-13T23:31:30.123Z", output);
  }


  @Test
  public void testFormatInputLocalDate() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, zoned}");

    Map<String, Object> map = Map.of("var", LocalDate.of(2020, 7, 25));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2020-07-25T00:00:00-07:00[America/Los_Angeles]", output);
  }


  @Test
  public void testFormatInputLocalDateTime() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, zoned}");

    Map<String, Object> map = Map.of("var", LocalDateTime.of(2015, 5, 10, 20, 19, 18));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2015-05-10T20:19:18-07:00[America/Los_Angeles]", output);
  }


  @Test
  public void testFormatInputLocalTime() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, time, local_time}");

    Map<String, Object> map = Map.of("var", LocalTime.of(12, 11, 10));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 12:11:10", output);
  }


  @Test
  public void testFormatInputLong() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, instant}");
    Map<String, Object> map = Map.of("var", 1234567890123L);
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2009-02-13T23:31:30.123Z", output);
  }


  @Test
  public void testFormatInputNull() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, zoned}");

    Map<String, Object> map = new HashMap<>();
    map.put("var", null);
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = ", output);
  }


  @Test
  public void testFormatInputOffset() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, zoned}");

    Map<String, Object> map = Map.of("var", OffsetDateTime.of(2020, 10, 9, 8, 7, 6, 5, ZoneOffset.ofHours(-2)));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2020-10-09T08:07:06.000000005-02:00", output);
  }


  @Test
  public void testFormatInputZoned() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {no:var, datetime, zoned}");

    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L).atZone(ZoneId.of("Europe/Moscow")));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2009-02-14T02:31:30.123+03:00[Europe/Moscow]", output);
  }


  @Test
  public void testFormatInstant() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, instant}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2009-02-13T23:31:30.123Z", output);
  }


  @Test
  public void testFormatLocalDate() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, Local_Date}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2009-02-13", output);
  }


  @Test
  public void testFormatLocalTime() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, Local TIME}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 15:31:30.123", output);
  }


  @Test
  public void testFormatOffsetDate() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, OffsetDate}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2009-02-13-08:00", output);
  }


  @Test
  public void testFormatOffsetTime() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, iso_offset_time}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 15:31:30.123-08:00", output);
  }


  @Test
  public void testFormatOrdinal() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, ordinal}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2009-044-08:00", output);
  }


  @Test
  public void testFormatRFC() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, rfc}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = Fri, 13 Feb 2009 15:31:30 -0800", output);
  }


  @Test
  public void testFormatTime() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, time}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 15:31:30.123-08:00", output);
  }


  @Test
  public void testFormatWeekDate() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, weekdate}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2009-W07-5-08:00", output);
  }


  @Test
  public void testFormatZonedDateTime() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, datetime}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2009-02-13T15:31:30.123-08:00[America/Los_Angeles]", output);
  }


  @Test
  public void testFormatZonedTime() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {var, datetime, zoned}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("DT = 2009-02-13T15:31:30.123-08:00[America/Los_Angeles]", output);
  }

}