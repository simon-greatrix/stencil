package com.pippsford.stencil.source;

import java.util.Locale;
import javax.annotation.Nonnull;

import com.pippsford.stencil.Source;
import com.pippsford.stencil.SourceProvider;
import com.pippsford.stencil.escape.Escape;

/**
 * The ID for a Stencil including the source provider that provides it.
 *
 * @author Simon Greatrix on 10/01/2021.
 */
public class SourceStencilId extends StencilId {

  private final Source source;

  private final SourceProvider sourceProvider;

  private final String sourceRoot;

  private final Class<?> sourceType;


  /**
   * New instance.
   *
   * @param provider    the source provider
   * @param stencilName the stencil's name
   * @param locale      the processing locale
   * @param bundle      the message bundle
   * @param escape      the escape mechanism
   *
   * @throws StencilNotFoundException if the stencil cannot be located
   */
  public SourceStencilId(SourceProvider provider, String stencilName, Locale locale, String bundle, Escape escape) throws StencilNotFoundException {
    super(locale, bundle, escape);
    source = Source.of(stencilName);
    sourceProvider = provider;
    sourceType = provider.getClass();
    sourceRoot = provider.getSourceRoot();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SourceStencilId)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    SourceStencilId that = (SourceStencilId) o;

    if (!getSource().equals(that.getSource())) {
      return false;
    }
    if (!sourceProvider.equals(that.sourceProvider)) {
      return false;
    }
    if (getSourceRoot() != null ? !getSourceRoot().equals(that.getSourceRoot()) : that.getSourceRoot() != null) {
      return false;
    }
    return getSourceType().equals(that.getSourceType());
  }


  @Override
  public String getLogId() {
    return source.toString();
  }


  /**
   * The source.
   *
   * @return the source
   */
  public Source getSource() {
    return source;
  }


  /**
   * The root location for the source.
   *
   * @return the root location
   */
  public String getSourceRoot() {
    return sourceRoot;
  }


  /**
   * The type of the source provider.
   *
   * @return the type.
   */
  public Class<?> getSourceType() {
    return sourceType;
  }


  @Override
  @Nonnull
  public String getText() throws StencilStorageException, StencilNotFoundException {
    String text = sourceProvider.getSource(source, getLocale());
    if (text == null) {
      throw new StencilNotFoundException(getSource().toString());
    }
    return text;
  }


  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + getSource().hashCode();
    result = 31 * result + sourceProvider.hashCode();
    result = 31 * result + (getSourceRoot() != null ? getSourceRoot().hashCode() : 0);
    result = 31 * result + getSourceType().hashCode();
    return result;
  }


  @Override
  public boolean isMessage() {
    return false;
  }


  @Override
  public String toString() {
    return String.format(
        "SourceStencilId(sourceType='%s', sourceRoot='%s', name='%s')",
        sourceType, sourceRoot, getSource()
    );
  }

}
