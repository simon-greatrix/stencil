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

  /**
   * Handlers to convert input into entry collections.
   */
  private static final List<EntriesHandler> HANDLERS = List.of(
      // Handle nulls
      new EntriesHandler() {
        @Override
        public boolean canHandle(Object value) {
          return value == null;
        }


        @Override
        public Object handle(Object object) {
          return null;
        }
      },

      // Handle maps
      new EntriesHandler() {
        @Override
        public boolean canHandle(Object value) {
          return value instanceof Map<?, ?>;
        }


        @Override
        public Object handle(Object value) {
          Map<?, ?> map = (Map<?, ?>) value;
          ArrayList<Map.Entry<?, ?>> list = new ArrayList<>(map.entrySet());
          list.sort(Comparator.comparing(e -> String.valueOf(e.getKey())));
          return list;
        }
      },

      // Handle anything iterable
      new EntriesHandler() {
        @Override
        public boolean canHandle(Object value) {
          return value instanceof Iterable<?>;
        }


        @Override
        public Object handle(Object value) {
          return collect(((Iterable<?>) value).iterator());
        }
      },

      // Explicit iterators
      new EntriesHandler() {
        @Override
        public boolean canHandle(Object value) {
          return value instanceof Iterator<?>;
        }


        @Override
        public Object handle(Object value) {
          return collect((Iterator<?>) value);
        }
      },

      // Enumerators
      new EntriesHandler() {
        @Override
        public boolean canHandle(Object value) {
          return value instanceof Enumeration<?>;
        }


        @Override
        public Object handle(Object value) {
          return collect(((Enumeration<?>) value).asIterator());
        }
      },

      // Arrays
      new EntriesHandler() {
        @Override
        public boolean canHandle(Object value) {
          return value.getClass().isArray();
        }


        @Override
        public Object handle(Object value) {
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
              });
        }
      }
  );

  /** The instance of this function. */
  public static final ValueProcessor INSTANCE = new EntriesFunction();



  private interface EntriesHandler {

    boolean canHandle(Object value);

    Object handle(Object value);

  }


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
    ValueProcessor.verifyArity(arguments, 1, 1);

    Object value = arguments[0].getValue();

    // Try the handlers
    for (EntriesHandler entriesHandler : HANDLERS) {
      if (entriesHandler.canHandle(value)) {
        return entriesHandler.handle(value);
      }
    }

    // Convert as a bean/record
    ValueProvider provider = ValueAccessor.makeProvider(data.getProvider(), value);
    ArrayList<Map.Entry<String, Object>> list = new ArrayList<>();
    provider.visit((k, v, s) -> {
      if (s) {
        list.add(new SimpleEntry<>(k, v));
      }
    });
    return list;
  }

}
