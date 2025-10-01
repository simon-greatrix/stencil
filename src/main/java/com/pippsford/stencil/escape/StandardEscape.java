package com.pippsford.stencil.escape;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import com.pippsford.common.StringUtils;

/**
 * Collection of built-in value escape functions.
 *
 * @author Simon Greatrix on 01/01/2021.
 */
public enum StandardEscape implements Escape {
  /** Do not perform any escaping. */
  NO_ESCAPE {
    @Override
    public String escape(String input) {
      return input != null ? input : "";
    }


    @Override
    public boolean isHandlerFor(String name) {
      return super.isHandlerFor(name) || "NO".equals(name.toUpperCase(Locale.ROOT)) || "NONE".equals(name.toUpperCase(Locale.ROOT));
    }
  },

  /** Ensure there are no unescaped HTML symbols. */
  HTML_SAFE {
    @Override
    public String escape(String input) {
      return HTML.escapeOnce(input);
    }
  },

  /** Ensure every character is escaped. */
  HTML_STRICT {
    @Override
    public String escape(String input) {
      return HTML.escape(input);
    }
  },

  /** Escape as a ECMA script (Javascript, JScript, ES6+) string. */
  ECMA {
    @Override
    public String escape(String input) {
      return EcmaScriptLiteral.ECMA.escape(input);
    }
  },

  /** Escape as a ECMA script (Javascript, JScript, ES6+) string, forcing ASCII compliance. */
  ECMA_ASCII {
    @Override
    public String escape(String input) {
      return EcmaScriptLiteral.ECMA_ASCII.escape(input);
    }
  },

  /** Escape as a Java string. */
  JAVA {
    @Override
    public String escape(String input) {
      return JavaScriptLiteral.JAVA.escape(input);
    }
  },

  /** Escape as a Java string, forcing ASCII compliance. */
  JAVA_ASCII {
    @Override
    public String escape(String input) {
      return JavaScriptLiteral.JAVA_ASCII.escape(input);
    }
  },

  /** Escape as a JSON string. */
  JSON {
    @Override
    public String escape(String input) {
      return JsonScriptLiteral.JSON.escape(input);
    }
  },

  /** Escape as a JSON string, forcing ASCII compliance. */
  JSON_ASCII {
    @Override
    public String escape(String input) {
      return JsonScriptLiteral.JSON_ASCII.escape(input);
    }
  },

  /** Escape using a UTF-8 URL Encoder string. */
  URL {
    @Override
    public String escape(String input) {
      return input != null ? URLEncoder.encode(input, StandardCharsets.UTF_8) : "";
    }
  },

  /** Produces a "log safe" output. */
  LOG {
    @Override
    public String escape(String input) {
      return input != null ? StringUtils.logSafe(input) : "";
    }
  };


  @Override
  public boolean isHandlerFor(String name) {
    name = name.toUpperCase(Locale.ROOT).replaceAll("[\\s._-]", "");
    String me = name().replaceAll("_", "");
    return me.equals(name);
  }
}
