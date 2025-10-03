package com.pippsford.stencil.source;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import jakarta.annotation.Nullable;

import com.pippsford.stencil.Source;
import com.pippsford.stencil.SourceProvider;

/**
 * A source of stencils from message resource bundles.
 *
 * @author Simon Greatrix on 10/01/2021.
 */
public class BundleSourceProvider implements SourceProvider {

  /** The name of the resource bundle. */
  protected final String bundleName;


  /**
   * New instance.
   *
   * @param bundleName the name of the resource bundle
   */
  public BundleSourceProvider(String bundleName) {
    this.bundleName = bundleName;
  }


  /**
   * Get the appropriate bundle for the locale. This uses the default lookup method. Override this method to perform the lookup using a specific Module,
   * Control, or ClassLoader.
   *
   * @param locale the locale
   *
   * @return the resource bundle
   */
  protected ResourceBundle getBundle(Locale locale) {
    return ResourceBundle.getBundle(bundleName, locale);
  }


  @Nullable
  @Override
  public String getSource(Source path, Locale locale) {
    try {
      ResourceBundle bundle = getBundle(locale);
      if (bundle == null) {
        return null;
      }
      return bundle.getString(path.toString());
    } catch (MissingResourceException e) {
      return null;
    }
  }


  @Override
  public String getSourceRoot() {
    return bundleName;
  }


  @Override
  public String toString() {
    return String.format(
        "BundleSourceProvider(bundleName='%s')",
        bundleName
    );
  }

}
