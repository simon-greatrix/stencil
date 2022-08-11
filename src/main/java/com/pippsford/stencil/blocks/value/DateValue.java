package com.pippsford.stencil.blocks.value;

import java.time.format.DateTimeFormatter;

import com.pippsford.stencil.blocks.BlockTypes;
import com.pippsford.stencil.escape.Escape;

/**
 * A date value, formatted according to a single formatting rule.
 */
public class DateValue extends BaseDateTimeValue {

  /**
   * Date value renderer.
   *
   * @param param     parameter to render
   * @param dateStyle the format selection
   */
  public DateValue(Escape escapeStyle, String param, String dateStyle) {
    super(BlockTypes.VALUE_DATE, escapeStyle, param, dateStyle, DateTimeFormatter::ofLocalizedDate);
  }

}
