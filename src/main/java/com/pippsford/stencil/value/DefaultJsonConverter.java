package com.pippsford.stencil.value;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;

/**
 * Standard JSON converter that does simplistic expansion of beans.
 *
 * @author Simon Greatrix on 10/11/2021.
 */
public class DefaultJsonConverter implements JsonConverter {

  protected final JsonProvider provider;

  private final IdentityHashSet processed = new IdentityHashSet();

  private int depth;

  private int maxDepth = 10;


  public DefaultJsonConverter(JsonProvider provider) {
    this.provider = (provider != null) ? provider : JsonProvider.provider();
  }


  public DefaultJsonConverter() {
    this(null);
  }


  protected JsonValue convertAny(Object object) {
    if (object == null) {
      return JsonValue.NULL;
    }
    if (object instanceof JsonValue) {
      return (JsonValue) object;
    }
    if (object instanceof CharSequence) {
      return provider.createValue(((CharSequence) object).toString());
    }
    if (object instanceof Number) {
      return toNumber((Number) object);
    }
    if (object instanceof Boolean) {
      return ((Boolean) object) ? JsonValue.TRUE : JsonValue.FALSE;
    }
    if (object instanceof AtomicBoolean) {
      return ((AtomicBoolean) object).get() ? JsonValue.TRUE : JsonValue.FALSE;
    }
    if (object instanceof Enum) {
      return provider.createValue(((Enum<?>) object).name());
    }

    if (isBlacklisted(object)) {
      return provider.createValue(String.valueOf(object));
    }

    if (!processed.add(object)) {
      return provider.createValue("꩜");
    }

    depth++;
    try {
      if (depth >= maxDepth) {
        return provider.createValue("⋯");
      }

      ValueProvider provider = ValueAccessor.makeProvider(ValueProvider.NULL_VALUE_PROVIDER, object);
      if (provider instanceof IndexedValueProvider) {
        IndexedValueProvider p = (IndexedValueProvider) provider;
        if (p.isPureList()) {
          return toArray(p);
        }
        // Not a pure list, so process as an object
      }
      return toObject(provider);
    } finally {
      depth--;
    }
  }


  public int getMaxDepth() {
    return maxDepth;
  }


  public JsonProvider getProvider() {
    return provider;
  }


  /**
   * Check if an object should not be JSON expanded.
   *
   * @param object the object to check
   *
   * @return true if the object should not be expanded.
   */
  protected boolean isBlacklisted(Object object) {
    if (object instanceof Collection<?> || object instanceof Map<?, ?>) {
      return false;
    }

    String className = object.getClass().getName();
    return className.startsWith("java.")
        || className.startsWith("javax.")
        || className.startsWith("com.sun.")
        || className.startsWith("jdk.")
        || className.startsWith("sun.");
  }


  public void setMaxDepth(int maxDepth) {
    this.maxDepth = maxDepth;
  }


  protected JsonArray toArray(IndexedValueProvider values) {
    int s = values.size();
    JsonArrayBuilder builder = provider.createArrayBuilder();
    for (int i = 0; i < s; i++) {
      builder.add(convertAny(values.get(i)));
    }
    return builder.build();
  }


  protected JsonNumber toNumber(Number number) {
    if (number instanceof Integer || number instanceof AtomicInteger) {
      return provider.createValue(number.intValue());
    }
    if (number instanceof Long || number instanceof AtomicLong) {
      return provider.createValue(number.longValue());
    }
    return provider.createValue(new BigDecimal(number.toString()));
  }


  protected JsonObject toObject(ValueProvider values) {
    JsonObjectBuilder builder = provider.createObjectBuilder();
    values.visit((k, v) -> builder.add(k, convertAny(v)));
    return builder.build();
  }


  @Override
  public JsonValue toValue(Object object) {
    processed.clear();
    depth = 0;
    return convertAny(object);
  }

}
