package com.pippsford.stencil.apply;

import com.pippsford.stencil.value.Data;

/**
 * Apply some process to the currently provided values.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
@FunctionalInterface
public interface ValueProcessor {

  static void verifyArity(Parameter[] arguments, int min, int max) {
    if (min <= arguments.length && arguments.length <= max) {
      return;
    }
    if (min == max) {
      throw new IllegalArgumentException("Must provide exactly " + min + " arguments to this function");
    }
    if (arguments.length < min) {
      throw new IllegalArgumentException("Must provide at least " + min + " arguments");
    }
    throw new IllegalArgumentException("Must provide at most " + max + " arguments");
  }

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
