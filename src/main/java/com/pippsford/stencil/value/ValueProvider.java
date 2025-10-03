package com.pippsford.stencil.value;

import java.util.function.BiConsumer;
import jakarta.annotation.Nonnull;

/**
 * The basic requirements for a value provider.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
public interface ValueProvider {

  /** A provider that always returns absent. Used for handling missing values. */
  ValueProvider NULL_VALUE_PROVIDER = new ValueProvider() {
    @Override
    @Nonnull
    public OptionalValue get(@Nonnull String name) {
      return OptionalValue.absent();
    }


    @Override
    @Nonnull
    public OptionalValue getLocal(@Nonnull String key) {
      return OptionalValue.absent();
    }


    @Override
    public void visit(BiConsumer<String, Object> visitor) {
      // do nothing
    }
  };


  /**
   * Get the named value.
   *
   * @param name the name of the value.
   *
   * @return the value
   */
  @Nonnull
  OptionalValue get(@Nonnull String name);


  /**
   * Get the named value, ignoring inherited values from the parent scope.
   *
   * @param key the name of the value
   *
   * @return the value
   */
  @Nonnull
  OptionalValue getLocal(@Nonnull String key);


  /**
   * Visit every value in this provider.
   *
   * @param visitor visitor
   */
  void visit(BiConsumer<String, Object> visitor);

}
