package com.pippsford.stencil.blocks;

/** The parsers processing mode. */
public enum ProcessingMode {
  /** The normal processing mode. Values and directives are inactive within "here documents". */
  NORMAL,

  /** The inverted processing mode. Values and directives are active only within "here documents". */
  INVERTED;
}
