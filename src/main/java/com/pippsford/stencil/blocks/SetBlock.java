package com.pippsford.stencil.blocks;

import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.value.Data;

/**
 * The set block doesn't do anything, but does affect whitespace processing.
 *
 * @author Simon Greatrix on 21/01/2021.
 */
public class SetBlock implements Block {

  public static final SetBlock INSTANCE = new SetBlock();


  private SetBlock() {

  }


  @Override
  public BlockTypes getType() {
    return BlockTypes.SET;
  }


  @Override
  public void process(Writer out, Locale locale, ZoneId zoneId, Data data) {
    // do nothing
  }

}
