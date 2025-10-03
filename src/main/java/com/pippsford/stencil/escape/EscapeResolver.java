package com.pippsford.stencil.escape;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import jakarta.annotation.Nonnull;

import com.pippsford.stencil.parser.StencilParseFailedException;
import com.pippsford.util.CopyOnWriteMap;

/**
 * Global matching for escape styles.
 *
 * @author Simon Greatrix on 10/01/2021.
 */
public class EscapeResolver {

  /** Known escape processors. */
  private final CopyOnWriteArrayList<Escape> instances = new CopyOnWriteArrayList<>();

  /** Known matches. */
  private final CopyOnWriteMap<String, Escape> match = new CopyOnWriteMap<>();

  /** Known additional resolvers. */
  private final CopyOnWriteArrayList<Function<String, Escape>> resolvers = new CopyOnWriteArrayList<>();


  /**
   * New instance.
   */
  public EscapeResolver() {
    for (StandardEscape es : StandardEscape.values()) {
      registerEscapeStyle(es);
    }
  }


  /**
   * Get the style for the provided name. Name matching ignores case and may substitute periods for underscores, or omit the underscore completely.
   *
   * @param name         the name to match
   * @param defaultStyle the style to return if the name is null or empty
   *
   * @return the new style
   *
   * @throws StencilParseFailedException if the name cannot be matched
   */
  public Escape forName(String name, Escape defaultStyle) throws StencilParseFailedException {
    if (name == null || name.isBlank()) {
      return defaultStyle;
    }

    Escape style = match.get(name);
    if (style != null) {
      return style;
    }

    for (Escape escape : instances) {
      if (escape.isHandlerFor(name)) {
        match.put(name, escape);
        return escape;
      }
    }

    for (Function<String, Escape> r : resolvers) {
      Escape escape = r.apply(name);
      if (escape != null) {
        match.put(name, escape);
        return escape;
      }
    }

    throw new StencilParseFailedException("Unrecognised escape style: " + name);
  }


  /**
   * Register a new escape style.
   *
   * @param implementation the new style
   */
  public void registerEscapeStyle(@Nonnull Escape implementation) {
    Objects.requireNonNull(implementation);
    instances.remove(implementation);
    instances.add(implementation);
  }


  /**
   * Register a provider of escape styles.
   *
   * @param resolver a function that returns either an implementation or null
   */
  public void registerSupplementaryResolver(@Nonnull Function<String, Escape> resolver) {
    Objects.requireNonNull(resolver);
    resolvers.remove(resolver);
    resolvers.add(resolver);
  }

}
