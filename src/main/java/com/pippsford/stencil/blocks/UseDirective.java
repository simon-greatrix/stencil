package com.pippsford.stencil.blocks;

import java.io.IOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.ValueAccessor;

/**
 * A directive that starts a block using a specific value as the default value context.
 *
 * @author Simon Greatrix on 06/01/2021.
 */
public class UseDirective extends Directive {

  /**
   * Create new directive.
   *
   * @param param the parameter to be tested
   * @param main  the primary template, rendered for each TypeSafeMap of the parameter
   * @param other the alternative template
   */
  public UseDirective(String param, Template main, Template other) {
    super(param, main, other);
  }


  @Override
  public BlockTypes getType() {
    return BlockTypes.USE;
  }


  @Override
  public void process(Writer writer, Locale locale, ZoneId zoneId, Data data) throws IOException, StencilException {
    // If no data, go to the alternative template
    Object value = data.get(param);
    if (value == null) {
      other.process(writer, locale, zoneId, data);
      return;
    }

    Data nextData = new Data(ValueAccessor.makeProvider(data.getProvider(), value));
    main.process(writer, locale, zoneId, nextData);
  }

}
