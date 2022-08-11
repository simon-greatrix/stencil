package com.pippsford.stencil.apply;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.MutableMapValueProvider;
import com.pippsford.stencil.value.ValueProvider;

/**
 * @author Simon Greatrix on 21/01/2021.
 */
class ParameterNullTest {

  @Test
  void getValue() {
    assertNull(ParameterNull.INSTANCE.getValue());
  }


  @Test
  void isLiteral() {
    assertFalse(ParameterNull.INSTANCE.isLiteral());
  }


  @Test
  void testToString() {
    assertNotNull(ParameterNull.INSTANCE.toString());
  }


  @Test
  void withData() {
    assertSame(ParameterNull.INSTANCE, ParameterNull.INSTANCE.withData(new Data(new MutableMapValueProvider(ValueProvider.NULL_VALUE_PROVIDER))));
  }

}