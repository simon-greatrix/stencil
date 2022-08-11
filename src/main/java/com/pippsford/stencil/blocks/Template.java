package com.pippsford.stencil.blocks;

import java.io.IOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.value.Data;


/**
 * A set of blocks that comprise one logical unit of output.
 */
public class Template implements Block {

  /**
   * Blocks that make up template.
   */
  private final Block[] blocks;


  /**
   * New template consisting of specified blocks.
   *
   * @param blocks the blocks
   */
  public Template(Block[] blocks) {
    this.blocks = blocks.clone();
  }


  @Override
  public BlockTypes getType() {
    // A fully expanded include, not an include directive
    return BlockTypes.INCLUDE;
  }


  @Override
  public void process(Writer writer, Locale locale, ZoneId zoneId, Data data) throws IOException, StencilException {
    for (Block b : blocks) {
      b.process(writer, locale, zoneId, data);
    }
  }

}
