package com.pippsford.stencil.escape;

/**
 * ECMA Script (ES6+) style escaping. This includes escaping the back-quote and representing non-BMP Unicode as \ u{12345}.
 *
 * @author Simon Greatrix on 11/01/2021.
 */
public class EcmaScriptLiteral extends StringLiteral {

  /** ECMA style string escaping to Unicode. */
  public static final EcmaScriptLiteral ECMA = new EcmaScriptLiteral(false);

  /** ECMA style string escaping to ASCII. */
  public static final EcmaScriptLiteral ECMA_ASCII = new EcmaScriptLiteral(true);


  /**
   * New instance.
   *
   * @param toAscii if true, force only ASCII output
   */
  public EcmaScriptLiteral(boolean toAscii) {
    super(
        new int[]{'\b', '\t', '\n', '\013', '\f', '\r', '\"', '\'', '\\', '`'},
        new String[]{"\\b", "\\t", "\\n", "\\v", "\\f", "\\r", "\\\"", "\\'", "\\\\", "\\`"},
        toAscii
    );
  }


  @Override
  protected void handleNonBMP(StringBuilder buffer, int cp) {
    buffer.append(String.format("\\u{%x}", cp));
  }

}
