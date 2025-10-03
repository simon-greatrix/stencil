package com.pippsford.stencil.blocks.value;

import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.blocks.BlockTypes;
import com.pippsford.stencil.escape.Escape;
import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.OptionalValue;

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
   * @param template    the definition in the stencil
   * @param param       the parameter to format
   * @param format      the format specifier
   */
  public FormatValue(String template, Escape escapeStyle, String param, String format) {
    super(BlockTypes.VALUE_FORMAT, template, escapeStyle, param);
    this.format = format;
  }


  @Override
  protected String getText(Locale locale, ZoneId zoneId, Data data) {
    OptionalValue datum = data.get(param);
    if (datum.isMissing()) {
      return template;
    }

    return String.format(locale, format, datum.value());
  }

}
