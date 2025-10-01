package com.pippsford.stencil.value;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

/**
 * Standard JSON converter that does simplistic expansion of beans.
 *
 * @author Simon Greatrix on 10/11/2021.
 */
public class DefaultJsonConverter implements JsonConverter {

  /** The JSON provider used to create JSON values. */
  protected final JsonProvider provider;

  private final IdentityHashSet processed = new IdentityHashSet();

  private int depth;

  private int maxDepth = 10;


  /**
   * New instance using the specified JSON provider.
   *
   * @param provider the provider to use (null means default)
   */
  public DefaultJsonConverter(JsonProvider provider) {
    this.provider = (provider != null) ? provider : JsonProvider.provider();
  }


  /** New instance using the default JSON provider. */
  public DefaultJsonConverter() {
    this(null);
  }


  /**
   * Convert any object to a JSON object.
   *
   * @param object the object to convert
   *
   * @return the JsonValue that corresponds to the object
   */
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


  /**
   * Get the maximum processing depth.
   *
   * @return the maximum depth
   */
  public int getMaxDepth() {
    return maxDepth;
  }


  /**
   * Get the JSON provider.
   *
   * @return the provider
   */
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


  /**
   * The maximum number of levels to expand the object.
   *
   * @param maxDepth the new maximum depth
   */
  public void setMaxDepth(int maxDepth) {
    this.maxDepth = maxDepth;
  }


  /**
   * Convert an indexed value to a JSON array.
   *
   * @param values the collection of values
   *
   * @return the JSON representation
   */
  protected JsonArray toArray(IndexedValueProvider values) {
    int s = values.size();
    JsonArrayBuilder builder = provider.createArrayBuilder();
    for (int i = 0; i < s; i++) {
      builder.add(convertAny(values.get(i)));
    }
    return builder.build();
  }


  /**
   * Convert a number to a JSON number.
   *
   * @param number the number to convert
   *
   * @return the JSON representation
   */
  protected JsonNumber toNumber(Number number) {
    if (number instanceof Integer || number instanceof AtomicInteger) {
      return provider.createValue(number.intValue());
    }
    if (number instanceof Long || number instanceof AtomicLong) {
      return provider.createValue(number.longValue());
    }
    return provider.createValue(new BigDecimal(number.toString()));
  }


  /**
   * Convert a value to a JSON object.
   *
   * @param values the values to convert
   *
   * @return a JSON object holding the values
   */
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
