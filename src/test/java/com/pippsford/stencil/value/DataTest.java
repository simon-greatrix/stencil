package com.pippsford.stencil.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.spi.JsonProvider;

import org.junit.jupiter.api.Test;

import com.pippsford.json.Canonical;
import com.pippsford.stencil.blocks.Pojo;

/**
 * @author Simon Greatrix on 07/01/2021.
 */
class DataTest {


  @Test
  void test1() {
    Pojo pojo1 = new Pojo("Dr", "Karl", 45);
    Pojo pojo2 = new Pojo("Mr", "Bartholomew", 13);
    pojo1.setOther(pojo2);

    ValueProvider valueProvider = new BeanValueProvider(ValueProvider.NULL_VALUE_PROVIDER, pojo1);
    Data data = new Data(valueProvider);

    assertEquals(13, data.get("other.age").value());
    assertEquals("Mr", data.get(new String[]{"other", "title"}).value());
    assertSame(valueProvider, data.getProvider());

    data.put("other.properties.a.b.c.d", "value");
    assertEquals("value", data.get("other.properties.a.b.c.d").value());
    assertEquals(MutableMapValueProvider.class, data.get("other.properties.a.b.c").value().getClass());

    // provider is now mutable
    assertNotSame(valueProvider, data.getProvider());
  }


  @Test
  void test2() {
    ValueProvider valueProvider = new MutableMapValueProvider(ValueProvider.NULL_VALUE_PROVIDER);
    Data data = new Data(valueProvider);
    assertNull(data.get("key").value());
    assertTrue(data.get("key").isMissing());
    assertSame(valueProvider, data.getProvider());
    data.put("key", "value");

    // input was mutable, so not changed by put
    assertSame(valueProvider, data.getProvider());
    assertEquals("value", data.get("key").value());
  }


  @Test
  void test3() {
    Pojo pojo1 = new Pojo("Dr", "Karl", 45);
    Pojo pojo2 = new Pojo("Mr", "Bartholomew", 13);
    pojo1.setOther(pojo2);

    ValueProvider valueProvider = new BeanValueProvider(ValueProvider.NULL_VALUE_PROVIDER, pojo1);
    Data data = new Data(valueProvider);
    data.put("other.properties.a.b.c.d", "value");
    data.put("other.properties.l", new String[]{"a", "b", "c"});
    data.put("properties", Map.of("a", new int[0], "c", ""));

    String json = Canonical.cast(data.toJson()).toPrettyString();
    assertEquals(
        """
            {
              "age": 45,
              "name": "Karl",
              "other": {
                "age": 13,
                "name": "Bartholomew",
                "other": null,
                "properties": {
                  "a": {
                    "b": {
                      "c": { "d": "value" }
                    }
                  },
                  "l": [ "a", "b", "c" ]
                },
                "qualifications": null,
                "title": "Mr"
              },
              "properties": { "a": [], "c": "" },
              "qualifications": null,
              "title": "Dr"
            }""", json);
  }


  @Test
  public void testAtomics() {
    Data data = new Data();
    data.put("b", new AtomicBoolean(true));
    data.put("i", new AtomicInteger(2));
    assertEquals(Boolean.TRUE, data.get("b").value());
    assertEquals(2, ((Number) data.get("i").safeValue()).intValue());
  }


  @Test
  public void testRawJson() {
    Data data = new Data();
    JsonArray array = JsonProvider.provider().createArrayBuilder(List.of(1, 2, 3)).build();
    JsonObject object = JsonProvider.provider().createObjectBuilder()
        .add("string", "a value")
        .add("true", true)
        .add("false", false)
        .add("array", array)
        .add("object", JsonProvider.provider().createObjectBuilder(Map.of("a", 1, "b", 2)).build())
        .add("number", 12345)
        .addNull("null")
        .build();
    data.put("json", object);
    assertEquals("a value", data.get("json.string").value());
    assertEquals(Boolean.TRUE, data.get("json.true").value());
    assertEquals(Boolean.FALSE, data.get("json.false").value());
    assertEquals(Integer.valueOf(12345), data.get("json.number").value());
    assertEquals(array, data.get("json.array").value());
    assertNull(data.get("json.null").value());
  }

}
