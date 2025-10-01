package com.pippsford.stencil.value;

import jakarta.json.JsonValue;

/**
 * Class for converting values in {@link Data} instances to JSON.
 *
 * @author Simon Greatrix on 10/11/2021.
 */
public interface JsonConverter {

  /**
   * Convert the specified object to JSON.
   *
   * @param object the object to convert
   *
   * @return the equivalent JSON
   */
  JsonValue toValue(Object object);

}
