package com.pippsford.stencil.source;

import com.pippsford.stencil.StencilException;

/**
 * Exception thrown when a specified stencil cannot be provided by a Stencil Provider instance.
 *
 * @author Simon Greatrix on 13/02/2021.
 */
public class StencilNotFoundException extends StencilException {

  public StencilNotFoundException(String message) {
    super(message);
  }


  public StencilNotFoundException(String message, Exception e) {
    super(message, e);
  }

}
