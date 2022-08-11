package com.pippsford.stencil.apply;

import com.pippsford.stencil.value.Data;

/**
 * A ValueProcesser parameter which is a literal value.
 *
 * @author Simon Greatrix on 20/01/2021.
 */
public class ParameterLiteral extends Parameter {

  public ParameterLiteral(String value) {
    super(value);
  }


  @Override
  public Object getValue() {
    return getRaw();
  }


  @Override
  public boolean isLiteral() {
    return true;
  }


  public String toString() {
    return "Literal[" + getValue() + "]";
  }


  @Override
  public Parameter withData(Data data) {
    return this;
  }

}
