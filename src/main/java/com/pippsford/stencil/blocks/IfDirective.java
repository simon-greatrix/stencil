package com.pippsford.stencil.blocks;

import java.io.IOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.common.TypeSafeMap;
import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.value.Data;


/**
 * A conditional directive to determine which of two templates to render.
 */
public class IfDirective extends Directive {

  /**
   * Create new directive.
   *
   * @param param the parameter to be tested
   * @param main  the primary template, rendered if parameter is not null and not false
   * @param other the alternative template
   */
  public IfDirective(String param, Template main, Template other) {
    super(param, main, other);
  }


  @Override
  public BlockTypes getType() {
    return BlockTypes.IF;
  }


  @Override
  public void process(Writer writer, Locale locale, ZoneId zoneId, Data data) throws IOException, StencilException {
    Object value = data.get(param).value();
    boolean bool = TypeSafeMap.asBoolean(value);
    Template temp = bool ? main : other;
    temp.process(writer, locale, zoneId, data);
  }

}
