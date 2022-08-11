package com.pippsford.stencil;

/**
 * Root class for stencil related exceptions.
 *
 * @author Simon Greatrix on 13/02/2021.
 */
public class StencilException extends Exception {

  protected StencilException(String message) {
    super(message);
  }


  protected StencilException(String message, Throwable cause) {
    super(message, cause);
  }

}
