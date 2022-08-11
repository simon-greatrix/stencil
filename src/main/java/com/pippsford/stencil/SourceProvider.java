package com.pippsford.stencil;

import java.util.Locale;
import javax.annotation.Nullable;

import com.pippsford.stencil.source.StencilStorageException;

/**
 * A provider of stencil source files.
 *
 * @author Simon Greatrix on 29/12/2020.
 */
public interface SourceProvider {

  /**
   * Get the named source file appropriate for the specified locale.
   *
   * @param path   the path that identifies the file
   * @param locale the locale
   *
   * @return the text of the file, or null if it is not found
   *
   * @throws StencilStorageException if stencil storage mechanism fails.
   */
  @Nullable
  String getSource(Source path, Locale locale) throws StencilStorageException;

  /**
   * Get the specification of the "root folder" or equivalent for this provider.
   *
   * @return the source root
   */
  String getSourceRoot();

}
