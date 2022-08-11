package com.pippsford.stencil.apply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.source.MemorySourceProvider;

/**
 * @author Simon Greatrix on 12/01/2021.
 */
class AugmentedIndexTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void test1() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[loop list]{isFirst},{isLast},{isOdd},{isEven},{index1}:{index}={value}\n[end]");
    Map map = Map.of("list", new int[]{5, 6, 7, 8});
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals(",,,,:0=5\n"
        + ",,,,:1=6\n"
        + ",,,,:2=7\n"
        + ",,,,:3=8\n", output);
  }


  @Test
  public void test10() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply F.index('$','400000000000000000')]");
    stencils.setDefaultValue("F.index", AugmentedIndex.INSTANCE);
    Map map = Map.of("value", "foo", "index", "1", "size", 4);
    assertThrows(IllegalArgumentException.class, () -> stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[loop list][apply F.index]{isFirst},{isLast},{isOdd},{isEven},{index1}:{index}={value}\n[end]");
    stencils.setDefaultValue("F.index", AugmentedIndex.INSTANCE);
    Map map = Map.of("list", new int[]{5, 6, 7, 8});
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("true,false,false,true,1:0=5\n"
        + "false,false,true,false,2:1=6\n"
        + "false,false,false,true,3:2=7\n"
        + "false,true,true,false,4:3=8\n", output);
  }


  @Test
  public void test3() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[loop list][apply F.index('$')]{$isFirst},{$isLast},{$isOdd},{$isEven},{$index1}:{index}={value}\n[end]");
    stencils.setDefaultValue("F.index", AugmentedIndex.INSTANCE);
    Map map = Map.of("list", new int[]{5, 6, 7, 8});
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("true,false,false,true,1:0=5\n"
        + "false,false,true,false,2:1=6\n"
        + "false,false,false,true,3:2=7\n"
        + "false,true,true,false,4:3=8\n", output);
  }


  @Test
  public void test4() throws StencilException {
    sourceProvider.putFile(
        Locale.ROOT,
        "test.txt",
        "[loop list][apply F.index('$','4')]{$isFirstOnPage},{$isLastOnPage},{$pageRow},{$pageNumber},{$pageCount}:{index}={value}\n[end]"
    );
    stencils.setDefaultValue("F.index", AugmentedIndex.INSTANCE);
    Map map = Map.of("list", new int[]{5, 6, 7, 8, 9, 10, 11, 12});
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("true,false,1,1,2:0=5\n"
        + "false,false,2,1,2:1=6\n"
        + "false,false,3,1,2:2=7\n"
        + "false,true,4,1,2:3=8\n"
        + "true,false,1,2,2:4=9\n"
        + "false,false,2,2,2:5=10\n"
        + "false,false,3,2,2:6=11\n"
        + "false,true,4,2,2:7=12\n", output);
  }


  @Test
  public void test5() throws StencilException {
    sourceProvider.putFile(
        Locale.ROOT,
        "test.txt",
        "[loop list][apply F.index('$','4')]{$isFirstOnPage},{$isLastOnPage},{$pageRow},{$pageNumber},{$pageCount}:{index}={value}\n[end]"
    );
    stencils.setDefaultValue("F.index", AugmentedIndex.INSTANCE);
    Map map = Map.of("list", new int[]{5, 6, 7, 8, 9, 10, 11, 12, 13, 14});
    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("true,false,1,1,3:0=5\n"
        + "false,false,2,1,3:1=6\n"
        + "false,false,3,1,3:2=7\n"
        + "false,true,4,1,3:3=8\n"
        + "true,false,1,2,3:4=9\n"
        + "false,false,2,2,3:5=10\n"
        + "false,false,3,2,3:6=11\n"
        + "false,true,4,2,3:7=12\n"
        + "true,false,1,3,3:8=13\n"
        + "false,true,2,3,3:9=14\n", output);
  }


  @Test
  public void test6() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply F.index('$','4')]");
    stencils.setDefaultValue("F.index", AugmentedIndex.INSTANCE);
    Map map = Map.of("value", "foo", "size", 4);
    assertThrows(IllegalArgumentException.class, () -> stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test7() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply F.index('$','4')]");
    stencils.setDefaultValue("F.index", AugmentedIndex.INSTANCE);
    Map map = Map.of("value", "foo", "index", "boggle", "size", 4);
    assertThrows(IllegalArgumentException.class, () -> stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test8() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply F.index('$','4')]");
    stencils.setDefaultValue("F.index", AugmentedIndex.INSTANCE);
    Map map = Map.of("value", "foo", "index", "1", "size", "boggle");
    assertThrows(IllegalArgumentException.class, () -> stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test9() {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply F.index(,'dood')]");
    stencils.setDefaultValue("F.index", AugmentedIndex.INSTANCE);
    Map map = Map.of("value", "foo", "index", "1", "size", 4);
    assertThrows(IllegalArgumentException.class, () -> stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map));
  }

}