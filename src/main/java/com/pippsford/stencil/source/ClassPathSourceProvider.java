package com.pippsford.stencil.source;

import static com.pippsford.common.StringUtils.logSafe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import jakarta.annotation.Nullable;

import com.pippsford.stencil.Source;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * A source provider that reads resource files from the class path.
 *
 * @author Simon Greatrix on 29/12/2020.
 */
public class ClassPathSourceProvider extends IndividualSourceProvider {

  /** The class loader used to locate the resources. */
  private final ClassLoader classLoader;

  /** The root to prefix to the resource path. */
  private final String root;


  /**
   * New instance.
   *
   * @param classLoader the class loader to fetch resources from
   * @param root        the root folder for resources
   */
  public ClassPathSourceProvider(ClassLoader classLoader, String root) {
    Objects.requireNonNull(root, "Root folder must be specified");
    if (classLoader == null) {
      classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader == null) {
        classLoader = ClassPathSourceProvider.class.getClassLoader();
      }
    }
    this.classLoader = classLoader;
    try {
      // root always starts with a '/' and does not end with one, so this strips off the '/'
      this.root = Source.cleanPath(root).substring(1);
    } catch (StencilNotFoundException e) {
      throw new IllegalArgumentException("Root path could not be parsed", e);
    }
  }


  /**
   * New instance based upon a specified package.
   *
   * @param classLoader the class loader
   * @param root        the package
   */
  public ClassPathSourceProvider(ClassLoader classLoader, Package root) {
    this(classLoader, root.getName().replace('.', '/'));
  }


  /**
   * New instance with a specified class loader and root class.
   *
   * @param classLoader the class loader
   * @param root        the root class
   */
  public ClassPathSourceProvider(ClassLoader classLoader, Class<?> root) {
    this(classLoader, root.getPackage());
  }


  /**
   * New instance based upon a specific class's location in the class path.
   *
   * @param root the class
   */
  public ClassPathSourceProvider(Class<?> root) {
    this(root.getClassLoader(), root.getPackage());
  }


  /**
   * New instance with specified named root in the current context class loader.
   *
   * @param root the root name
   */
  public ClassPathSourceProvider(String root) {
    this(null, root);
  }


  @Override
  public String getSourceRoot() {
    return root;
  }


  @Nullable
  @Override
  @SuppressFBWarnings(
      value = {"RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "NP_LOAD_OF_KNOWN_NULL_VALUE"},
      justification = "False positive. Redundant check is in the try-with-resources generated code")
  protected String handleGet(String path) throws StencilStorageException {
    try (InputStream in = classLoader.getResourceAsStream(root + path)) {
      if (in == null) {
        return null;
      }

      Reader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
      StringBuilder writer = new StringBuilder();
      int r;
      while ((r = reader.read()) != -1) {
        writer.append((char) r);
      }
      reader.close();
      return writer.toString();
    } catch (IOException ioException) {
      throw new StencilStorageException("Unable to retrieve resource via class loader: " + logSafe(path));
    }
  }


  @Override
  public String toString() {
    return String.format(
        "ClassPathSourceProvider(root='%s')",
        root
    );
  }

}
