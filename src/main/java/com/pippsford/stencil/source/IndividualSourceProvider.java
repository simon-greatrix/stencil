package com.pippsford.stencil.source;

import java.util.Locale;
import javax.annotation.Nullable;

import com.pippsford.stencil.Source;
import com.pippsford.stencil.SourceProvider;

/**
 * A source provider where each source is in an individual container.
 *
 * @author Simon Greatrix on 29/12/2020.
 */
public abstract class IndividualSourceProvider implements SourceProvider {


  @Nullable
  @Override
  public String getSource(Source path, Locale locale) throws StencilStorageException {
    for (Locale testLocale : PathLocaliser.getCandidateLocales(locale)) {
      String source = handleGet(PathLocaliser.getSearchName(path.getPath(), path.getSuffix(), testLocale));
      if (source != null) {
        return source;
      }
    }

    // not found
    return null;
  }


  /**
   * Handle the retrieval of a specific path.
   *
   * @param path the path, including a locale specifier
   *
   * @return the source text, or null
   *
   * @throws StencilStorageException if the stencil cannot be read
   */
  @Nullable
  protected abstract String handleGet(String path) throws StencilStorageException;


}
