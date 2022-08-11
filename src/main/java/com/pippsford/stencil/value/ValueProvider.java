package com.pippsford.stencil.value;

import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The basic requirements for a value provider.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
public interface ValueProvider {

  /** A provider that always returns null. Used for handling missing values. */
  ValueProvider NULL_VALUE_PROVIDER = new ValueProvider() {
    @Nullable
    @Override
    public Object get(@Nonnull String name) {
      return null;
    }


    @Nullable
    @Override
    public Object getLocal(String key) {
      return null;
    }


    @Override
    public void visit(BiConsumer<String, Object> visitor) {
      // do nothing
    }
  };


  /**
   * Get the named value. May return null if the value is unspecified or is set to null.
   *
   * @param name the name of the value.
   *
   * @return the value, or null
   */
  @Nullable
  Object get(@Nonnull String name);


  /**
   * Get the named value, ignoring inherited values from the parent scope.
   *
   * @param key the name of the value
   *
   * @return the value, or null
   */
  @Nullable
  Object getLocal(String key);


  /**
   * Visit every value in this provider.
   *
   * @param visitor visitor
   */
  void visit(BiConsumer<String, Object> visitor);

}
