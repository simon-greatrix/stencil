package com.pippsford.stencil.apply;

import com.pippsford.stencil.value.Data;

/**
 * A function that performs standard logical comparisons to set a boolean parameter.
 *
 * @author Simon Greatrix on 20/01/2021.
 */
public class IsFunction implements ValueProcessor {

  /** Singleton instance of this function. */
  public static final IsFunction INSTANCE = new IsFunction();


  private IsFunction() {
    // do nothing
  }


  @Override
  public Object apply(Data valueProvider, Parameter[] arguments) {
    ValueProcessor.verifyArity(arguments, 3, 3);

    if (arguments[1].isNull()) {
      throw new IllegalArgumentException("No comparison operator specified");
    }

    Parameter left = arguments[0];
    Parameter right = arguments[2];

    return switch (arguments[1].asString()) {
      case "EQ" -> left.compareTo(right) == 0;
      case "NE" -> left.compareTo(right) != 0;
      case "LT" -> left.compareTo(right) < 0;
      case "LE" -> left.compareTo(right) <= 0;
      case "GT" -> left.compareTo(right) > 0;
      case "GE" -> left.compareTo(right) >= 0;
      default -> throw new IllegalArgumentException("Unrecognized operator: " + arguments[1].asString());
    };
  }

}
