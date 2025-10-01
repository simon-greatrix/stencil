package com.pippsford.stencil.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 10/11/2021.
 */
public class DefaultJsonConverterTest {

  DefaultJsonConverter converter = new DefaultJsonConverter();


  @Test
  public void getProvider() {
    assertNotNull(converter.getProvider());
  }


  @Test
  public void test() {
    JsonProvider p = JsonProvider.provider();
    assertEquals(JsonValue.NULL, converter.toValue(null));
    assertEquals(JsonValue.FALSE, converter.toValue(false));
    assertEquals(JsonValue.FALSE, converter.toValue(new AtomicBoolean(false)));
    assertEquals(JsonValue.TRUE, converter.toValue(true));
    assertEquals(JsonValue.TRUE, converter.toValue(new AtomicBoolean(true)));
    assertEquals(p.createValue("test"), converter.toValue("test"));
    assertEquals(p.createValue(123), converter.toValue(123));
    assertEquals(p.createValue(123L), converter.toValue(new AtomicLong(123)));
    assertEquals(JsonValue.EMPTY_JSON_ARRAY, converter.toValue(List.of()));
    assertEquals(JsonValue.EMPTY_JSON_ARRAY, converter.toValue(JsonValue.EMPTY_JSON_ARRAY));
    assertEquals(p.createValue(5.46), converter.toValue(5.46));

    // check recursion
    Object[] b = { "a", null };
    b[1] = b;
    assertEquals(p.createArrayBuilder().add("a").add("\uaa5c").build(), converter.toValue(b));

  }

}
