package com.pippsford.stencil.blocks;

import java.io.IOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.value.Data;


/**
 * A simple block of static text.
 */
public class Static implements Block {

  private final boolean isRequired;

  /**
   * Text to output.
   */
  private final String text;


  /**
   * New static block of text.
   *
   * @param text       the text
   * @param isRequired if true, the text must be specified
   */
  public Static(String text, boolean isRequired) {
    this.text = text;
    this.isRequired = isRequired;
  }


  public String getText() {
    return text;
  }


  @Override
  public BlockTypes getType() {
    return isRequired ? BlockTypes.VALUE_HERE : null;
  }


  public boolean isRequired() {
    return isRequired;
  }


  @Override
  public void process(Writer out, Locale locale, ZoneId zoneId, Data data) throws IOException {
    out.write(text);
  }

}
