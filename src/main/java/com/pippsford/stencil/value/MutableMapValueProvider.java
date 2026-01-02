package com.pippsford.stencil.value;

import static com.pippsford.stencil.value.IndexedValueProvider.P_IS_EMPTY;
import static com.pippsford.stencil.value.IndexedValueProvider.P_SIZE;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nonnull;

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


  @Override
  @Nonnull
  public OptionalValue get(@Nonnull String name) {
    return getLocal(name).orDefault(() -> parent.get(name));
  }


  @Override
  @Nonnull
  public OptionalValue getLocal(@Nonnull String name) {
    Object value = map.get(name);
    if (value != null || map.containsKey(name)) {
      return OptionalValue.of(value);
    }
    return OptionalValue.absent();
  }


  @Override
  public void put(@Nonnull String name, @Nonnull Object newValue) {
    map.put(name, newValue);
  }


  @Override
  public void visit(ValueVisitor visitor) {
    map.forEach((k, v) -> {
      if (k != null) {
        visitor.visit(k, v, true);
      }
    });
    if (!map.containsKey(P_IS_EMPTY)) {
      visitor.visit(P_IS_EMPTY, map.isEmpty(), false);
    }
    if (!map.containsKey(P_SIZE)) {
      visitor.visit(P_SIZE, map.size(), false);
    }
  }

}
