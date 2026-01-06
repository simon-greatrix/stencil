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
   * Verify the arity of a function's input parameters and throw an {@code IllegalArgumentException} if the wrong number of arguments are specified.
   *
   * @param arguments the argument array
   * @param min       the minimum number of arguments allowed
   * @param max       the maximum number of arguments allowed
   */
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
