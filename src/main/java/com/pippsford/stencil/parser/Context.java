package com.pippsford.stencil.parser;

import java.util.Locale;
import java.util.Objects;

import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.blocks.ProcessingMode;
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

  /** Number of times the bundle changes. */
  private final int bundleChanges;

  /** Number of times the escape style changes. */
  private final int escapeChanges;

  /** Current processing flags. */
  private final Escape escapeStyle;

  /** Is the processing mode normal or inverted?. */
  private final ProcessingMode mode;

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
    bundleChanges = 0;
    escapeStyle = stencilId.getEscape();
    escapeChanges = 0;
    mode = ProcessingMode.NORMAL;
  }


  private Context(Context original, String newBundle, Escape newStyle, ProcessingMode newMode) {
    stencils = original.stencils;
    stencilId = original.stencilId;
    bundle = newBundle;
    bundleChanges = original.bundleChanges + (Objects.equals(original.bundle, newBundle) ? 0 : 1);
    escapeStyle = newStyle;
    escapeChanges = original.escapeChanges + (Objects.equals(original.escapeStyle, newStyle) ? 0 : 1);
    mode = newMode;
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
   * Get the current processing mode.
   *
   * @return the processing mode
   */
  public ProcessingMode getMode() {
    return mode;
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
    return new Context(this, newBundle, escapeStyle, mode);
  }


  public Context withChanges(Context before, Context after) {
    return new Context(
        this,
        (before.bundleChanges == after.bundleChanges) ? this.bundle : after.bundle,
        (before.escapeChanges == after.escapeChanges) ? this.escapeStyle : after.escapeStyle,
        mode
    );
  }


  /**
   * Create a new context instance with the specified escape style.
   *
   * @param newStyle the new escape style
   *
   * @return the new instance
   */
  public Context withEscapeStyle(Escape newStyle) {
    return new Context(this, bundle, newStyle, mode);
  }


  /**
   * Create a new context instance with the specified processing mode.
   *
   * @param newMode the new processing mode
   *
   * @return the new instance
   */
  public Context withMode(ProcessingMode newMode) {
    return new Context(this, bundle, escapeStyle, newMode);
  }

}
