package com.pippsford.stencil.apply;

import java.lang.reflect.Array;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.ValueAccessor;
import com.pippsford.stencil.value.ValueProvider;

/**
 * Extract an entry set from a map, bean, or record.
 */
public class EntriesFunction implements ValueProcessor {

  /** The instance of this function. */
  public static final ValueProcessor INSTANCE = new EntriesFunction();


  private static List<Map.Entry<String, ?>> collect(Iterator<?> iterator) {
    ArrayList<Map.Entry<String, ?>> list = new ArrayList<>();
    int index = 0;
    while (iterator.hasNext()) {
      list.add(new SimpleEntry<>(String.valueOf(index), iterator.next()));
      index++;
    }
    return list;
  }


  private EntriesFunction() {
  }


  @Override
  public Object apply(Data data, Parameter[] arguments) {
    if (arguments.length != 1) {
      throw new IllegalArgumentException("Must provide exactly one argument to this function");
    }

    Object value = arguments[0].getValue();

    if (value == null) {
      return null;
    }

    // Maps are obvious
    if (value instanceof Map<?, ?> map) {
      ArrayList<Map.Entry<?, ?>> list = new ArrayList<>(map.entrySet());
      list.sort(Comparator.comparing(e -> String.valueOf(e.getKey())));
      return list;
    }

    // Other collections are number -> value mappings

    // Anything iterable
    if (value instanceof Iterable<?> collection) {
      return collect(collection.iterator());
    }

    // An iterator
    if (value instanceof Iterator<?> iterator) {
      return collect(iterator);
    }

    // An enumeration
    if (value instanceof Enumeration<?> enumeration) {
      return collect(enumeration.asIterator());
    }

    // An array
    if (value.getClass().isArray()) {
      return collect(
          new Iterator<>() {
            private final int length = Array.getLength(value);

            private int index = 0;


            @Override
            public boolean hasNext() {
              return index < length;
            }


            @Override
            public Object next() {
              if (hasNext()) {
                return Array.get(value, index++);
              }
              throw new NoSuchElementException();
            }
          }
      );
    }

    // Convert as a bean/record
    ValueProvider provider = ValueAccessor.makeProvider(data.getProvider(), value);
    ArrayList<Map.Entry<String, Object>> list = new ArrayList<>();
    provider.visit((k, v) -> list.add(new SimpleEntry<>(k, v)));
    return list;
  }

}
