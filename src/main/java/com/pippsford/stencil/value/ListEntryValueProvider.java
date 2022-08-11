package com.pippsford.stencil.value;

import java.util.Map.Entry;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provide a value from a list or other source that can be accessed via an integer.
 *
 * @author Simon Greatrix on 06/01/2021.
 */
public class ListEntryValueProvider implements ValueProvider {

  private final int index;

  private final ValueProvider parent;

  private final int size;

  private final Object value;


  /**
   * New instance.
   *
   * @param index the index of this entry in the list
   * @param size  the number of entries in the list
   * @param value the value at this point in the list
   */
  public ListEntryValueProvider(ValueProvider parent, int index, int size, Object value) {
    this.parent = parent;
    this.index = index;
    this.size = size;
    this.value = value;
  }


  @Nullable
  @Override
  public Object get(@Nonnull String name) {
    Object value = getLocal(name);
    return (value != null) ? value : parent.get(name);
  }


  @Nullable
  @Override
  public Object getLocal(String name) {
    switch (name) {
      case "value":
        return (value instanceof Entry<?, ?>) ? ((Entry<?, ?>) value).getValue() : value;
      case "index":
        return index;
      case "size":
        return size;
      case "key":
        return (value instanceof Entry<?, ?>) ? ((Entry<?, ?>) value).getKey() : parent.get(name);
      default:
        return null;
    }
  }


  @Override
  public void visit(BiConsumer<String, Object> visitor) {
    if (value instanceof Entry<?, ?>) {
      Entry<?, ?> entry = (Entry<?, ?>) value;
      visitor.accept("key", entry.getKey());
      visitor.accept("value", entry.getValue());
    } else {
      visitor.accept("value", value);
    }

    visitor.accept("index", index);
    visitor.accept("size", size);
  }

}
