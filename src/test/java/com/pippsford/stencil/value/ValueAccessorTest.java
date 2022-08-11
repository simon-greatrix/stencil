package com.pippsford.stencil.value;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 12/01/2021.
 */
class ValueAccessorTest {

  @Test
  public void coverage() {
    assertNotNull(new ValueAccessor());
  }


  @Test
  public void makeMutable1() {
    assertThrows(NullPointerException.class, () -> ValueAccessor.makeMutable(null));
  }


  @Test
  public void makeMutable2() {
    MutableMapValueProvider provider = new MutableMapValueProvider(ValueProvider.NULL_VALUE_PROVIDER);
    assertSame(provider, ValueAccessor.makeMutable(provider));
    assertNotNull(ValueAccessor.makeMutable(new MapValueProvider(ValueProvider.NULL_VALUE_PROVIDER, Map.of("a", "b"))));
  }

}