package com.pippsford.stencil.value;

/** A visitor that visits all elements of a Value Provider. */
public interface ValueVisitor {

  /**
   * Visit an entry.
   *
   * @param name   the entry's key
   * @param value  the entry's value
   * @param isReal true if this is a real entry. If false, it is a pseudo-entry such as a collection's {@code isEmpty} and {@code size} entries
   */
  void visit(String name, Object value, boolean isReal);

}
