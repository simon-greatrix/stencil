package com.pippsford.stencil.source;

import java.util.Locale;
import java.util.function.BiFunction;
import javax.annotation.Nullable;

import com.pippsford.stencil.Source;
import com.pippsford.stencil.SourceProvider;

/**
 * A source provider that adds header and footer text to existing source.
 *
 * @author Simon Greatrix on 13/02/2021.
 */
public class FramingSourceProvider implements SourceProvider {

  private static final BiFunction<Source, Locale, String> BLANK_PROVIDER = new ConstantFunction("");



  static class ConstantFunction implements BiFunction<Source, Locale, String> {

    private final String value;


    ConstantFunction(String value) {
      this.value = value != null ? value : "";
    }


    @Override
    public String apply(Source s, Locale l) {
      return value;
    }

  }



  /** The actual source provider. */
  private final SourceProvider sourceProvider;

  private BiFunction<Source, Locale, String> footerProvider = BLANK_PROVIDER;

  private BiFunction<Source, Locale, String> headerProvider = BLANK_PROVIDER;


  public FramingSourceProvider(SourceProvider sourceProvider) {
    this.sourceProvider = sourceProvider;
  }


  public BiFunction<Source, Locale, String> getFooterProvider() {
    return footerProvider;
  }


  public BiFunction<Source, Locale, String> getHeaderProvider() {
    return headerProvider;
  }


  @Nullable
  @Override
  public String getSource(Source path, Locale locale) throws StencilStorageException {
    String content = getSourceProvider().getSource(path, locale);
    if (content == null) {
      return null;
    }
    return getHeaderProvider().apply(path, locale)
        + content
        + getFooterProvider().apply(path, locale);
  }


  public SourceProvider getSourceProvider() {
    return sourceProvider;
  }


  @Override
  public String getSourceRoot() {
    return sourceProvider.getSourceRoot();
  }


  public void setFooter(String footer) {
    footerProvider = new ConstantFunction(footer);
  }


  /**
   * Set the footer provider.
   *
   * @param footerProvider the provider. If null, provides an empty string
   */
  public void setFooterProvider(BiFunction<Source, Locale, String> footerProvider) {
    if (footerProvider == null) {
      footerProvider = BLANK_PROVIDER;
    }
    this.footerProvider = footerProvider;
  }


  public void setHeader(String header) {
    headerProvider = new ConstantFunction(header);
  }


  /**
   * Set the header provider.
   *
   * @param headerProvider the provider. If null, provides an empty string.
   */
  public void setHeaderProvider(BiFunction<Source, Locale, String> headerProvider) {
    if (headerProvider == null) {
      headerProvider = BLANK_PROVIDER;
    }
    this.headerProvider = headerProvider;
  }

}
