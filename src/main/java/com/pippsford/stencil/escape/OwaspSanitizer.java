package com.pippsford.stencil.escape;

import java.util.Locale;
import java.util.Set;

import org.owasp.html.PolicyFactory;

/**
 * An OWASP HTML Sanitizer that accepts alternative policies. The allows what HTML is permitted in the escaped result to be customized.
 *
 * @author Simon Greatrix on 27/10/2017.
 */
public class OwaspSanitizer implements Escape {

  private final Set<String> names;

  private final PolicyFactory policy;


  /**
   * New instance.
   *
   * @param policy source of an OWASP sanitation policy
   * @param name   the names this escape handler handles
   */
  public OwaspSanitizer(PolicyFactory policy, Set<String> name) {
    names = Set.copyOf(name);
    this.policy = policy;
  }


  @Override
  public String escape(String input) {
    return policy.sanitize(input);
  }


  @Override
  public boolean isHandlerFor(String name) {
    return names.contains(name.toUpperCase(Locale.ROOT));
  }

}
