package com.pippsford.stencil.value;

import java.util.Map.Entry;
import java.util.function.BiConsumer;
import jakarta.annotation.Nonnull;

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

  private final Entry<?,?> entry;

  /**
   * New instance.
   *
   * @param parent parent value for inheritance
   * @param index  the index of this entry in the list
   * @param size   the number of entries in the list
   * @param value  the value at this point in the list
   */
  public ListEntryValueProvider(ValueProvider parent, int index, int size, Object value) {
    this.parent = parent;
    this.index = index;
    this.size = size;
    this.value = value;
    this.entry = value instanceof Entry<?,?> ? (Entry<?,?>) value : null;
  }


  @Override
  @Nonnull
  public OptionalValue get(@Nonnull String name) {
    return getLocal(name).orDefault(() -> parent.get(name));
  }


  @Override
  @Nonnull
  public OptionalValue getLocal(@Nonnull String name) {
    return switch (name) {
      case "value" -> OptionalValue.of(entry != null ? entry.getValue() : value);
      case "index" -> OptionalValue.of(index);
      case "size" -> OptionalValue.of(size);
      case "key" -> entry != null ? OptionalValue.of(entry.getKey()) : OptionalValue.absent();
      default -> OptionalValue.absent();
    };
  }


  @Override
  public void visit(BiConsumer<String, Object> visitor) {
    if (entry!=null) {
      visitor.accept("key", entry.getKey());
      visitor.accept("value", entry.getValue());
    } else {
      visitor.accept("value", value);
    }

    visitor.accept("index", index);
    visitor.accept("size", size);
  }

}
