package com.pippsford.stencil.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pippsford.common.UncheckedCheckedException;
import com.pippsford.stencil.blocks.Pojo;
import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 07/01/2021.
 */
class RecordValueProviderTest {

  public record PoRec(String title, String name, int age) {}

  RecordValueProvider valueProvider = new RecordValueProvider(ValueProvider.NULL_VALUE_PROVIDER, new PoRec("Dr", "Karl", 45));


  @Test
  public void test1() {
    assertEquals("Dr", valueProvider.get("title").value());
    assertEquals(45, valueProvider.get("age").value());
    assertTrue(valueProvider.get("not a property").isMissing());
    assertEquals(PoRec.class, valueProvider.get("class").value());
  }

}
