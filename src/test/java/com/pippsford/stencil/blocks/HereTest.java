package com.pippsford.stencil.blocks;

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
 * @author Simon Greatrix on 30/12/2020.
 */
public class HereTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void testDefault() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "DT = {>>!!}[>>!!]Hello [if][else] {fribble}*}[!!]{!!} World");
    Map<String, Object> map = Map.of("var", Instant.ofEpochMilli(1234567890123L));
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("DT = [&gt;&gt;!!]Hello [if][else] {fribble}*}[!!] World", output);
  }

}
