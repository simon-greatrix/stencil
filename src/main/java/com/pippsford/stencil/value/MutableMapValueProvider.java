package com.pippsford.stencil.value;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provide values from a parent provider and allow them to be over-ridden and extended by storing values in a map.
 *
 * @author Simon Greatrix on 04/01/2021.
 */
public class MutableMapValueProvider implements MutableValueProvider {

  private final Map<String, Object> map = new HashMap<>();

  private final ValueProvider parent;


  /**
   * New instance.
   *
   * @param parent the parent value provider
   */
  public MutableMapValueProvider(ValueProvider parent) {
    this.parent = parent;
  }


  @Nullable
  @Override
  public Object get(@Nonnull String name) {
    Object r = map.get(name);
    return (r != null) ? r : parent.get(name);
  }


  @Nullable
  @Override
  public Object getLocal(@Nonnull String name) {
    return map.get(name);
  }


  @Override
  public void put(@Nonnull String name, @Nullable Object newValue) {
    map.put(name, newValue);
  }


  @Override
  public void visit(BiConsumer<String, Object> visitor) {
    map.forEach((k, v) -> {
      if (k != null) {
        visitor.accept(k, v);
      }
    });
  }

}
