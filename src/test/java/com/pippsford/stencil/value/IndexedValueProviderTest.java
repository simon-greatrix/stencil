package com.pippsford.stencil.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.pippsford.stencil.apply.EntriesFunction;
import com.pippsford.stencil.blocks.Pojo;
import com.pippsford.stencil.value.RecordValueProviderTest.PoRec;
import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 07/01/2021.
 */
class IndexedValueProviderTest {

  @Test
  public void intArray() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, new int[]{1, 2, 3});
    assertEquals(3, valueProvider.size());
    assertEquals(3, valueProvider.get("size").value());
    assertEquals(Boolean.FALSE, valueProvider.get("isEmpty").value());
    assertNull(valueProvider.get(56).value());
    assertEquals(2, valueProvider.get("1").value());
  }


  @Test
  public void list() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, List.of("a", "b", "c", "d"));
    Data data = new Data(valueProvider);
    assertEquals(
        """
            ["a","b","c","d"]""", data.toJson().toString());

    valueProvider.put("a", "b");
    data = new Data(valueProvider);
    assertEquals(
        """
            {"0":"a","1":"b","2":"c","3":"d","a":"b"}""", data.toJson().toString());
  }

  @Test
  public void singleValue() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, 17);
    assertEquals(1, valueProvider.size());
  }


  @Test
  public void list2() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, List.of("a", "b", "c", "d"));
    assertEquals(4, valueProvider.size());
    assertEquals(4, valueProvider.get("size").value());
    assertEquals(Boolean.FALSE, valueProvider.get("isEmpty").value());
    assertNull(valueProvider.get(-23).value());
    assertEquals("b", valueProvider.get("1").value());
  }


  @Test
  void test3() {
    MutableMapValueProvider parent = new MutableMapValueProvider(ValueProvider.NULL_VALUE_PROVIDER);
    parent.put("0", "default");
    IndexedValueProvider valueProvider = new IndexedValueProvider(parent, new Float[0]);
    assertEquals(0, valueProvider.size());
    assertEquals(0, valueProvider.get("size").value());
    assertEquals(Boolean.TRUE, valueProvider.get("isEmpty").value());
    assertEquals("default", valueProvider.get(0).value());
    assertEquals("default", valueProvider.get("0").value());
    assertNull(valueProvider.get(1).value());
    assertNull(valueProvider.get("1").value());
  }


  @Test
  void test4() {
    MutableMapValueProvider parent = new MutableMapValueProvider(ValueProvider.NULL_VALUE_PROVIDER);
    parent.put("3", "default");
    IndexedValueProvider valueProvider = new IndexedValueProvider(parent, List.of());
    assertEquals(0, valueProvider.size());
    assertEquals(0, valueProvider.get("size").value());
    assertEquals(Boolean.TRUE, valueProvider.get("isEmpty").value());
    assertNull(valueProvider.get(-23).value());
    assertNull(valueProvider.get("0").value());
    assertEquals("default", valueProvider.get(3).value());
    assertEquals("default", valueProvider.get("3").value());
  }


  @Test
  void test5() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, new TreeSet<>(Set.of("fish", "cat", "dog")));
    assertEquals(3, valueProvider.size());
    assertEquals(3, valueProvider.get("size").value());
    assertNull(valueProvider.get(-23).value());
    assertEquals("fish", valueProvider.get(2).value());
    assertEquals("cat", valueProvider.get("0").value());
  }




  @Test
  void test7() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, List.of("a", "b", "c", "d"));
    assertEquals("b", valueProvider.get(1).value());
    assertEquals("b", valueProvider.get("1").value());
    valueProvider.put("1", "thing");
    assertEquals("thing", valueProvider.get(1).value());
    assertEquals("thing", valueProvider.get("1").value());
  }


  @Test
  public void test8() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, List.of("a", "b", "c", "d"));
    assertEquals(4, valueProvider.size());
    assertTrue(valueProvider.isPureList());

    valueProvider.put("4", "e");
    assertEquals(5, valueProvider.size());
    assertTrue(valueProvider.isPureList());

    valueProvider.put("size", 87);
    assertEquals(5, valueProvider.size());
    assertEquals(87, valueProvider.get("size").value());
    assertFalse(valueProvider.isPureList());
  }


  @Test
  public void test9() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, List.of("a", "b", "c", "d"));
    valueProvider.put("87", 87);
    assertFalse(valueProvider.isPureList());
  }

  @Test
  public void test10() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, List.of());
    assertTrue(valueProvider.isPureList());
    valueProvider.put("0",87);
    assertTrue(valueProvider.isPureList());
  }
}
