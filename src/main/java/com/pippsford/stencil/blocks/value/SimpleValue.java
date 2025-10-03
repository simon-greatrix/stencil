package com.pippsford.stencil.blocks.value;

import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.blocks.BlockTypes;
import com.pippsford.stencil.escape.Escape;
import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.OptionalValue;

/**
 * A value.
 */
public class SimpleValue extends BaseValue {

  /**
   * New value renderer.
   *
   * @param template    the definition in the stencil
   * @param escapeStyle escaping style to use
   * @param param       parameter to render in DataElementHelper format
   */
  public SimpleValue(String template, Escape escapeStyle, String param) {
    super(BlockTypes.VALUE, template, escapeStyle, param);
  }


  @Override
  protected String getText(Locale locale, ZoneId zoneId, Data data) {
    OptionalValue value = data.get(param);
    return value.isPresent() ? String.valueOf(value.value()) : template;
  }

}
