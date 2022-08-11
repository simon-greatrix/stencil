package com.pippsford.stencil.apply;

import com.pippsford.stencil.value.Data;

/**
 * Apply some process to the currently provided values.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
@FunctionalInterface
public interface ValueProcessor {

  Object apply(Data valueProvider, Parameter[] arguments);

}
