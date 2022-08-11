package com.pippsford.stencil.escape;

/**
 * Escaping as JSON literal.
 *
 * @author Simon Greatrix on 11/01/2021.
 */
public class JsonScriptLiteral extends StringLiteral {

  public static final JsonScriptLiteral JSON = new JsonScriptLiteral(false);

  public static final JsonScriptLiteral JSON_ASCII = new JsonScriptLiteral(true);


  /**
   * New instance.
   *
   * @param toAscii if true, force ASCII output
   */
  public JsonScriptLiteral(boolean toAscii) {
    super(
        new int[]{'\b', '\t', '\n', '\f', '\r', '\"', '/', '\\'},
        new String[]{"\\b", "\\t", "\\n", "\\f", "\\r", "\\\"", "\\/", "\\\\"},
        toAscii
    );
  }

}
