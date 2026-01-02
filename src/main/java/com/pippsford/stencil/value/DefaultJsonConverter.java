package com.pippsford.stencil.value;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

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

  private static final List<String> BLACKLISTED_NAMES = List.of(
      "java.",
      "javax.",
      "jakarta.",
      "com.sun.",
      "jdk.",
      "sun."
  );

  private static final List<BiFunction<JsonProvider, Object, JsonValue>> CONVERTERS = List.of(
      (p, o) -> (o == null) ? JsonValue.NULL : null,
      (p, o) -> (o instanceof JsonValue) ? (JsonValue) o : null,
      (p, o) -> (o instanceof CharSequence) ? p.createValue(((CharSequence) o).toString()) : null,
      (p, o) -> (o instanceof Number) ? toNumber(p, (Number) o) : null,
      (p, o) -> (o instanceof Boolean) ? (((Boolean) o) ? JsonValue.TRUE : JsonValue.FALSE) : null,
      (p, o) -> (o instanceof AtomicBoolean) ? (((AtomicBoolean) o).get() ? JsonValue.TRUE : JsonValue.FALSE) : null,
      (p, o) -> (o instanceof Enum) ? p.createValue(((Enum<?>) o).name()) : null,
      (p, o) -> isBlacklisted(o) ? p.createValue(String.valueOf(o)) : null
  );



  /** Conversion state. */
  protected static class State {

    protected final int maxDepth;

    /** Set of processed vales. */
    protected final IdentityHashSet processed = new IdentityHashSet();

    /** Current conversion depth. */
    protected int depth;


    public State(int maxDepth) {
      this.maxDepth = maxDepth;
    }

  }


  /**
   * Check if an object should not be JSON expanded.
   *
   * @param object the object to check
   *
   * @return true if the object should not be expanded.
   */
  protected static boolean isBlacklisted(Object object) {
    if (object instanceof Collection<?> || object instanceof Map<?, ?>) {
      return false;
    }

    String className = object.getClass().getName();
    for (String prefix : BLACKLISTED_NAMES) {
      if (className.startsWith(prefix)) {
        return true;
      }
    }
    return false;
  }


  /**
   * Convert a number to a JSON number.
   *
   * @param provider the JSON implementation
   * @param number   the number to convert
   *
   * @return the JSON representation
   */
  protected static JsonNumber toNumber(JsonProvider provider, Number number) {
    if (number instanceof Integer || number instanceof AtomicInteger) {
      return provider.createValue(number.intValue());
    }
    if (number instanceof Long || number instanceof AtomicLong) {
      return provider.createValue(number.longValue());
    }
    return provider.createValue(new BigDecimal(number.toString()));
  }


  /** The JSON provider used to create JSON values. */
  protected final JsonProvider provider;

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
  protected JsonValue convertAny(State state, Object object) {
    for (BiFunction<JsonProvider, Object, JsonValue> converter : CONVERTERS) {
      JsonValue value = converter.apply(provider, object);
      if (value != null) {
        return value;
      }
    }

    if (!state.processed.add(object)) {
      return provider.createValue("꩜");
    }

    state.depth++;
    try {
      if (state.depth >= state.maxDepth) {
        return provider.createValue("⋯");
      }

      ValueProvider provider = ValueAccessor.makeProvider(ValueProvider.NULL_VALUE_PROVIDER, object);
      if (provider instanceof IndexedValueProvider p) {
        if (p.isPureList()) {
          return toArray(state, p);
        }
        // Not a pure list, so process as an object
      }
      return toObject(state, provider);
    } finally {
      state.depth--;
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
  protected JsonArray toArray(State state, IndexedValueProvider values) {
    int s = values.size();
    JsonArrayBuilder builder = provider.createArrayBuilder();
    // As the input should be a pure list, all indices should be present.
    for (int i = 0; i < s; i++) {
      OptionalValue value = values.get(i);
      if (value.isPresent()) {
        // Should always be true
        builder.add(convertAny(state, value.value()));
      }
    }
    return builder.build();
  }


  /**
   * Convert a value to a JSON object.
   *
   * @param values the values to convert
   *
   * @return a JSON object holding the values
   */
  protected JsonObject toObject(State state, ValueProvider values) {
    JsonObjectBuilder builder = provider.createObjectBuilder();
    values.visit((k, v, r) -> {
      if (r) {
        builder.add(k, convertAny(state, v));
      }
    });
    return builder.build();
  }


  @Override
  public JsonValue toValue(ValueProvider object) {
    return convertAny(new State(maxDepth), object);
  }

}
