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
   * @param template    the definition in the stencil
   * @param escapeStyle escaping style to use
   * @param param       parameter to render
   * @param timeStyle   the format selection
   */
  public TimeValue(String template, Escape escapeStyle, String param, String timeStyle) {
    super(BlockTypes.VALUE_TIME, template, escapeStyle, param, timeStyle, DateTimeFormatter::ofLocalizedTime);
  }

}
