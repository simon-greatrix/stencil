package com.pippsford.stencil.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;

/**
 * @author Simon Greatrix on 12/01/2021.
 */
class BundleSourceProviderTest {

  BundleSourceProvider provider = new BundleSourceProvider("test_stencils");


  @Test
  public void coverage() {
    assertNotNull(provider.toString());
  }


  @Test
  public void test2() {
    Stencils stencils = new Stencils(provider);
    Map<String, Object> map = Map.of(
        "title", "Mr",
        "name", "Frobisher"
    );
    assertThrows(StencilNotFoundException.class, () -> stencils.write("/unknown.txt", Locale.UK, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test3() throws StencilException {
    Stencils stencils = new Stencils(provider);
    Map<String, Object> map = Map.of(
        "title", "Mr",
        "name", "Duckworth"
    );
    String output = stencils.write("/test/file1.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Mr Duckworth!\n(C) Copyright SETL", output);
  }


  @Test
  public void test4() throws StencilException {
    Stencils stencils = new Stencils(provider);
    Map<String, Object> map = Map.of(
        "title", "Mr",
        "name", "Hamster"
    );
    String output = stencils.write("/test/file1.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Mr Hamster!\n(C) Copyright SETL", output);
  }

}