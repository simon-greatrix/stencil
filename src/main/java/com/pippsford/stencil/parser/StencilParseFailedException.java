package com.pippsford.stencil.parser;

import com.pippsford.stencil.StencilException;

/**
 * Exception thrown when the parse of a stencil fails.
 *
 * @author Simon Greatrix on 22/12/2020.
 */
public class StencilParseFailedException extends StencilException {

  public StencilParseFailedException(String message) {
    super(message);
  }


  public StencilParseFailedException(String s, Exception e) {
    super(s, e);
  }

}
