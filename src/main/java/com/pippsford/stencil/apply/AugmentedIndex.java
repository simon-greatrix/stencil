package com.pippsford.stencil.apply;

import java.math.BigDecimal;

import com.pippsford.common.TypeSafeMap;
import com.pippsford.stencil.value.Data;

/**
 * This value processor provides additional fields to assist in processing of collections with the <code>[with]</code> directive.
 *
 * @author Simon Greatrix on 08/01/2021.
 */
public class AugmentedIndex implements ValueProcessor {

  /** Instance of the processor. */
  public static final AugmentedIndex INSTANCE = new AugmentedIndex();


  private AugmentedIndex() {
    // do nothing
  }


  /**
   * Adds index related fields to the current values. The current values are expected to include "index" and "size". The fields added are:
   *
   * <dl>
   *   <dt>isFirst</dt>
   *   <dd>Boolean. True if the index is zero.</dd>
   *
   *   <dt>isLast</dt>
   *   <dd>Boolean. True if the index is equal to one less than the size.</dd>
   *
   *   <dt>isOdd</dt>
   *   <dd>Boolean. True if the index is odd.</dd>
   *
   *   <dt>isEven</dt>
   *   <dd>Boolean. True if the index is even.</dd>
   *
   *   <dt>index1</dt>
   *   <dd>Integer. A 1-based index as an alternative to the standard 0-based index.</dd>
   * </dl>
   *
   * <p>The following additional fields are added if a page size is passed in.</p>
   *
   * <dl>
   *   <dt>isFirstOnPage</dt>
   *   <dd>Boolean. True if this is the first entry on a page.</dd>
   *
   *   <dt>isLastOnPage</dt>
   *   <dd>Boolean. True if this is the last entry on a page, or if this is the last entry overall.</dd>
   *
   *   <dt>pageRow</dt>
   *   <dd>Integer. The row on the current page (one based)</dd>
   *
   *   <dt>pageNumber</dt>
   *   <dd>Integer. The current page number (one based)</dd>
   *
   *   <dt>pageCount</dt>
   *   <dd>Integer. The number of pages.</dd>
   * </dl>
   *
   * <p>Note: a prefix may be specified to prevent the added fields masking another field.</p>
   *
   * <p>Example:</p>
   * <dl>
   *   <dt><code>[apply AugmentedIndex($,20)]</code></dt>
   *   <dd>Prefix all the fields with '$' and use a page size of 20.</dd>
   *
   *   <dt><code>[apply AugmentedIndex(,pageSize)]</code></dt>
   *   <dd>Use the standard field names and get the page size from the current values.</dd>
   *
   *   <dt><code>[apply AugmentedIndex]</code></dt>
   *   <dd>Use the standard field named and do not enable paging.</dd>
   * </dl>
   *
   * @param valueProvider the value provider.
   * @param arguments     two arguments: prefix (which may be an empty string), and a page size.
   */
  @Override
  public Object apply(Data valueProvider, Parameter[] arguments) {
    Integer index = TypeSafeMap.asInt(valueProvider.get("index").value());
    Integer size = TypeSafeMap.asInt(valueProvider.get("size").value());

    if (index == null) {
      throw new IllegalArgumentException("No 'index' property in provided data");
    }
    if (size == null) {
      throw new IllegalArgumentException("No 'size' property in provided data");
    }

    String prefix = arguments.length > 0 ? arguments[0].asString() : "";
    Integer pageSize = null;
    if (arguments.length > 1) {
      Parameter paramSize = arguments[1];
      if (paramSize.isNumber()) {
        BigDecimal bigSize = paramSize.asNumber();
        try {
          pageSize = bigSize.intValueExact();
        } catch (ArithmeticException e) {
          throw new IllegalArgumentException("Page size specified as '" + bigSize + "' but could not translate to a sensible size.");
        }
      } else {
        throw new IllegalArgumentException("Page size specified as '" + arguments[1] + "' but could not translate to actual size.");
      }
    }

    valueProvider.put(prefix + "isFirst", index == 0);
    valueProvider.put(prefix + "isLast", index == size - 1);
    valueProvider.put(prefix + "index1", index + 1);
    valueProvider.put(prefix + "isEven", (index & 1) == 0);
    valueProvider.put(prefix + "isOdd", (index & 1) == 1);

    if (pageSize != null) {
      valueProvider.put(prefix + "isFirstOnPage", (index % pageSize) == 0);
      valueProvider.put(prefix + "isLastOnPage", (index % pageSize) == (pageSize - 1) || (index == size - 1));
      valueProvider.put(prefix + "pageRow", 1 + (index % pageSize));
      valueProvider.put(prefix + "pageNumber", 1 + (index / pageSize));
      valueProvider.put(prefix + "pageCount", (size / pageSize) + (((size % pageSize) != 0) ? 1 : 0));
    }

    return null;
  }

}
