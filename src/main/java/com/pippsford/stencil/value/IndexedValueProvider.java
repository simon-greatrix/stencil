package com.pippsford.stencil.value;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A value provider that allows access to a list. List's have a property for every index, plus "size" and "isEmpty".
 *
 * @author Simon Greatrix on 03/01/2021.
 */
public class IndexedValueProvider implements MutableValueProvider {

  private static final Pattern INDEX = Pattern.compile("[1-9][0-9]*");

  static final String P_IS_EMPTY = "isEmpty";

  static final String P_SIZE = "size";

  private final IntFunction<Object> getter;

  private final Map<String, Object> otherValues = new HashMap<>();

  private final ValueProvider parent;

  private final int size;


  /**
   * New instance.
   *
   * @param parent the parent value provider
   * @param array  the list or array
   */
  public IndexedValueProvider(ValueProvider parent, Object array) {
    this.parent = parent;
    if (array instanceof List<?>) {
      getter = ((List<?>) array)::get;
      size = ((List<?>) array).size();
    } else if (array.getClass().isArray()) {
      getter = i -> Array.get(array, i);
      size = Array.getLength(array);
    } else if (array instanceof Iterable<?>) {
      ArrayList<Object> list = new ArrayList<>();
      for (Object o : (Iterable<?>) array) {
        list.add(o);
      }
      getter = list::get;
      size = list.size();
    } else if (array instanceof Map<?, ?>) {
      ArrayList<Object> list = new ArrayList<>(((Map<?, ?>) array).entrySet());
      getter = list::get;
      size = list.size();
    } else if (array instanceof Indexable) {
      @SuppressWarnings("unchecked")
      Indexable<Object> indexable = (Indexable<Object>) array;
      getter = indexable;
      size = indexable.size();
    } else {
      getter = List.of(array)::get;
      size = 1;
    }
  }


  @Nullable
  @Override
  public Object get(@Nonnull String name) {
    Object value = getLocal(name);
    return (value != null) ? value : parent.get(name);
  }


  /**
   * Get the specified value from the list or array. Note, if the index is out of range, then the parent is queried for a suitable value.
   *
   * @param index the index in the list or array
   *
   * @return the value
   */
  public Object get(int index) {
    String name = Integer.toString(index);
    Object r = otherValues.get(name);
    if (r != null) {
      return r;
    }
    try {
      return getter.apply(index);
    } catch (IndexOutOfBoundsException e) {
      return parent.get(name);
    }
  }


  @Nullable
  @Override
  public Object getLocal(@Nonnull String name) {
    Object other = otherValues.get(name);
    if (other != null) {
      return other;
    }

    if (name.equals(P_SIZE)) {
      return size();
    }
    if (name.equals(P_IS_EMPTY)) {
      return size() == 0;
    }
    try {
      int index = Integer.parseInt(name);
      return getter.apply(index);
    } catch (NumberFormatException | IndexOutOfBoundsException e) {
      // nothing local
      return null;
    }
  }


  /**
   * Test if this provider is providing from a list and only a list. If values have been put into this provider that neither over-write nor extend the
   * under-lying list, then this is not a pure list.
   */
  public boolean isPureList() {
    if (otherValues.isEmpty()) {
      return true;
    }

    int maxSize = size + otherValues.size();
    BitSet bitSet = new BitSet(maxSize);
    bitSet.set(0, size);
    for (String key : otherValues.keySet()) {
      if (INDEX.matcher(key).matches()) {
        int v = Integer.parseInt(key);
        if (v < bitSet.size()) {
          bitSet.set(v);
        } else {
          return false;
        }
      } else {
        // Definitely not an index
        return false;
      }
    }

    return bitSet.nextClearBit(0) <= maxSize;
  }


  @Override
  public void put(@Nonnull String name, @Nullable Object newValue) {
    otherValues.put(name, newValue);
  }


  /**
   * Get the size of this list.
   *
   * @return the size of the list
   */
  public int size() {
    int s = size;
    if (!otherValues.isEmpty()) {
      while (otherValues.containsKey(Integer.toString(s))) {
        s++;
      }
    }
    return s;
  }


  @Override
  public void visit(BiConsumer<String, Object> visitor) {
    final HashSet<String> visited = new HashSet<>();
    for (var e : otherValues.entrySet()) {
      String key = e.getKey();
      visited.add(key);
      visitor.accept(key, e.getValue());
    }

    int s = size();
    if (visited.add(P_SIZE)) {
      visitor.accept(P_SIZE, s);
    }
    if (visited.add(P_IS_EMPTY)) {
      visitor.accept(P_IS_EMPTY, s == 0);
    }

    for (int i = 0; i < size; i++) {
      String k = Integer.toString(i);
      if (visited.add(k)) {
        visitor.accept(k, getter.apply(i));
      }
    }
  }

}