package com.pippsford.stencil.apply;

import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.IndexableInt;

/**
 * Helper function for creating "for" loops. The input parameters are the start (inclusive), end (exclusive) and the step size for the "for" loop.
 *
 * @author Simon Greatrix on 21/02/2021.
 */
public class ForFunction implements ValueProcessor {

  /** Singleton instance. */
  public static final ForFunction INSTANCE = new ForFunction();


  private ForFunction() {
    // do nothing
  }


  @Override
  public Object apply(Data valueProvider, Parameter[] arguments) {
    if (arguments.length == 0 || arguments.length > 3) {
      throw new IllegalArgumentException("Must provide one, two or three arguments to this function");
    }
    int start = (arguments.length == 1) ? 0 : arguments[0].asNumber().intValueExact();
    int end = arguments[arguments.length == 1 ? 0 : 1].asNumber().intValueExact();
    int step = (arguments.length == 3) ? arguments[2].asNumber().intValueExact() : 1;
    return new IndexableInt(start, end, step);
  }

}
