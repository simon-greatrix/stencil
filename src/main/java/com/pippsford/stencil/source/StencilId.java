package com.pippsford.stencil.source;

import java.util.Locale;
import java.util.Objects;
import jakarta.annotation.Nonnull;

import com.pippsford.stencil.escape.Escape;

/**
 * An ID for a processed stencil.
 *
 * @author Simon Greatrix on 10/01/2021.
 */
public abstract class StencilId {

  private final String bundle;

  private final Escape escape;

  private final Locale locale;


  /**
   * New instance.
   *
   * @param locale the locale
   * @param bundle the name of the resource bundle
   * @param escape the escape style
   */
  protected StencilId(Locale locale, String bundle, Escape escape) {
    Objects.requireNonNull(locale);
    Objects.requireNonNull(escape);
    this.locale = locale;
    this.bundle = bundle;
    this.escape = escape;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StencilId that)) {
      return false;
    }

    return Objects.equals(getBundle(), that.getBundle())
        && getEscape().equals(that.getEscape())
        && getLocale().equals(that.getLocale());
  }


  /**
   * The default bundle for messages.
   *
   * @return the bundle name
   */
  public String getBundle() {
    return bundle;
  }


  /**
   * The current escape style.
   *
   * @return the escape style.
   */
  public Escape getEscape() {
    return escape;
  }


  /**
   * The parsing locale.
   *
   * @return the locale
   */
  public Locale getLocale() {
    return locale;
  }


  /**
   * An ID for log messages.
   *
   * @return a loggable ID
   */
  public abstract String getLogId();


  /**
   * Get the source of the template.
   *
   * @return the source text of the template.
   *
   * @throws StencilStorageException  if the source text cannot be retrieved.
   * @throws StencilNotFoundException if the source does not exist
   */
  @Nonnull
  public abstract String getText() throws StencilStorageException, StencilNotFoundException;


  @Override
  public int hashCode() {
    int result = getBundle() != null ? getBundle().hashCode() : 0;
    result = 31 * result + getEscape().hashCode();
    result = 31 * result + getLocale().hashCode();
    return result;
  }


  /**
   * A source file stencil, or a resource bundle message stencil?.
   *
   * @return true if a resource bundle message stencil
   */
  public abstract boolean isMessage();

}
