package com.pippsford.stencil.apply;

import com.pippsford.stencil.value.Data;

/**
 * A value processor parameter which is explicitly null.
 *
 * @author Simon Greatrix on 21/01/2021.
 */
public class ParameterNull extends Parameter {

  public static final ParameterNull INSTANCE = new ParameterNull();


  private ParameterNull() {
    super(null);
  }


  @Override
  public Object getValue() {
    return null;
  }


  @Override
  public boolean isLiteral() {
    return false;
  }


  public String toString() {
    return "<null>";
  }


  @Override
  public Parameter withData(Data data) {
    return this;
  }

}
