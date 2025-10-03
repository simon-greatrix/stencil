package com.pippsford.stencil.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntConsumer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.apply.ForFunction;
import com.pippsford.stencil.source.MemorySourceProvider;

/**
 * @author Simon Greatrix on 21/02/2021.
 */
class IndexableIntTest {

  private List<Integer> doLoad(IndexableInt ii) {
    ArrayList<Integer> list = new ArrayList<>();
    int s = ii.size();
    for (int i = 0; i < s; i++) {
      list.add(ii.apply(i));
    }
    return list;
  }


  @Test
  public void test1() {
    IndexableInt ii = new IndexableInt(1, 10, 2);
    List<Integer> loaded = doLoad(ii);
    assertEquals(List.of(1, 3, 5, 7, 9), loaded);
  }


  @Test
  public void test2() {
    IndexableInt ii = new IndexableInt(10, 1, 2);
    List<Integer> loaded = doLoad(ii);
    assertEquals(List.of(), loaded);
    assertEquals(10, ii.getStart());
    assertEquals(1, ii.getEnd());
    assertEquals(2, ii.getStep());
  }


  @Test
  public void test3() {
    IndexableInt ii = new IndexableInt(5, 1, -1);
    List<Integer> loaded = doLoad(ii);
    assertEquals(List.of(5, 4, 3, 2), loaded);
  }


  @Test
  public void test3b() {
    IndexableInt ii = new IndexableInt(5, 1);
    List<Integer> loaded = doLoad(ii);
    assertEquals(List.of(5, 4, 3, 2), loaded);
    assertEquals(5, ii.getStart());
    assertEquals(1, ii.getEnd());
    assertEquals(-1, ii.getStep());
  }


  @Test
  public void test3c() {
    IndexableInt ii = new IndexableInt(1, 5);
    List<Integer> loaded = doLoad(ii);
    assertEquals(List.of(1, 2, 3, 4), loaded);
  }


  @Test
  public void test4() {
    IndexableInt ii = new IndexableInt(8, 100, 17);
    List<Integer> loaded = doLoad(ii);
    assertEquals(List.of(8, 25, 42, 59, 76, 93), loaded);
  }


  @Test
  public void testInTemplate1() throws StencilException {
    MemorySourceProvider sourceProvider = new MemorySourceProvider();
    Stencils stencils = new Stencils(sourceProvider);
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply f=for('2','7')][loop f]{index}|{value},[end]");
    String output = stencils.write("test.txt", Map.of("for", ForFunction.INSTANCE));
    assertEquals("0|2,1|3,2|4,3|5,4|6,", output);
  }


  @Test
  public void testInTemplate2() throws StencilException {
    MemorySourceProvider sourceProvider = new MemorySourceProvider();
    Stencils stencils = new Stencils(sourceProvider);
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply f=for('2','7','2')][loop f]{index}|{value},[end]");
    String output = stencils.write("test.txt", Map.of("for", ForFunction.INSTANCE));
    assertEquals("0|2,1|4,2|6,", output);
  }


  @Test
  public void testInTemplate3() throws StencilException {
    MemorySourceProvider sourceProvider = new MemorySourceProvider();
    Stencils stencils = new Stencils(sourceProvider);
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply f=for('3')][loop f]{index}|{value},[end]");
    String output = stencils.write("test.txt", Map.of("for", ForFunction.INSTANCE));
    assertEquals("0|0,1|1,2|2,", output);
  }


  @Test
  public void testIndexOutOfBounds() {
    IndexableInt ii = new IndexableInt(1, 5);
    IntConsumer iii = new IntConsumer() {
      @Override
      @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
      public void accept(int value) {
        ii.apply(value);
      }
    };
    assertThrows(IndexOutOfBoundsException.class, () -> iii.accept(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> iii.accept(10));
  }


  @Test
  public void testZeroStep() {
    assertThrows(IllegalArgumentException.class, () -> new IndexableInt(1, 10, 0));
  }

}
