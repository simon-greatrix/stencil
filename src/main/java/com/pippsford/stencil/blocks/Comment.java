package com.pippsford.stencil.blocks;

import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.value.Data;

/**
 * A comment in a stencil. A comment may be a directive comment "[* ... *]" or a value comment "{* ... *}". Directive comments affect the interpretation of
 * ignorable white space in the same way as all other directives.
 *
 * @author Simon Greatrix on 21/01/2021.
 */
public class Comment implements Block {

  private final boolean isValue;


  /**
   * New instance.
   *
   * @param isValue is this a "value comment" or a "directive comment"?
   */
  public Comment(boolean isValue) {
    this.isValue = isValue;
  }


  @Override
  public BlockTypes getType() {
    return isValue ? BlockTypes.VALUE_COMMENT : BlockTypes.COMMENT;
  }


  @Override
  public void process(Writer out, Locale locale, ZoneId zoneId, Data data) {
    // do nothing
  }

}
