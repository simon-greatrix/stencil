package com.pippsford.stencil.value;

import java.util.function.IntFunction;

/**
 * An indexable object of size <code>N</code> maps the numbers zero through to <code>N-1</code> to objects.
 *
 * @author Simon Greatrix on 21/02/2021.
 */
public interface Indexable<T> extends IntFunction<T> {

  /**
   * The number of entries in this. Must be non-negative.
   *
   * @return the number of entries.
   */
  int size();

}
