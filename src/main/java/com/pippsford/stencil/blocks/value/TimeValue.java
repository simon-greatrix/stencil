package com.pippsford.stencil.blocks.value;

import java.time.format.DateTimeFormatter;

import com.pippsford.stencil.blocks.BlockTypes;
import com.pippsford.stencil.escape.Escape;

/**
 * A time value, appropriately formatted.
 */
public class TimeValue extends BaseDateTimeValue {

  /**
   * Time value renderer.
   *
   * @param escapeStyle escaping style to use
   * @param param       parameter to render
   * @param timeStyle   the format selection
   */
  public TimeValue(Escape escapeStyle, String param, String timeStyle) {
    super(BlockTypes.VALUE_TIME, escapeStyle, param, timeStyle, DateTimeFormatter::ofLocalizedTime);
  }

}
