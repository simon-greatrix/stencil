package com.pippsford.stencil.source;

/**
 * Exception thrown when the terminating marker for a "here document" was not found anywhere in the compendium.
 *
 * @author Simon Greatrix on 13/02/2021.
 */
public class CompendiumMarkerMissingException extends StencilStorageException {

  public CompendiumMarkerMissingException(String message) {
    super(message);
  }

}
