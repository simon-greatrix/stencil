package com.pippsford.stencil.apply;

import java.math.BigDecimal;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

import com.pippsford.common.TypeSafeMap;
import com.pippsford.stencil.value.Data;

/**
 * A parameter that can be used as input to a ValueProcessor.
 *
 * @author Simon Greatrix on 20/01/2021.
 */
public abstract class Parameter implements Comparable<Parameter> {

  private static final Pattern IS_NUMBER = Pattern.compile("[+-]?(?:[0-9]*\\.)?[0-9]+(?:[Ee][+-]?[0-9]+)?");

  /** The raw value from the stencil. */
  protected final String rawValue;


  /**
   * New instance.
   *
   * @param rawValue the raw value from the stencil
   */
  protected Parameter(String rawValue) {
    this.rawValue = rawValue;
  }


  /**
   * Convert the value of this parameter to a Boolean.
   *
   * @return this parameter's value as a Boolean.
   */
  public boolean asBoolean() {
    return TypeSafeMap.asBoolean(getValue());
  }


  /**
   * Convert the value of this parameter to a Number.
   *
   * @return this parameter's value as a Number.
   */
  public BigDecimal asNumber() {
    return new BigDecimal(asString());
  }


  /**
   * Convert the value of this parameter to a String.
   *
   * @return this parameter's value as a String
   */
  public String asString() {
    return TypeSafeMap.asString(getValue());
  }


  /**
   * Compare to another parameter. Nulls sort last. If both are numeric, compare numerically. Otherwise, sort in default String order.
   */
  @Override
  public int compareTo(@Nonnull Parameter other) {
    if (isNumber() && other.isNumber()) {
      BigDecimal thisDecimal = asNumber();
      BigDecimal otherDecimal = other.asNumber();
      return thisDecimal.compareTo(otherDecimal);
    }

    Object thisValue = getValue();
    Object otherValue = other.getValue();
    if (thisValue == null) {
      return (otherValue == null) ? 0 : 1;
    }
    if (otherValue == null) {
      return -1;
    }

    String thisText = thisValue.toString();
    String otherText = otherValue.toString();

    return thisText.compareTo(otherText);
  }


  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Parameter)) {
      return false;
    }
    if (other == this) {
      return true;
    }
    return compareTo((Parameter) other) == 0;
  }


  /**
   * Get the raw value for this parameter. This could be a literal, or a key.
   *
   * @return the raw value
   */
  public String getRaw() {
    return rawValue;
  }


  /**
   * Get the value of this parameter.
   *
   * @return the value
   */
  public abstract Object getValue();


  @Override
  public int hashCode() {
    // NB: This does not work well with BigDecimal as they could have the same value but different scales.
    return rawValue.hashCode();
  }


  /**
   * Does this parameter have a fixed literal value?.
   *
   * @return true if the value is fixed
   */
  public abstract boolean isLiteral();


  /**
   * Is the value of this parameter null?.
   *
   * @return true if this is null
   */
  public boolean isNull() {
    return getValue() == null;
  }


  /**
   * Is this parameter a numeric value?.
   *
   * @return true if this is numeric
   */
  public boolean isNumber() {
    Object value = getValue();
    if (value instanceof Number) {
      return true;
    }
    if (value instanceof String) {
      return IS_NUMBER.matcher((String) value).matches();
    }
    return false;
  }


  /**
   * Create a parameter instance which takes a value from the supplied data, if appropriate.
   *
   * @param data the data to take a value from
   *
   * @return the new instance
   */
  public abstract Parameter withData(Data data);

}
