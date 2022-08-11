package com.pippsford.stencil.blocks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.source.MemorySourceProvider;

/**
 * @author Simon Greatrix on 06/01/2021.
 */
class UseDirectiveTest {

  Pojo pojo1 = new Pojo("Dr", "Karl", 46);

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void test1() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[use p1]{job} : {title} {name} {age}[else]DETAILS MISSING for {job}[end]");
    Map<String, Object> map = Map.of("p1", pojo1, "job", "Prof");
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("Prof : Dr Karl 46", output);
  }


  @Test
  public void test2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[use p1]{job} : {title} {name} {age}[else]DETAILS MISSING for {job}[end]");
    Map<String, Object> map = Map.of("job", "Prof");
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("DETAILS MISSING for Prof", output);
  }

}