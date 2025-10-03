package com.pippsford.stencil.source;

import static com.pippsford.common.StringUtils.logSafe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import jakarta.annotation.Nullable;

/**
 * Provider which loads resources from a file system.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
public class FileSourceProvider extends IndividualSourceProvider {

  private final Path rootPath;


  /**
   * New instance.
   *
   * @param rootPath the document root
   */
  public FileSourceProvider(Path rootPath) {
    this.rootPath = rootPath.toAbsolutePath();
  }


  @Override
  public String getSourceRoot() {
    return rootPath.toUri().toString();
  }


  @Nullable
  @Override
  protected String handleGet(String path) throws StencilStorageException {
    Path file = rootPath.resolve(path.substring(1)).normalize();
    if (Files.isReadable(file) && Files.isRegularFile(file)) {
      try {
        return Files.readString(file);
      } catch (IOException ioException) {
        throw new StencilStorageException("Unable to retrieve file from file system: " + logSafe(path));
      }
    }

    // file does not exist or is not readable
    return null;
  }


  @Override
  public String toString() {
    return String.format(
        "FileSourceProvider(rootPath=%s)",
        rootPath.toUri()
    );
  }

}
