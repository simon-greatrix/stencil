package com.pippsford.stencil.apply;

import com.pippsford.stencil.value.Data;

/**
 * A value processor function that draws from a Data instance.
 *
 * @author Simon Greatrix on 20/01/2021.
 */
public class ParameterDatum extends Parameter {

  private final Data myData;


  public ParameterDatum(Data data, String rawValue) {
    super(rawValue);
    myData = data;
  }


  @Override
  public Object getValue() {
    return myData.get(rawValue);
  }


  @Override
  public boolean isLiteral() {
    return false;
  }


  public String toString() {
    return "Datum[ " + rawValue + " -> " + getValue() + " ]";
  }


  @Override
  public Parameter withData(Data newData) {
    return new ParameterDatum(newData, getRaw());
  }

}
