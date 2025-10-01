package com.pippsford.stencil.source;

import com.pippsford.stencil.StencilException;

/**
 * Exception thrown when a specified stencil cannot be provided by a Stencil Provider instance.
 *
 * @author Simon Greatrix on 13/02/2021.
 */
public class StencilNotFoundException extends StencilException {

  /**
   * New instance.
   *
   * @param message the error message
   */
  public StencilNotFoundException(String message) {
    super(message);
  }


  /**
   * New instance.
   *
   * @param message the error message
   * @param cause   the cause
   */
  public StencilNotFoundException(String message, Exception cause) {
    super(message, cause);
  }

}
