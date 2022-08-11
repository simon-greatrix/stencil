package com.pippsford.stencil.blocks;

import java.io.IOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.value.Data;

/**
 * A single block of output.
 */
public interface Block {

  /**
   * Get the type of this block.
   *
   * @return the type of this block
   */
  BlockTypes getType();

  /**
   * Process this block, producing the output.
   *
   * @param out    the output stream
   * @param locale the output locale
   * @param data   this page's data
   */
  void process(Writer out, Locale locale, ZoneId zoneId, Data data) throws StencilException, IOException;

}
