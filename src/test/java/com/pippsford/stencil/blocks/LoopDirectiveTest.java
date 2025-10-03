package com.pippsford.stencil.blocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.parser.StencilParseFailedException;
import com.pippsford.stencil.source.MemorySourceProvider;

/**
 * @author Simon Greatrix on 06/01/2021.
 */
class LoopDirectiveTest {

  Pojo pojo1 = new Pojo("Dr", "Karl", 46);

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void test1() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[loop p1]{value.title} {value.name} {value.age}[else]DETAILS MISSING[end]");
    Map<String, Object> map = Map.of("name", "Karl", "title", "Dr");
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("DETAILS MISSING", output);
  }


  @Test
  public void test2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[loop p1]{index}: {value.title} {value.name} {value.age}[else]DETAILS MISSING[end]");
    Map<String, Object> map = Map.of("p1", pojo1);
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("0: Dr Karl 46", output);
  }


  @Test
  public void test3() throws StencilException {
    pojo1.setQualifications(List.of("a100", "x200", "ztec"));
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[loop p1.qualifications]{index}/{size}: {value}{noop}\n[else]DETAILS MISSING\n[end]");
    Map<String, Object> map = Map.of("p1", pojo1);
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals(
        """
            0/3: a100{noop}
            1/3: x200{noop}
            2/3: ztec{noop}
            """, output);
  }


  @Test
  public void test4() throws StencilException {
    pojo1.setQualifications(List.of("a100", "x200", "ztec"));
    sourceProvider.putFile(Locale.ROOT, "test.txt", "{p1.qualifications.2} {p1.qualifications.size} "
        + "f={p1.qualifications.fribble} {p1.qualifications.isEmpty}");
    Map<String, Object> map = Map.of("p1", pojo1);
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("ztec 3 f={p1.qualifications.fribble} false", output);
  }


  @Test
  public void test5() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[loop p1.qualifications]{index}/{size}: {value}{noop}\n[else]DETAILS MISSING\n[end]");
    Map<String, Object> map = Map.of("p1", pojo1);
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("DETAILS MISSING\n", output);
  }


  @Test
  public void test6() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[loop array]{index}/{size}: {value}\n[else]DETAILS MISSING\n[end]");
    Map<String, Object> map = Map.of("array", new int[]{4, 5, 6, 7});
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals(
        """
            0/4: 4
            1/4: 5
            2/4: 6
            3/4: 7
            """, output);
  }


  @Test
  public void test7() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[loop array]{index}/{size}: {value}\n[else]DETAILS MISSING\n[end]");
    Map<String, Object> map = Map.of("array", new int[0]);
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("DETAILS MISSING\n", output);
  }


  @Test
  void test8() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "Hello [loop title]{title}\n[use foo][else][end]{name}.");
    Map<String, Object> map = Map.of("name", "Karl");
    assertThrows(StencilParseFailedException.class, () -> stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test9() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[loop m.p1]{index}:{size}:{key}:{value}:{foo}\n[end]");
    Map<String, Object> map = Map.of("m", Map.of("p1", new TreeMap<>(Map.of("a", "b", "c", "d"))), "foo", "z");
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals(
        """
            0:2:a:b:z
            1:2:c:d:z
            """, output);
  }

}
