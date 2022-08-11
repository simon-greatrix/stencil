package com.pippsford.stencil.blocks;

import java.io.IOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.parser.Context;
import com.pippsford.stencil.source.MessageStencilId;
import com.pippsford.stencil.source.StencilId;
import com.pippsford.stencil.value.Data;

/**
 * Include a stencil from a ResourceBundle.
 *
 * @author Simon Greatrix on 22/12/2020.
 */
public class Resource implements Block {

  /**
   * The context for includes.
   */
  private final Context context;

  /**
   * Property within the bundle.
   */
  private final StencilId stencilId;


  /**
   * Create a new block.
   *
   * @param context  the current parse context for parsing referenced stencils
   * @param bundle   the bundle to retrieve resources from
   * @param property the property in the bundle
   */
  public Resource(Context context, String bundle, String property) {
    this.context = context.withBundle(bundle);
    stencilId = new MessageStencilId(bundle, property, context.getLocale(), context.getEscapeStyle());
  }


  @Override
  public BlockTypes getType() {
    return BlockTypes.RESOURCE_2;
  }


  @Override
  public void process(Writer out, Locale locale, ZoneId zoneId, Data data) throws StencilException, IOException {
    Template temp = context.getStencils().getTemplate(stencilId);
    temp.process(out, locale, zoneId, data);
  }

}
