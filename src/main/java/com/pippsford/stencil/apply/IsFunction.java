package com.pippsford.stencil.apply;

import com.pippsford.stencil.value.Data;

/**
 * A function that performs standard logical comparisons to set a boolean parameter.
 *
 * @author Simon Greatrix on 20/01/2021.
 */
public class IsFunction implements ValueProcessor {

  public static final IsFunction INSTANCE = new IsFunction();


  private IsFunction() {
    // do nothing
  }


  @Override
  public Object apply(Data valueProvider, Parameter[] arguments) {
    if (arguments.length != 3) {
      throw new IllegalArgumentException("Must provide exactly three arguments to this function");
    }
    if (arguments[1].isNull()) {
      throw new IllegalArgumentException("No comparison operator specified");
    }

    Parameter left = arguments[0];
    Parameter right = arguments[2];

    switch (arguments[1].asString()) {
      case "EQ":
        return left.compareTo(right) == 0;
      case "NE":
        return left.compareTo(right) != 0;
      case "LT":
        return left.compareTo(right) < 0;
      case "LE":
        return left.compareTo(right) <= 0;
      case "GT":
        return left.compareTo(right) > 0;
      case "GE":
        return left.compareTo(right) >= 0;
      default:
        throw new IllegalArgumentException("Unrecognized operator: " + arguments[1].asString());
    }
  }

}
