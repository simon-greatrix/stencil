package com.pippsford.stencil.blocks;

import java.io.IOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.value.Data;

/**
 * Include a specified stencil in this stencil.
 *
 * @author Simon Greatrix on 11/01/2021.
 */
public class Include implements Block {

  /** Functional interface for a template provider that can throw a StencilException if there is a problem. */
  public interface TemplateProvider {

    /**
     * Get a template.
     *
     * @return the template
     *
     * @throws StencilException if the template cannot be acquired
     */
    Template get() throws StencilException;

  }



  /** Loader for template. */
  private final TemplateProvider loader;

  /** The lazily-loaded included template. */
  private Template template;


  /**
   * New instance.
   *
   * @param loader provider of templates
   */
  public Include(TemplateProvider loader) {
    this.loader = loader;
  }


  @Override
  public BlockTypes getType() {
    return BlockTypes.INCLUDE;
  }


  private synchronized Template loadTemplate() throws StencilException {
    if (template == null) {
      template = loader.get();
    }
    return template;
  }


  @Override
  public void process(Writer out, Locale locale, ZoneId zoneId, Data data) throws IOException, StencilException {
    loadTemplate().process(out, locale, zoneId, data);
  }

}
