package com.pippsford.stencil.blocks;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Possible block types within page.
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


  private static String expandPattern(Properties properties, String name) {
    String value = properties.getProperty(name);
    if (value == null) {
      throw new InternalError("No definition found for \"" + name + "\".");
    }
    Pattern p = Pattern.compile("![A-Z_]+!");
    while (true) {
      Matcher m = p.matcher(value);
      if (!m.find()) {
        return value;
      }
      String r = expandPattern(properties, m.group());
      value = value.replace(m.group(), r);
    }
  }


  /**
   * Get a named regular expression used in parsing the block specification.
   *
   * @param name the name to look up
   *
   * @return the pattern
   */
  public static Pattern loadPattern(String name) {
    String patternText = loadPatternText(name);
    try {
      return Pattern.compile(
          patternText,
          Pattern.UNICODE_CHARACTER_CLASS + Pattern.COMMENTS + Pattern.CASE_INSENSITIVE + Pattern.DOTALL
      );
    } catch (PatternSyntaxException badPattern) {
      throw new InternalError("Bad specification for " + name + "\n" + patternText, badPattern);
    }
  }


  /**
   * Get a named regular expression used in parsing the block specification.
   *
   * @param name the name to look up
   *
   * @return the pattern
   */
  @SuppressFBWarnings(
      value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
      justification = "False positive. Redundant check is in the try-with-resources generated code")
  public static String loadPatternText(String name) {
    Properties properties = new Properties();
    try (InputStream in = BlockTypes.class.getResourceAsStream("patterns.xml")) {
      properties.loadFromXML(in);
    } catch (IOException ioException) {
      throw new InternalError("Required internal resource was unavailable", ioException);
    }

    return expandPattern(properties, name);
  }


  private final boolean isValue;

  /**
   * The pattern matched for this block.
   */
  private final Pattern pattern;


  /**
   * Create new block type with specified pattern.
   */
  BlockTypes() {
    pattern = loadPattern(name());
    isValue = name().startsWith("VALUE_") || name().equals("VALUE");
  }


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
