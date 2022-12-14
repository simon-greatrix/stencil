package com.pippsford.stencil.blocks.value;

import java.io.IOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.blocks.Block;
import com.pippsford.stencil.blocks.BlockTypes;
import com.pippsford.stencil.escape.Escape;
import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.ValueAccessor;

/**
 * Base value that ensures the escaping and processing rules are applied to the resulting value.
 *
 * @author Simon Greatrix on 01/01/2021.
 */
public abstract class BaseValue implements Block {

  /** The escaping applicable to this value. */
  protected final Escape escapeStyle;

  /**
   * The parameter to output.
   */
  protected final String[] param;

  protected final BlockTypes type;


  protected BaseValue(BlockTypes type, Escape escapeStyle, String param) {
    this.escapeStyle = escapeStyle;
    this.param = ValueAccessor.toKey(param);
    this.type = type;
  }


  protected abstract String getText(Locale locale, ZoneId zoneId, Data data);


  @Override
  public BlockTypes getType() {
    return type;
  }


  @Override
  public void process(Writer out, Locale locale, ZoneId zoneId, Data data) throws IOException {
    String text = getText(locale, zoneId, data);
    out.write(escapeStyle.escape(text));
  }

}
