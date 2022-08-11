package com.pippsford.stencil.apply;

import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.value.Data;

/**
 * Apply some process to the currently provided values.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
@FunctionalInterface
public interface LocalizedValueProcessor {

  Object apply(Data data, Locale locale, ZoneId zoneId, Parameter[] arguments);

}
