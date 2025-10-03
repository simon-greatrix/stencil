package com.pippsford.stencil.value;

import static com.pippsford.stencil.value.IndexedValueProvider.P_IS_EMPTY;
import static com.pippsford.stencil.value.IndexedValueProvider.P_SIZE;

import java.util.Map;
import java.util.function.BiConsumer;
import jakarta.annotation.Nonnull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A value provider backed by a map.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
public class MapValueProvider implements ValueProvider {

  private final Map<?, ?> map;

  private final ValueProvider parent;


  /**
   * New instance. The passed in map provides the values and is treated as immutable.
   *
   * @param parent parent value provider for inheritance
   * @param map    the map.
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public MapValueProvider(ValueProvider parent, Map<?, ?> map) {
    this.parent = parent;
    this.map = map;
  }


  @Override
  @Nonnull
  public OptionalValue get(@Nonnull String name) {
    return getLocal(name).orDefault(() -> parent.get(name));
  }


  @Override
  @Nonnull
  public OptionalValue getLocal(@Nonnull String name) {
    Object r = map.get(name);
    if (r == null && !map.containsKey(name)) {
      if (name.equals(P_IS_EMPTY)) {
        r = map.isEmpty();
      } else if (name.equals(P_SIZE)) {
        r = map.size();
      } else {
        return OptionalValue.absent();
      }
    }
    return OptionalValue.of(r);
  }


  @Override
  public void visit(BiConsumer<String, Object> visitor) {
    map.forEach((k, v) -> {
      if (k instanceof String) {
        visitor.accept((String) k, v);
      }
    });
    if (!map.containsKey(P_IS_EMPTY)) {
      visitor.accept(P_IS_EMPTY, map.isEmpty());
    }
    if (!map.containsKey(P_SIZE)) {
      visitor.accept(P_SIZE, map.size());
    }
  }

}
