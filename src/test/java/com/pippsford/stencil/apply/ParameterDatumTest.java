package com.pippsford.stencil.apply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.MapValueProvider;
import com.pippsford.stencil.value.ValueProvider;

/**
 * @author Simon Greatrix on 21/01/2021.
 */
class ParameterDatumTest {

  HashMap<String, Object> map = new HashMap<>(Map.of(
      "foo", "bar",
      "alice", "bob",
      "empty", "",
      "num1", 46.57f,
      "num2", "47",
      "int",1234,
      "zero", "0"
  ));

  Data data = new Data(new MapValueProvider(ValueProvider.NULL_VALUE_PROVIDER, map));


  @Test
  void asBoolean() {
    assertTrue(new ParameterDatum(data, "zero").asBoolean());
    assertTrue(new ParameterDatum(data, "foo").asBoolean());
    assertFalse(new ParameterDatum(data, "empty").asBoolean());
  }


  @Test
  void asNumber() {
    assertEquals(new BigDecimal("46.57"), new ParameterDatum(data, "num1").asNumber());
  }


  @Test
  void asString() {
    assertEquals("bar", new ParameterDatum(data, "foo").asString());
  }


  @Test
  void compareTo() {
    map.put("nv", null);
    assertTrue(new ParameterDatum(data, "num1").compareTo(new ParameterDatum(data, "num2")) < 0);
    assertTrue(new ParameterDatum(data, "foo").compareTo(new ParameterDatum(data, "num2")) > 0);
    assertTrue(new ParameterDatum(data, "foo").compareTo(new ParameterDatum(data, "nv")) < 0);
    assertTrue(new ParameterDatum(data, "nv").compareTo(new ParameterDatum(data, "nv")) == 0);
    assertTrue(new ParameterDatum(data, "nv").compareTo(new ParameterDatum(data, "foo")) > 0);

  }


  @Test
  void getRaw() {
    assertEquals("foo", new ParameterDatum(data, "foo").getRaw());
  }


  @Test
  void getValue() {
    assertEquals(Integer.valueOf(1234),new ParameterDatum(data,"int").getValue());
  }


  @Test
  void isLiteral() {
    assertFalse(new ParameterDatum(data, "foo").isLiteral());
  }


  @Test
  void isNull() {
    map.put("nv", null);
    assertTrue(new ParameterDatum(data, "xxx").isNull());
    assertTrue(new ParameterDatum(data, "nv").isNull());
    assertFalse(new ParameterDatum(data, "foo").isNull());
  }


  @Test
  void isNumber() {
    assertTrue(new ParameterDatum(data, "num1").isNumber());
    assertTrue(new ParameterDatum(data, "zero").isNumber());
    assertFalse(new ParameterDatum(data, "foo").isNumber());
    assertFalse(new ParameterDatum(data, "xxx").isNumber());
  }


  @Test
  void testToString() {
    assertNotNull(new ParameterDatum(data, "foo").toString());
  }


  @Test
  void withData() {
    Data data2 = new Data(new MapValueProvider(ValueProvider.NULL_VALUE_PROVIDER, Map.of("foo", "wibble")));
    Parameter parameter = new ParameterDatum(data, "foo");
    Parameter parameter1 = parameter.withData(data2);
    assertEquals("bar", parameter.asString());
    assertEquals("wibble", parameter1.asString());
  }

}