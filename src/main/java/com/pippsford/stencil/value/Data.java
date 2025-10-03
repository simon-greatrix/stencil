package com.pippsford.stencil.value;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

/**
 * A data carrier with lazily initialized mutability. Data structures are hierarchical with levels being separated by '.'s in the parameter name.
 *
 * @author Simon Greatrix on 05/01/2021.
 */
public class Data {

  private static final Map<ValueType, Function<JsonValue, Object>> JSON_ACCESSORS;


  /**
   * Convert various raw data to more convenient forms.
   *
   * @param optRaw the raw datum
   *
   * @return the simplified form
   */
  private static OptionalValue convertRaw(OptionalValue optRaw) {
    if( optRaw.isMissing() ) return optRaw;
    Object raw = optRaw.value();

    // Return simple values quickly.
    if (raw == null || raw instanceof String || raw instanceof Number) {
      return optRaw;
    }

    if (raw instanceof JsonValue value) {
      return OptionalValue.of(JSON_ACCESSORS.get(value.getValueType()).apply(value));
    }

    if (raw instanceof AtomicBoolean ab) {
      return OptionalValue.of(ab.get());
    }

    // No conversion
    return optRaw;
  }


  /**
   * Create a Data instance wrapping a particular input value.
   *
   * @param input the input value
   *
   * @return the wrapping Data.
   */
  public static Data create(Object input) {
    if (input instanceof Data) {
      return (Data) input;
    }

    return new Data(ValueAccessor.makeProvider(ValueProvider.NULL_VALUE_PROVIDER, input));
  }


  static {
    EnumMap<ValueType, Function<JsonValue, Object>> map = new EnumMap<>(ValueType.class);
    map.put(ValueType.ARRAY, json -> json);
    map.put(ValueType.FALSE, json -> Boolean.FALSE);
    map.put(ValueType.OBJECT, json -> json);
    map.put(ValueType.NULL, json -> null);
    map.put(ValueType.NUMBER, json -> ((JsonNumber) json).numberValue());
    map.put(ValueType.STRING, json -> ((JsonString) json).getString());
    map.put(ValueType.TRUE, json -> Boolean.TRUE);
    JSON_ACCESSORS = Collections.unmodifiableMap(map);
  }

  private MutableValueProvider mutable;

  private ValueProvider provider;


  /**
   * New instance.
   *
   * @param provider the value provider which backs this instance
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Data(ValueProvider provider) {
    this.provider = provider;
    if (provider instanceof MutableValueProvider) {
      mutable = (MutableValueProvider) provider;
    } else {
      mutable = null;
    }
  }


  /**
   * New mutable empty instance.
   */
  public Data() {
    this(ValueProvider.NULL_VALUE_PROVIDER);
  }


  /**
   * Get a value in this data collection. The value may be transformed into some kind of standard form.
   *
   * @param name the value's name, which may contain periods ('.') as separators
   *
   * @return the value
   */
  @Nonnull
  public OptionalValue get(@Nonnull String name) {
    return convertRaw(getRaw(name));
  }


  /**
   * Get a value in this data collection. The value may be transformed into some kind of standard form.
   *
   * @param key the separated keys
   *
   * @return the value
   */
  @Nonnull
  public OptionalValue get(@Nonnull String[] key) {
    return convertRaw(getRaw(key));
  }


  /**
   * Get the value provider for this.
   *
   * @return the value provider
   */
  public ValueProvider getProvider() {
    return provider;
  }


  /**
   * Get a value in this data collection.
   *
   * @param key the separated keys
   *
   * @return the value, or null
   */
  @Nullable
  public OptionalValue getRaw(@Nonnull String[] key) {
    return ValueAccessor.get(provider, key);
  }


  /**
   * Get a value in this data collection.
   *
   * @param name the value's name, which may contain periods ('.') as separators
   *
   * @return the value
   */
  @Nonnull
  public OptionalValue getRaw(@Nonnull String name) {
    return get(ValueAccessor.toKey(name));
  }


  /**
   * Set a value in this data collection.
   *
   * @param name     the value's name, which may contain periods ('.') as separators
   * @param newValue the new value to associate with the name
   */
  public void put(@Nonnull String name, @Nonnull Object newValue) {
    put(ValueAccessor.toKey(name), newValue);
  }


  /**
   * Set a value in this data collection.
   *
   * @param key      the separated value's name
   * @param newValue the new value to associate with the name
   */
  public void put(@Nonnull String[] key, Object newValue) {
    if (mutable == null) {
      mutable = ValueAccessor.makeMutable(provider);
      provider = mutable;
    }
    ValueAccessor.put(mutable, key, newValue);
  }


  /**
   * Set a value in this data collection.
   *
   * @param name     the value's name, which may contain periods ('.') as separators
   * @param newValue the new value to associate with the name
   */
  public void putIfMissing(String name, Object newValue) {
    putIfMissing(ValueAccessor.toKey(name), newValue);
  }


  /**
   * Set a value in this data collection.
   *
   * @param key      the separated value's name
   * @param newValue the new value to associate with the name
   */
  public void putIfMissing(@Nonnull String[] key, Object newValue) {
    if (mutable == null) {
      mutable = ValueAccessor.makeMutable(provider);
      provider = mutable;
    }
    ValueAccessor.putIfMissing(mutable, key, newValue);
  }


  /**
   * Convert this instance to JSON. Requires a JSON provider on the classpath.
   *
   * @return this Data instance as JSON.
   */
  public JsonStructure toJson() {
    return toJson(null);
  }


  /**
   * Convert this instance to JSON. Requires a JSON provider on the classpath.
   *
   * @param converter bean to JSON converter.
   *
   * @return this Data instance as JSON.
   */
  public JsonStructure toJson(JsonConverter converter) {
    if (converter == null) {
      converter = new DefaultJsonConverter();
    }
    return (JsonStructure) converter.toValue(mutable != null ? mutable : provider);
  }

}
