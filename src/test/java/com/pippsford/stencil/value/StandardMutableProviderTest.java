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

    assertEquals("Alice", p1.get("a").value());
    assertEquals("Alice", p2.get("a").value());

    assertEquals("Bob", p1.get("b").value());
    assertEquals("Bertha", p2.get("b").value());

    assertNull(p1.get("e").value());
    assertEquals("Eve", p2.get("e").value());

    assertNull(p1.get("f").value());
    assertNull(p2.get("f").value());
  }


  @Test
  void put() {
    StandardMutableProvider p1 = new StandardMutableProvider(ValueProvider.NULL_VALUE_PROVIDER);
    assertThrows(IllegalArgumentException.class, () -> p1.put("a.b", "value"));
  }

}
