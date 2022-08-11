package com.pippsford.stencil.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 07/01/2021.
 */
class StandardMutableProviderTest {

  @Test
  void get() {
    StandardMutableProvider p1 = new StandardMutableProvider(ValueProvider.NULL_VALUE_PROVIDER);
    StandardMutableProvider p2 = new StandardMutableProvider(p1);

    p1.put("a", "Alice");
    p1.put("b", "Bob");
    p2.put("e", "Eve");
    p2.put("b", "Bertha");

    assertEquals("Alice", p1.get("a"));
    assertEquals("Alice", p2.get("a"));

    assertEquals("Bob", p1.get("b"));
    assertEquals("Bertha", p2.get("b"));

    assertNull(p1.get("e"));
    assertEquals("Eve", p2.get("e"));

    assertNull(p1.get("f"));
    assertNull(p2.get("f"));
  }


  @Test
  void put() {
    StandardMutableProvider p1 = new StandardMutableProvider(ValueProvider.NULL_VALUE_PROVIDER);
    assertThrows(IllegalArgumentException.class, () -> p1.put("a.b", "value"));
  }

}