package com.pippsford.stencil.escape;

/**
 * Escaping as Java literal.
 *
 * @author Simon Greatrix on 11/01/2021.
 */
public class JavaScriptLiteral extends StringLiteral {

  /** Escape to a Unicode JavaScript literal. */
  public static final JavaScriptLiteral JAVA = new JavaScriptLiteral(false);

  /** Escape to a strict ASCII JavaScript literal. */
  public static final JavaScriptLiteral JAVA_ASCII = new JavaScriptLiteral(true);


  /**
   * New instance.
   *
   * @param toAscii if true, force ASCII output
   */
  public JavaScriptLiteral(boolean toAscii) {
    super(
        new int[]{'\b', '\t', '\n', '\f', '\r', '\"', '\'', '\\'},
        new String[]{"\\b", "\\t", "\\n", "\\f", "\\r", "\\\"", "\\'", "\\\\"},
        toAscii
    );
  }

}
