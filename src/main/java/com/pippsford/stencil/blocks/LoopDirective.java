package com.pippsford.stencil.blocks;

import java.io.IOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.IndexedValueProvider;
import com.pippsford.stencil.value.ListEntryValueProvider;
import com.pippsford.stencil.value.ValueProvider;

/**
 * Directive for loops or sub-contexts.
 */
public class LoopDirective extends Directive {

  /**
   * Create new directive.
   *
   * @param param the parameter to be tested
   * @param main  the primary template, rendered for each TypeSafeMap of the parameter
   * @param other the alternative template
   */
  public LoopDirective(String param, Template main, Template other) {
    super(param, main, other);
  }


  @Override
  public BlockTypes getType() {
    return BlockTypes.LOOP;
  }


  @Override
  public void process(Writer writer, Locale locale, ZoneId zoneId, Data data) throws IOException, StencilException {
    // If no data, go to the alternative template
    Object value = data.get(param);
    if (value == null) {
      other.process(writer, locale, zoneId, data);
      return;
    }

    // There is some data. The expectation is for an IndexedValueProvider
    ValueProvider parentProvider = data.getProvider();
    IndexedValueProvider indexed = new IndexedValueProvider(parentProvider, value);
    int size = indexed.size();
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        Object datum = indexed.get(i);
        main.process(writer, locale, zoneId, new Data(new ListEntryValueProvider(parentProvider, i, size, datum)));
      }
    } else {
      // No actual data
      other.process(writer, locale, zoneId, data);
    }
  }

}
