package com.pippsford.stencil.blocks.value;

import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.blocks.BlockTypes;
import com.pippsford.stencil.escape.Escape;
import com.pippsford.stencil.value.Data;

/**
 * A value.
 */
public class SimpleValue extends BaseValue {

  /**
   * New value renderer.
   *
   * @param escapeStyle escaping style to use
   * @param param       parameter to render in DataElementHelper format
   */
  public SimpleValue(Escape escapeStyle, String param) {
    super(BlockTypes.VALUE, escapeStyle, param);
  }


  @Override
  protected String getText(Locale locale, ZoneId zoneId, Data data) {
    Object value = data.get(param);
    return value != null ? value.toString() : "";
  }

}
