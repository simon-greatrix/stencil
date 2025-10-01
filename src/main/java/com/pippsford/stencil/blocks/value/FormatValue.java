package com.pippsford.stencil.blocks.value;

import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.blocks.BlockTypes;
import com.pippsford.stencil.escape.Escape;
import com.pippsford.stencil.value.Data;

/**
 * A value formatter using the {@code String.format} style.
 *
 * @author Simon Greatrix on 28/12/2020.
 */
public class FormatValue extends BaseValue {

  private final String format;


  /**
   * New instance.
   *
   * @param escapeStyle the escape style
   * @param param       the parameter to format
   * @param format      the format specifier
   */
  public FormatValue(Escape escapeStyle, String param, String format) {
    super(BlockTypes.VALUE_FORMAT, escapeStyle, param);
    this.format = format;
  }


  @Override
  protected String getText(Locale locale, ZoneId zoneId, Data data) {
    Object datum = data.get(param);
    if (datum == null) {
      return "";
    }

    return String.format(locale, format, datum);
  }

}
