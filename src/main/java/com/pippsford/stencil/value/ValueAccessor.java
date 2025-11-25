package com.pippsford.stencil.value;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A value accessor that allows access along a path of references.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
public class ValueAccessor {


  /**
   * Get a value including inherited ones.
   *
   * @param provider the value provider
   * @param names    the key array
   *
   * @return the value
   */
  @Nonnull
  public static OptionalValue get(@Nonnull ValueProvider provider, @Nonnull String[] names) {
    return get(provider, names, 0);
  }


  @Nonnull
  private static OptionalValue get(ValueProvider provider, String[] names, int index) {
    OptionalValue value = provider.get(names[index]);
    index++;
    if (value.isMissing() || index == names.length) {
      return value;
    }

    return get(makeProvider(provider, value.value()), names, index);
  }


  /**
   * Get a value ignoring inherited values.
   *
   * @param provider the value provider
   * @param names    the key array
   *
   * @return the value
   */
  @Nonnull
  public static OptionalValue getLocal(@Nonnull ValueProvider provider, @Nonnull String[] names) {
    return getLocal(provider, names, 0);
  }


  @Nonnull
  private static OptionalValue getLocal(ValueProvider provider, String[] names, int index) {
    OptionalValue value = provider.getLocal(names[index]);
    index++;
    if (value.isMissing() || index == names.length) {
      return OptionalValue.absent();
    }

    return getLocal(makeProvider(provider, value.value()), names, index);
  }


  /**
   * Ensure that a value provider can be mutated.
   *
   * @param provider the provider
   *
   * @return a mutable wrapper on the provider, or the provider itself if it was mutable
   */
  @Nonnull
  public static MutableValueProvider makeMutable(@Nonnull ValueProvider provider) {
    Objects.requireNonNull(provider);
    if (provider instanceof MutableValueProvider) {
      return (MutableValueProvider) provider;
    }
    return new StandardMutableProvider(provider);
  }


  /**
   * Create a value provider for accessing a particular object.
   *
   * @param parent parent provider for inheritance
   * @param source the object
   *
   * @return the value provider
   */
  @Nonnull
  public static ValueProvider makeProvider(@Nonnull ValueProvider parent, @Nullable Object source) {
    if (source == null) {
      return parent;
    }

    if (source instanceof ValueProvider) {
      // Ignores parent
      return (ValueProvider) source;
    }

    if (source instanceof Record) {
      return new RecordValueProvider(parent, (Record) source);
    }

    Class<?> sourceClass = source.getClass();
    if (Map.class.isAssignableFrom(sourceClass)) {
      return new MapValueProvider(parent, (Map<?, ?>) source);
    }

    if (sourceClass.isArray() || Iterable.class.isAssignableFrom(sourceClass) || Indexable.class.isAssignableFrom(sourceClass)) {
      return new IndexedValueProvider(parent, source);
    }

    return new BeanValueProvider(parent, source);
  }


  private static void put(MutableValueProvider provider, String[] names, int index, Object value) {
    String key = names[index];
    index++;
    if (index == names.length) {
      provider.put(key, value);
      return;
    }

    OptionalValue nextValue = provider.getLocal(key);
    if (nextValue.isMissing()) {
      MutableMapValueProvider map = new MutableMapValueProvider(provider);
      provider.put(key, map);
      put(map, names, index, value);
      return;
    }

    Object next = nextValue.value();
    if (!(next instanceof MutableValueProvider)) {
      // need to set as a mutable value provider
      next = makeMutable(makeProvider(provider, next));
      provider.put(key, next);
    }

    put((MutableValueProvider) next, names, index, value);
  }


  /**
   * Put a value into a given value provider.
   *
   * @param provider the value provider
   * @param names    the key names
   * @param value    the value to put
   */
  public static void put(@Nonnull MutableValueProvider provider, @Nonnull String[] names, @Nonnull Object value) {
    put(provider, names, 0, value);
  }


  /**
   * Put a value into the mutable provider.
   *
   * @param provider the provider
   * @param key      the key path
   * @param newValue the new value
   */
  public static void putIfMissing(@Nonnull MutableValueProvider provider, @Nonnull String[] key, @Nonnull Object newValue) {
    if (getLocal(provider, key).isMissing()) {
      put(provider, key, newValue);
    }
  }


  /**
   * Convert a dot-separated name to a key array.
   *
   * @param param the dot-separated name
   *
   * @return the array of keys
   */
  public static String[] toKey(@Nonnull String param) {
    return param.split("\\.");
  }


  ValueAccessor() {
    // do nothing
  }

}
