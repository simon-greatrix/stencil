package com.pippsford.stencil.blocks;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Patterns {

  private static final Pattern LABEL = Pattern.compile("![A-Z_]+!");

  private static final Map<String, Pattern> PATTERNS;


  /**
   * Recursively expand a value.
   *
   * @param properties the original specifications.
   * @param expanded   values that have already been expanded
   * @param name       the name of the entry to expand
   *
   * @return the expanded value
   */
  private static String expandPattern(Properties properties, HashMap<String, String> expanded, String name) {
    // Already done?
    if (expanded.containsKey(name)) {
      return expanded.get(name);
    }

    // Get the value from the original properties
    String value = properties.getProperty(name);
    if (value == null) {
      throw new InternalError("No definition found for \"" + name + "\".");
    }

    // Repeatedly replace labels
    while (true) {
      Matcher m = LABEL.matcher(value);
      if (!m.find()) {
        // No labels left, so fully expanded
        expanded.put(name, value);
        return value;
      }

      // Recursively expand the label
      String r = expandPattern(properties, expanded, m.group());

      // Replace the label
      value = value.replace(m.group(), r);
    }
  }


  /**
   * Get a named pattern.
   *
   * @param name the pattern's name
   *
   * @return the pattern
   */
  public static Pattern get(String name) {
    return Objects.requireNonNull(PATTERNS.get(name));
  }


  static {
    // Load the properties resources
    Properties properties = new Properties();
    try (InputStream in = BlockTypes.class.getResourceAsStream("patterns.xml")) {
      properties.loadFromXML(in);
    } catch (IOException ioException) {
      throw new InternalError("Required internal resource was unavailable", ioException);
    }

    HashMap<String, Pattern> done = new HashMap<>();
    HashMap<String, String> expanded = new HashMap<>();

    for (String name : properties.stringPropertyNames()) {
      String value = expandPattern(properties, expanded, name);

      try {
        done.put(
            name, Pattern.compile(
                value,
                Pattern.UNICODE_CHARACTER_CLASS + Pattern.COMMENTS + Pattern.CASE_INSENSITIVE + Pattern.DOTALL
            )
        );
      } catch (PatternSyntaxException badPattern) {
        throw new InternalError("Bad specification for " + name + "\n" + value, badPattern);
      }
    }

    PATTERNS = Map.copyOf(done);
  }

}
