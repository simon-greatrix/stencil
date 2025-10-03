package com.pippsford.stencil.value;

import java.util.function.Supplier;

/** An optional value that can be null. */
public record OptionalValue(Object value, boolean isPresent) {

  static OptionalValue absent() {
    return new OptionalValue(null, false);
  }


  static OptionalValue of(Object value) {
    return new OptionalValue(value, true);
  }


  public boolean isMissing() {
    return !isPresent;
  }


  /**
   * Get this value if it is present, otherwise consult the supplier.
   *
   * @param defaultSupplier the supplier
   *
   * @return the first present value.
   */
  public OptionalValue orDefault(Supplier<OptionalValue> defaultSupplier) {
    if (isPresent || defaultSupplier == null) {
      return this;
    }
    return defaultSupplier.get();
  }


  public Object safeValue() {
    if (isMissing()) {
      throw new IllegalStateException("Value is missing");
    }
    return value;
  }

}
