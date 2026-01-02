package com.pippsford.stencil.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
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
    Object[] b = {"a", null};
    b[1] = b;

    List<Object> list = new ArrayList<>(
        List.of(
            false,
            new AtomicBoolean(false),
            true,
            new AtomicBoolean(true),
            "test",
            123,
            new AtomicLong(123),
            List.of(4,5,'A'),
            JsonValue.EMPTY_JSON_ARRAY,
            5.46d,
            b
        ));
    list.add(null);

    assertEquals(
        """
            [false,false,true,true,"test",123,123,[4,5,"A"],[],5.46E0,["a","꩜"],null]""",
        converter.toValue(new IndexedValueProvider(ValueProvider.NULL_VALUE_PROVIDER, list)).toString()
    );

  }

}
