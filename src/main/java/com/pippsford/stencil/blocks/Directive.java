package com.pippsford.stencil.blocks;

/**
 * A directive for control of processing flow within a page.
 */
abstract class Directive implements Block {

  /**
   * The primary template to be rendered.
   */
  final Template main;

  /**
   * The alternative template.
   */
  final Template other;

  /**
   * A parameter that determines what will be rendered.
   */
  final String[] param;


  /**
   * Create new directive.
   *
   * @param param the parameter to be used
   * @param main  the primary template
   * @param other the alternative template
   */
  Directive(String param, Template main, Template other) {
    this.param = param.split("\\.");
    this.main = main;
    this.other = other;
  }

}
