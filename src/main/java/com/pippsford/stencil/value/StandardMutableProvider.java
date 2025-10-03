package com.pippsford.stencil.value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiConsumer;

import jakarta.annotation.Nonnull;

/**
 * A value provider that enables mutation of another provider.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
public class StandardMutableProvider implements MutableValueProvider {

  private final Map<String, Object> overrides = new HashMap<>();

  private final ValueProvider values;


  /**
   * New instance.
   *
   * @param valueProvider the original values
   */
  public StandardMutableProvider(@Nonnull ValueProvider valueProvider) {
    values = valueProvider;
  }


  @Override
  @Nonnull
  public OptionalValue get(@Nonnull String name) {
    return getLocal(name).orDefault(() -> values.get(name));
  }


  @Override
  @Nonnull
  public OptionalValue getLocal(@Nonnull String name) {
    Object value = overrides.get(name);
    if (value != null) {
      return OptionalValue.of(value);
    }

    return values.getLocal(name);
  }


  @Override
  public void put(@Nonnull String name, @Nonnull Object newValue) {
    if (name.indexOf('.') != -1) {
      throw new IllegalArgumentException("Property name must not contain a '.'");
    }
    overrides.put(name, newValue);
  }


  @Override
  public void visit(BiConsumer<String, Object> visitor) {
    final HashSet<String> visited = new HashSet<>();
    for (var e : overrides.entrySet()) {
      String key = e.getKey();
      Object value = e.getValue();
      visited.add(key);
      visitor.accept(key, value);
    }

    values.visit((k, v) -> {
      if (visited.add(k)) {
        visitor.accept(k, v);
      }
    });
  }

}
