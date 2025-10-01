package com.pippsford.stencil.value;

import java.util.Map;
import java.util.Objects;

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
  public static Object get(ValueProvider provider, String[] names) {
    return get(provider, names, 0);
  }


  private static Object get(ValueProvider provider, String[] names, int index) {
    Object value = provider.get(names[index]);
    index++;
    if (value == null || index == names.length) {
      return value;
    }

    return get(makeProvider(provider, value), names, index);
  }


  /**
   * Get a value ignoring inherited values.
   *
   * @param provider the value provider
   * @param names    the key array
   *
   * @return the value
   */
  public static Object getLocal(ValueProvider provider, String[] names) {
    return getLocal(provider, names, 0);
  }


  private static Object getLocal(ValueProvider provider, String[] names, int index) {
    Object value = provider.getLocal(names[index]);
    index++;
    if (value == null || index == names.length) {
      return value;
    }

    return getLocal(makeProvider(provider, value), names, index);
  }


  /**
   * Ensure that a value provider can be mutated.
   *
   * @param provider the provider
   *
   * @return a mutable wrapper on the provider, or the provider itself if it was mutable
   */
  public static MutableValueProvider makeMutable(ValueProvider provider) {
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
  public static ValueProvider makeProvider(ValueProvider parent, Object source) {
    if (source == null) {
      return parent;
    }
    if (source instanceof ValueProvider) {
      // Ignores parent
      return (ValueProvider) source;
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

    Object next = provider.getLocal(key);
    if (next == null) {
      MutableMapValueProvider map = new MutableMapValueProvider(provider);
      provider.put(key, map);
      put(map, names, index, value);
      return;
    }

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
  public static void put(MutableValueProvider provider, String[] names, Object value) {
    put(provider, names, 0, value);
  }


  /**
   * Put a value into the mutable provider.
   *
   * @param provider the provider
   * @param key      the key path
   * @param newValue the new value
   */
  public static void putIfMissing(MutableValueProvider provider, String[] key, Object newValue) {
    if (getLocal(provider, key) == null) {
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
  public static String[] toKey(String param) {
    return param.split("\\.");
  }


  ValueAccessor() {
    // do nothing
  }

}
