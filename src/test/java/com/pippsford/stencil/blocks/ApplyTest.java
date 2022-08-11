package com.pippsford.stencil.blocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.source.MemorySourceProvider;
import com.pippsford.stencil.apply.LocalizedValueProcessor;
import com.pippsford.stencil.apply.ValueProcessor;

/**
 * @author Simon Greatrix on 05/01/2021.
 */
class ApplyTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void test1() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply name = foo('John')]Hello {name}");
    Map<String, Object> map = Map.of(
        "var", Instant.ofEpochMilli(1234567890123L),
        "foo", (ValueProcessor) (a, z) -> {
          return z[0].asString();
        }
    );
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Hello John", output);
  }


  @Test
  public void test2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply name=foo('John')]Hello {name}");
    Map<String, Object> map = Map.of(
        "var", Instant.ofEpochMilli(1234567890123L),
        "foo", (LocalizedValueProcessor) (d, l, z, v) -> {
          return v[0].asString() + " for Locale " + l + " in zone " + z;
        }
    );
    String output = stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Hello John for Locale en_GB in zone Europe/London", output);
  }


  @Test
  public void test3()  {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply foo]Hello {name}");
    Map<String, Object> map = Map.of(
        "var", Instant.ofEpochMilli(1234567890123L),
        "foo", "not a function"
    );
    assertThrows(IllegalStateException.class, () -> stencils.write("test.txt", Locale.UK, ZoneId.of("Europe/London"), map));
  }

}