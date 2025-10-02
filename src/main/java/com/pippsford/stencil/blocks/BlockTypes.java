package com.pippsford.stencil.blocks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Possible block types within a page.
 */
public enum BlockTypes {

  /** A procedure call. */
  APPLY,

  /**
   * A comment, which is just ignored.
   */
  COMMENT,

  /**
   * An [else] directive.
   */
  ELSE,

  /**
   * An [end] directive.
   */
  END,

  /**
   * An [if value] directive.
   */
  IF,

  /**
   * An [include template] directive.
   */
  INCLUDE,

  /**
   * A [loop value] directive.
   */
  LOOP,

  /**
   * An [{value}] template from a resource bundle.
   */
  RESOURCE_1,

  /**
   * An [{resource,value}] template from a resource bundle.
   */
  RESOURCE_2,

  /**
   * Set processing options.
   */
  SET,

  /**
   * A [use value] directive.
   */
  USE,

  /**
   * A value display.
   */
  VALUE,

  /**
   * A comment that counts as a value for processing whitespace.
   */
  VALUE_COMMENT,

  /**
   * A standard date value display.
   */
  VALUE_DATE,

  /** A here-document. */
  VALUE_HERE,

  /**
   * A standard time value display.
   */
  VALUE_TIME,

  /**
   * A standard date and time value display.
   */
  VALUE_DATE_TIME,

  /**
   * A standard date and time value display.
   */
  VALUE_DATE_TIME_2,

  /**
   * A date and time value display according to a pattern.
   */
  VALUE_DATE_TIME_HERE,

  /**
   * A value display using printf formatting.
   */
  VALUE_FORMAT,

  /**
   * A value display using printf formatting.
   */
  VALUE_FORMAT_HERE,

  /**
   * A value display.
   */
  VALUE_NUMBER,

  /**
   * A value display.
   */
  VALUE_NUMBER_HERE;


  private final boolean isValue;

  /**
   * The pattern matched for this block.
   */
  private final Pattern pattern;


  /**
   * Create new block type with specified pattern.
   */
  BlockTypes() {
    pattern = Patterns.get(name());
    isValue = name().startsWith("VALUE_") || name().equals("VALUE");
  }


  /**
   * Is this block a value or a directive?.
   *
   * @return true if a value
   */
  public boolean isValue() {
    return isValue;
  }


  /**
   * Get a matcher for this block type.
   *
   * @param input the input to match
   *
   * @return a Matcher
   */
  public Matcher matcher(String input) {
    return pattern.matcher(input);
  }
}
