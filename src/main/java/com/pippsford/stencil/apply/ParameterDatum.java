package com.pippsford.stencil.apply;

import com.pippsford.stencil.value.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A value processor function that draws from a Data instance.
 *
 * @author Simon Greatrix on 20/01/2021.
 */
public class ParameterDatum extends Parameter {

  private final Data myData;


  /**
   * New instance.
   *
   * @param data     the data to get the value from
   * @param rawValue the raw value (which is the path to the value)
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public ParameterDatum(Data data, String rawValue) {
    super(rawValue);
    myData = data;
  }


  @Override
  public Object getValue() {
    return myData.get(rawValue).value();
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
