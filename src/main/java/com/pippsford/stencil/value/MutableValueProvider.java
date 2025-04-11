package com.pippsford.stencil.value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A value provider which can be updated.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
public interface MutableValueProvider extends ValueProvider {

  /**
   * Set a new value into this provider which will then be retrievable by the get method. Note that this does not normally change the original data passed
   * into the stencil.
   *
   * @param name     the name of the value to set. The name must not contain periods.
   * @param newValue the actual new value
   */
  void put(@Nonnull String name, @Nullable Object newValue);

}
