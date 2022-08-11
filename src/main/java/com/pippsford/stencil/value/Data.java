package com.pippsford.stencil.value;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 * A data carrier with lazily initialized mutability. Data structures are hierarchical with levels being separated by '.'s in the parameter name.
 *
 * @author Simon Greatrix on 05/01/2021.
 */
public class Data {

  /**
   * Convert various raw data to more convenient forms.
   *
   * @param raw the raw datum
   *
   * @return the simplified form
   */
  private static Object convertRaw(Object raw) {
    // Return simple values quickly.
    if (raw == null || raw instanceof String || raw instanceof Number) {
      return raw;
    }

    if (raw instanceof JsonValue) {
      JsonValue value = (JsonValue) raw;
      switch (value.getValueType()) {
        case NULL:
          return null;
        case ARRAY:
        case OBJECT:
          return value;
        case TRUE:
          return Boolean.TRUE;
        case FALSE:
          return Boolean.FALSE;
        case NUMBER:
          return ((JsonNumber) value).numberValue();
        case STRING:
          return ((JsonString) value).getString();
        default:
          // Unreachable line
          throw new InternalError("Unknown JSON type:" + value.getValueType());
      }
    }

    if (raw instanceof AtomicBoolean) {
      return ((AtomicBoolean) raw).get();
    }

    // No conversion
    return raw;
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


  private MutableValueProvider mutable;

  private ValueProvider provider;


  /**
   * New instance.
   *
   * @param provider the value provider which backs this instance
   */
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
   * @return the value, or null
   */
  @Nullable
  public Object get(@Nonnull String name) {
    return convertRaw(getRaw(name));
  }


  /**
   * Get a value in this data collection. The value may be transformed into some kind of standard form.
   *
   * @param key the separated keys
   *
   * @return the value, or null
   */
  @Nullable
  public Object get(@Nonnull String[] key) {
    return convertRaw(getRaw(key));
  }


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
  public Object getRaw(@Nonnull String[] key) {
    return ValueAccessor.get(provider, key);
  }


  /**
   * Get a value in this data collection.
   *
   * @param name the value's name, which may contain periods ('.') as separators
   *
   * @return the value, or null
   */
  @Nullable
  public Object getRaw(@Nonnull String name) {
    return get(ValueAccessor.toKey(name));
  }


  /**
   * Set a value in this data collection.
   *
   * @param name     the value's name, which may contain periods ('.') as separators
   * @param newValue the new value to associate with the name
   */
  public void put(String name, Object newValue) {
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
   * @return this Data instance as JSON.
   */
  public JsonStructure toJson(JsonConverter converter) {
    if (converter == null) {
      converter = new DefaultJsonConverter();
    }
    return (JsonStructure) converter.toValue(mutable != null ? mutable : provider);
  }

}
