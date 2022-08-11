package com.pippsford.stencil.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import com.pippsford.stencil.SourceProvider;
import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 07/01/2021.
 */
class ClassPathSourceProviderTest {

  @Test
  public void coverage() {
    assertNotNull(new ClassPathSourceProvider(null, Stencils.class).toString());
  }


  @Test
  public void test1() throws StencilException {
    SourceProvider provider = new ClassPathSourceProvider("/com/pippsford/stencil/test/");
    Stencils stencils = new Stencils(provider);
    Map<String, Object> map = Map.of(
        "title", "Mr",
        "name", "Frobisher"
    );
    String output = stencils.write("file1.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Mr Frobisher!\n(C) Copyright SETL", output);
  }


  @Test
  public void test2() {
    SourceProvider provider = new ClassPathSourceProvider("/com/pippsford/stencil/test/");
    Stencils stencils = new Stencils(provider);
    Map<String, Object> map = Map.of(
        "title", "Mr",
        "name", "Frobisher"
    );
    assertThrows(StencilNotFoundException.class, () -> stencils.write("unknown.txt", Locale.UK, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test3() throws StencilException {
    SourceProvider provider = new ClassPathSourceProvider(Stencils.class);
    Stencils stencils = new Stencils(provider);
    Map<String, Object> map = Map.of(
        "title", "Mr",
        "name", "Duckworth"
    );
    String output = stencils.write("test/file1.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Mr Duckworth!\n(C) Copyright SETL", output);
  }


  @Test
  public void test4() throws StencilException {
    SourceProvider provider = new ClassPathSourceProvider(null, Stencils.class);
    Stencils stencils = new Stencils(provider);
    Map<String, Object> map = Map.of(
        "title", "Mr",
        "name", "Hamster"
    );
    String output = stencils.write("test/file1.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Mr Hamster!\n(C) Copyright SETL", output);
  }

}