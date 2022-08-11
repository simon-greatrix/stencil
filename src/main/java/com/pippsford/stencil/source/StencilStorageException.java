package com.pippsford.stencil.source;

import com.pippsford.stencil.StencilException;

/**
 * Exception thrown when a stencil cannot be retrieved from its storage.
 *
 * @author Simon Greatrix on 13/02/2021.
 */
public class StencilStorageException extends StencilException {


  public StencilStorageException(String message) {
    super(message);
  }


  public StencilStorageException(String message, Throwable cause) {
    super(message, cause);
  }

}
