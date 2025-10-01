package com.pippsford.stencil.parser;

import java.util.Locale;
import java.util.Objects;

import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.escape.Escape;
import com.pippsford.stencil.source.StencilId;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Context for identifying supplementary resources.
 *
 * @author Simon Greatrix on 22/12/2020.
 */
public class Context {

  /** The current I18N bundle. */
  private final String bundle;

  /** Current processing flags. */
  private final Escape escapeStyle;

  /** The ID of the stencil being processed. */
  private final StencilId stencilId;

  /** Containing stencil map. */
  private final Stencils stencils;


  /**
   * Create the parsing context.
   *
   * @param stencils  the stencil set which will contain this
   * @param stencilId the id of the stencil
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Context(Stencils stencils, StencilId stencilId) {
    Objects.requireNonNull(stencils);
    this.stencils = stencils;
    this.stencilId = stencilId;
    bundle = stencilId.getBundle();
    escapeStyle = stencilId.getEscape();
  }


  private Context(Context context, String bundle, Escape newStyle) {
    stencils = context.stencils;
    stencilId = context.stencilId;
    this.bundle = bundle;
    escapeStyle = newStyle;
  }


  /**
   * Get the current resource bundle.
   *
   * @return the resource bundle
   */
  public String getBundle() {
    return bundle;
  }


  /**
   * Get the current escape style.
   *
   * @return the escape implementation
   */
  public Escape getEscapeStyle() {
    return escapeStyle;
  }


  /**
   * Get the escape style associated with the style name.
   *
   * @param name the name of the escape style
   *
   * @return the escape style implementation
   *
   * @throws StencilParseFailedException if the style name is not recognised
   */
  public Escape getEscapeStyle(String name) throws StencilParseFailedException {
    return stencils.getEscapeResolver().forName(name, escapeStyle);
  }


  /**
   * Get the locale.
   *
   * @return the locale
   */
  public Locale getLocale() {
    return stencilId.getLocale();
  }


  /**
   * Get the current stencil ID.
   *
   * @return the ID
   */
  public StencilId getStencilId() {
    return stencilId;
  }


  /**
   * Get the {@link Stencils} instance.
   *
   * @return the Stencils instance
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Stencils getStencils() {
    return stencils;
  }


  /**
   * Create a new context instance with the specified resource bundle.
   *
   * @param newBundle the new resource bundle
   *
   * @return the new instance
   */
  public Context withBundle(String newBundle) {
    return new Context(this, newBundle, escapeStyle);
  }


  /**
   * Create a new context instance with the specified escape style.
   *
   * @param newStyle the new escape style
   *
   * @return the new instance
   */
  public Context withEscapeStyle(Escape newStyle) {
    return new Context(this, bundle, newStyle);
  }

}
