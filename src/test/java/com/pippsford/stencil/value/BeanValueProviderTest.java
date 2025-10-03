package com.pippsford.stencil.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pippsford.common.UncheckedCheckedException;
import com.pippsford.stencil.blocks.Pojo;
import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 07/01/2021.
 */
class BeanValueProviderTest {

  BeanValueProvider valueProvider = new BeanValueProvider(ValueProvider.NULL_VALUE_PROVIDER, new Pojo("Dr", "Karl", 45));


  @Test
  public void test1() {
    assertEquals("Dr", valueProvider.get("title").value());
    assertEquals(45, valueProvider.get("age").value());
    assertTrue(valueProvider.get("not a property").isMissing());
    assertThrows(UncheckedCheckedException.class, () -> valueProvider.get("error"));
  }

}
