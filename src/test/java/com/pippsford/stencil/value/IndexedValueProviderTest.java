package com.pippsford.stencil.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 07/01/2021.
 */
class IndexedValueProviderTest {

  @Test
  void test1() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, new int[]{1, 2, 3});
    assertEquals(3, valueProvider.size());
    assertEquals(3, valueProvider.get("size"));
    assertEquals(Boolean.FALSE, valueProvider.get("isEmpty"));
    assertNull(valueProvider.get(56));
    assertEquals(2, valueProvider.get("1"));
  }


  @Test
  public void test10() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, List.of("a", "b", "c", "d"));
    Data data = new Data(valueProvider);
    assertEquals("[\"a\",\"b\",\"c\",\"d\"]", data.toJson().toString());

    valueProvider.put("a", "b");
    data = new Data(valueProvider);
    assertEquals("{\"0\":\"a\",\"1\":\"b\",\"2\":\"c\",\"3\":\"d\",\"a\":\"b\",\"isEmpty\":false,\"size\":4}", data.toJson().toString());
  }


  @Test
  void test2() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, List.of("a", "b", "c", "d"));
    assertEquals(4, valueProvider.size());
    assertEquals(4, valueProvider.get("size"));
    assertEquals(Boolean.FALSE, valueProvider.get("isEmpty"));
    assertNull(valueProvider.get(-23));
    assertEquals("b", valueProvider.get("1"));
  }


  @Test
  void test3() {
    MutableMapValueProvider parent = new MutableMapValueProvider(ValueProvider.NULL_VALUE_PROVIDER);
    parent.put("0", "default");
    IndexedValueProvider valueProvider = new IndexedValueProvider(parent, new Float[0]);
    assertEquals(0, valueProvider.size());
    assertEquals(0, valueProvider.get("size"));
    assertEquals(Boolean.TRUE, valueProvider.get("isEmpty"));
    assertEquals("default", valueProvider.get(0));
    assertEquals("default", valueProvider.get("0"));
    assertNull(valueProvider.get(1));
    assertNull(valueProvider.get("1"));
  }


  @Test
  void test4() {
    MutableMapValueProvider parent = new MutableMapValueProvider(ValueProvider.NULL_VALUE_PROVIDER);
    parent.put("3", "default");
    IndexedValueProvider valueProvider = new IndexedValueProvider(parent, List.of());
    assertEquals(0, valueProvider.size());
    assertEquals(0, valueProvider.get("size"));
    assertEquals(Boolean.TRUE, valueProvider.get("isEmpty"));
    assertNull(valueProvider.get(-23));
    assertNull(valueProvider.get("0"));
    assertEquals("default", valueProvider.get(3));
    assertEquals("default", valueProvider.get("3"));
  }


  @Test
  void test5() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, new TreeSet<>(Set.of("fish", "cat", "dog")));
    assertEquals(3, valueProvider.size());
    assertEquals(3, valueProvider.get("size"));
    assertNull(valueProvider.get(-23));
    assertEquals("fish", valueProvider.get(2));
    assertEquals("cat", valueProvider.get("0"));
  }


  @Test
  void test6() {
    IndexedValueProvider valueProvider =
        new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, new TreeMap<>(Map.of("fish", "salmon", "cat", "tiger", "dog", "woof")));
    assertEquals(3, valueProvider.size());
    assertEquals(3, valueProvider.get("size"));
    assertNull(valueProvider.get(-23));
    assertEquals("fish=salmon", valueProvider.get(2).toString());
    assertEquals("tiger", ValueAccessor.get(valueProvider, new String[]{"0", "value"}));
  }


  @Test
  void test7() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, List.of("a", "b", "c", "d"));
    assertEquals("b", valueProvider.get(1));
    assertEquals("b", valueProvider.get("1"));
    valueProvider.put("1", "thing");
    assertEquals("thing", valueProvider.get(1));
    assertEquals("thing", valueProvider.get("1"));
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
    assertEquals(87, valueProvider.get("size"));
    assertFalse(valueProvider.isPureList());
  }


  @Test
  public void test9() {
    IndexedValueProvider valueProvider = new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, List.of("a", "b", "c", "d"));
    valueProvider.put("87", 87);
    assertFalse(valueProvider.isPureList());
  }

}