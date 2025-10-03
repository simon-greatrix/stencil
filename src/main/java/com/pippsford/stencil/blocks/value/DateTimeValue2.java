package com.pippsford.stencil.blocks.value;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import com.pippsford.stencil.blocks.BlockTypes;
import com.pippsford.stencil.escape.Escape;
import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.OptionalValue;
import com.pippsford.util.CopyOnWriteMap;

/**
 * A date-time value with a specific date and time format.
 */
public class DateTimeValue2 extends BaseValue {


  /** Format for the date part. */
  private final FormatStyle dateStyle;

  /**
   * The formatter to use.
   */
  private final CopyOnWriteMap<Locale, DateTimeFormatter> formatters = new CopyOnWriteMap<>();

  /** Format for the time part. */
  private final FormatStyle timeStyle;


  /**
   * Date value renderer.
   *
   * @param template    the definition in the stencil
   * @param escapeStyle escaping style to use
   * @param param       parameter to render
   * @param dateStyle   the format selection for the date part
   * @param timeStyle   the format selection for the time part
   */
  public DateTimeValue2(String template, Escape escapeStyle, String param, String dateStyle, String timeStyle) {
    super(BlockTypes.VALUE_DATE_TIME_2, template, escapeStyle, param);
    this.dateStyle = FormatStyle.valueOf(dateStyle.toUpperCase(Locale.ENGLISH));
    this.timeStyle = FormatStyle.valueOf(timeStyle.toUpperCase(Locale.ENGLISH));
  }


  DateTimeFormatter getFormatter(Locale locale) {
    return DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle).withLocale(locale);
  }


  @Override
  protected String getText(Locale locale, ZoneId zoneId, Data data) {
    Object msg = data.get(param).value();
    if (msg==null) {
      return template;
    }

    DateTimeFormatter formatter = formatters.computeIfAbsent(locale, this::getFormatter);
    return formatter.format(BaseDateTimeValue.convert(msg, zoneId));
  }

}
