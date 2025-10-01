package com.pippsford.stencil.apply;

import com.pippsford.stencil.value.Data;

/**
 * Apply some process to the currently provided values.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
@FunctionalInterface
public interface ValueProcessor {

  /**
   * Apply the process.
   *
   * @param valueProvider the data to process
   * @param arguments     the arguments into the process
   *
   * @return the result of the process
   */
  Object apply(Data valueProvider, Parameter[] arguments);

}
