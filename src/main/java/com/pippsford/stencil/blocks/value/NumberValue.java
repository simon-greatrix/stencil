package com.pippsford.stencil.blocks.value;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.function.Function;

import com.pippsford.common.TypeSafeMap;
import com.pippsford.stencil.blocks.BlockTypes;
import com.pippsford.stencil.escape.Escape;
import com.pippsford.stencil.value.Data;

/**
 * A value formatter that used the Java NumberFormat formats.
 *
 * @author Simon Greatrix on 28/12/2020.
 */
public class NumberValue extends BaseValue {

  /** Function to generate a locale-appropriate formatter. NumberFormat instances are not thread safe, so we create a new one every time. */
  private final Function<Locale, NumberFormat> formatterFunction;


  /**
   * Create a new block for formatting a number using the Java text Decimal Format system.
   *
   * @param escapeStyle the escaping style to apply after formatting.
   * @param param       the parameter to format
   * @param format      the format to apply
   */
  public NumberValue(Escape escapeStyle, String param, String format) {
    super(BlockTypes.VALUE_NUMBER, escapeStyle, param);
    if (format == null) {
      formatterFunction = NumberFormat::getNumberInstance;
      return;
    }

    String upper = format.toUpperCase(Locale.ENGLISH);
    switch (upper) {
      case "CURRENCY":
        formatterFunction = NumberFormat::getCurrencyInstance;
        return;
      case "PERCENT":
        formatterFunction = NumberFormat::getPercentInstance;
        return;
      case "INTEGER":
        formatterFunction = NumberFormat::getIntegerInstance;
        return;
      case "NUMBER":
        formatterFunction = NumberFormat::getNumberInstance;
        return;
      default:
        // falls through
    }

    // Test the format to catch errors at compile time.
    new DecimalFormat(format, DecimalFormatSymbols.getInstance());

    formatterFunction = locale -> new DecimalFormat(format, DecimalFormatSymbols.getInstance(locale));
  }


  @Override
  protected String getText(Locale locale, ZoneId zoneId, Data data) {
    Number datum = TypeSafeMap.asNumber(data.get(param));
    if (datum == null) {
      return "";
    }

    NumberFormat format = formatterFunction.apply(locale);
    return format.format(datum);
  }

}
