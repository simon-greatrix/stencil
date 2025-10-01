package com.pippsford.stencil;

/**
 * Root class for stencil related exceptions.
 *
 * @author Simon Greatrix on 13/02/2021.
 */
public class StencilException extends Exception {

  /**
   * New instance.
   *
   * @param message the error message
   */
  protected StencilException(String message) {
    super(message);
  }


  /**
   * New instance.
   *
   * @param message the error message
   * @param cause   the cause
   */
  protected StencilException(String message, Throwable cause) {
    super(message, cause);
  }

}
