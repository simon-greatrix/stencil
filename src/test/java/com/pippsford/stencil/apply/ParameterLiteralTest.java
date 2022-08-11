package com.pippsford.stencil.apply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.MutableMapValueProvider;
import com.pippsford.stencil.value.ValueProvider;

/**
 * @author Simon Greatrix on 21/01/2021.
 */
class ParameterLiteralTest {

  ParameterLiteral literal = new ParameterLiteral("xyz");


  @Test
  void getValue() {
    assertEquals("xyz", literal.getValue());
  }


  @Test
  void isLiteral() {
    assertTrue(literal.isLiteral());
  }


  @Test
  void testToString() {
    assertNotNull(literal.toString());
  }


  @Test
  void withData() {
    assertSame(literal, literal.withData(new Data(new MutableMapValueProvider(ValueProvider.NULL_VALUE_PROVIDER))));
  }

}