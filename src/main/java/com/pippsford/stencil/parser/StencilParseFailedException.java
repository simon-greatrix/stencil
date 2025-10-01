package com.pippsford.stencil.parser;

import com.pippsford.stencil.StencilException;

/**
 * Exception thrown when the parse of a stencil fails.
 *
 * @author Simon Greatrix on 22/12/2020.
 */
public class StencilParseFailedException extends StencilException {

  /**
   * New instance.
   *
   * @param message the error message
   */
  public StencilParseFailedException(String message) {
    super(message);
  }


  /**
   * New instance.
   *
   * @param message the error message
   * @param cause   the cause
   */
  public StencilParseFailedException(String message, Exception cause) {
    super(message, cause);
  }

}
