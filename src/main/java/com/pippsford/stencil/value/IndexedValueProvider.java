package com.pippsford.stencil.value;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.regex.Pattern;

import jakarta.annotation.Nonnull;

/**
 * A value provider that allows access to a list. List's have a property for every index, plus "size" and "isEmpty".
 *
 * @author Simon Greatrix on 03/01/2021.
 */
public class IndexedValueProvider implements MutableValueProvider {

  static final String P_IS_EMPTY = "isEmpty";

  static final String P_SIZE = "size";

  private static final Pattern INDEX = Pattern.compile("[1-9][0-9]*");


  private static <T extends Comparable<T>> int compare(T a, T b) {
    return a.compareTo(b);
  }


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
  @SuppressWarnings("unchecked")
  public IndexedValueProvider(ValueProvider parent, Object array) {
    this.parent = parent;

    if (array instanceof Indexable) {
      Indexable<Object> indexable = (Indexable<Object>) array;
      getter = indexable;
      size = indexable.size();
      return;
    }

    if (array instanceof List<?> list) {
      getter = list::get;
      size = list.size();
      return;
    }

    if (array.getClass().isArray()) {
      getter = i -> Array.get(array, i);
      size = Array.getLength(array);
      return;
    }

    Iterator<Object> iterator;

    if (array instanceof Iterator<?>) {
      iterator = (Iterator<Object>) array;

    } else if (array instanceof Iterable<?> iterable) {
      iterator = (Iterator<Object>) iterable.iterator();

    } else if (array instanceof Enumeration<?> enumeration) {
      iterator = (Iterator<Object>) enumeration.asIterator();

    } else {
      // Final fallback - no idea how to loop over this
      getter = List.of(array)::get;
      size = 1;
      return;
    }

    ArrayList<Object> list = new ArrayList<>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    getter = list::get;
    size = list.size();
  }


  @Override
  @Nonnull
  public OptionalValue get(@Nonnull String name) {
    return getLocal(parseInt(name), name).orDefault(() -> parent.get(name));
  }


  /**
   * Get the specified value from the list or array. Note, if the index is out of range, then the parent is queried for a suitable value.
   *
   * @param index the index in the list or array
   *
   * @return the value
   */
  public OptionalValue get(int index) {
    String name = Integer.toString(index);
    return getLocal(index, name).orDefault(() -> parent.get(name));
  }


  @Override
  @Nonnull
  public OptionalValue getLocal(@Nonnull String name) {
    return getLocal(parseInt(name), name);
  }


  private OptionalValue getLocal(Integer index, @Nonnull String name) {
    Object other = otherValues.get(name);
    if (other != null) {
      return OptionalValue.of(other);
    }

    if (index != null) {
      if (0 <= index && index < size) {
        return OptionalValue.of(getter.apply(index));
      }
    } else {
      if (name.equals(P_SIZE)) {
        return OptionalValue.of(size());
      }

      if (name.equals(P_IS_EMPTY)) {
        return OptionalValue.of(size() == 0);
      }
    }

    // nothing local
    return OptionalValue.absent();
  }


  /**
   * Test if this provider is providing from a list and only a list. If values have been put into this provider that neither over-write nor extend the
   * under-lying list, then this is not a pure list.
   *
   * @return true if this is a list and only a list
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


  private Integer parseInt(String name) {
    try {
      return Integer.parseInt(name);
    } catch (NumberFormatException e) {
      return null;
    }
  }


  @Override
  public void put(@Nonnull String name, @Nonnull Object newValue) {
    if (name.indexOf('.') != -1) {
      throw new IllegalArgumentException("Property name must not contain a '.'");
    }

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
