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
class FormatValueTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void testFormatBasic() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Birthday: {var, format, %1$tb %1$te, %1$tY}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L).atZone(ZoneId.of("GMT")));

    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("Birthday: Feb 13, 2009", output);
  }


  @Test
  public void testFormatBasic2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Columns: |{var1, format, %6d}|{var2, format, %6s}|{var3, format, %6.3f}|");
    Map<String, Object> map = Map.of("var1", 4321, "var2", "Hello", "var3", 6.5);

    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("Columns: |  4321| Hello| 6.500|", output);
  }


  @Test
  public void testFormatHere() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Birthday: {var, format, >> !!}%1$tb %1$te, %1$tY{!!}");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L).atZone(ZoneId.of("GMT")));

    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("Birthday: Feb 13, 2009", output);
  }


  @Test
  public void testFormatNull() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Columns: |{var1, format, %6d}|");
    HashMap<String, Object> map = new HashMap<>();
    map.put("var1", null);
    String output = stencils.write("test.txt", Locale.ENGLISH, ZoneId.of("America/Los_Angeles"), map);
    assertEquals("Columns: ||", output);
  }

}