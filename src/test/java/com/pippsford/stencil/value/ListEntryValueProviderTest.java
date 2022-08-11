package com.pippsford.stencil.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 10/11/2021.
 */
class ListEntryValueProviderTest {

  ListEntryValueProvider e = new ListEntryValueProvider(ValueProvider.NULL_VALUE_PROVIDER, 1, 5, Map.entry("x", "y"));

  ListEntryValueProvider p = new ListEntryValueProvider(ValueProvider.NULL_VALUE_PROVIDER, 1, 5, "hello");


  @Test
  void get() {
    assertEquals(1, e.get("index"));
    assertEquals(5, e.get("size"));
    assertEquals("hello", p.get("value"));
    assertNull(p.get("key"));
    assertEquals("x", e.get("key"));
    assertEquals("y", e.get("value"));
    assertNull(p.get("z"));
  }


  @Test
  void visit() {
    Map<String, Object> map = new HashMap<>();
    e.visit(map::put);
    assertEquals(Map.of("index", 1, "size", 5, "key", "x", "value", "y"), map);

    map.clear();
    p.visit(map::put);
    assertEquals(Map.of("index", 1, "size", 5, "value", "hello"), map);
  }

}