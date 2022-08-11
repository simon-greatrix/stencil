package com.pippsford.stencil.blocks.value;

import java.time.format.DateTimeFormatter;

import com.pippsford.stencil.blocks.BlockTypes;
import com.pippsford.stencil.escape.Escape;

/**
 * A date-time value, formatted according to a single formatting rule.
 */
public class DateTimeValue extends BaseDateTimeValue {

  /**
   * Date-time value renderer.
   *
   * @param param     parameter to render
   * @param dateStyle the format selection
   */
  public DateTimeValue(Escape escapeStyle, String param, String dateStyle) {
    super(BlockTypes.VALUE_DATE_TIME, escapeStyle, param, dateStyle, DateTimeFormatter::ofLocalizedDateTime);
  }

}
