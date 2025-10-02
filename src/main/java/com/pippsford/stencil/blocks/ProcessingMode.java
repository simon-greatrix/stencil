package com.pippsford.stencil.blocks;

public enum ProcessingMode {
  /** The normal processing mode. Values and directives are inactive within "here documents". */
  NORMAL,

  /** Inverted mode: values and directives are active only within "here documents" */
  INVERTED;
}
