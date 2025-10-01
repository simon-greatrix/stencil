package com.pippsford.stencil;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import com.pippsford.stencil.apply.AugmentedIndex;
import com.pippsford.stencil.apply.ForFunction;
import com.pippsford.stencil.apply.IsFunction;
import com.pippsford.stencil.apply.StackTraceFunction;
import com.pippsford.stencil.apply.ValueProcessor;
import com.pippsford.stencil.blocks.Template;
import com.pippsford.stencil.escape.Escape;
import com.pippsford.stencil.escape.EscapeResolver;
import com.pippsford.stencil.escape.StandardEscape;
import com.pippsford.stencil.parser.StencilParseFailedException;
import com.pippsford.stencil.parser.TemplateParser;
import com.pippsford.stencil.source.SourceStencilId;
import com.pippsford.stencil.source.StencilId;
import com.pippsford.stencil.source.StencilNotFoundException;
import com.pippsford.stencil.source.StencilStorageException;
import com.pippsford.stencil.value.Data;
import com.pippsford.util.CopyOnWriteMap;

/**
 * Primary API for using stencils.
 *
 * @author Simon Greatrix on 22/12/2020.
 */
public class Stencils {

  private static final Map<String, ValueProcessor> STANDARD_FUNCTIONS = Map.of(
      "index", AugmentedIndex.INSTANCE,
      "for", ForFunction.INSTANCE,
      "is", IsFunction.INSTANCE,
      "stackTrace", StackTraceFunction.INSTANCE
  );

  private final Map<String, Object> defaultValues = new CopyOnWriteMap<>();

  /** Handle for resolving named escape styles. */
  private final EscapeResolver escapeResolver = new EscapeResolver();

  /** Provider of stencil sources. */
  private final SourceProvider sourceProvider;

  /** Map of resources to stencils. */
  private final CopyOnWriteMap<StencilId, Template> stencilMap = new CopyOnWriteMap<>();

  /** The default escape style for values. */
  private Escape defaultEscape = StandardEscape.HTML_SAFE;

  private String defaultResourceBundle;

  private String standardFunctionScope = null;


  /**
   * Create a stencil map.
   *
   * @param sourceProvider the source files
   */
  public Stencils(SourceProvider sourceProvider) {
    this.sourceProvider = sourceProvider;
    setStandardFunctions("F");
  }


  /**
   * Get the default escape style for values.
   *
   * @return the default escape style
   */
  public Escape getDefaultEscape() {
    return defaultEscape;
  }


  /**
   * Get the default resource bundle used by templates. The default resource bundle is initially unset.
   *
   * @return the default resource bundle
   */
  public String getDefaultResourceBundle() {
    return defaultResourceBundle;
  }


  /**
   * Get the default value associated with a specifed key.
   *
   * @param key the key
   *
   * @return the default value
   */
  public Object getDefaultValue(String key) {
    return defaultValues.get(key);
  }


  /**
   * Get the escape style resolver.
   *
   * @return the escape style resolver
   */
  public EscapeResolver getEscapeResolver() {
    return escapeResolver;
  }


  /**
   * Get the source provider.
   *
   * @return the source provider
   */
  public SourceProvider getSourceProvider() {
    return sourceProvider;
  }


  /**
   * Get the template for the ID.
   *
   * @param stencilId the ID.
   *
   * @return the template
   *
   * @throws StencilStorageException     if the storage fails
   * @throws StencilParseFailedException if the stencil does not parse
   * @throws StencilNotFoundException    if the stencil does not exist.
   */
  public Template getTemplate(StencilId stencilId) throws StencilException {
    Template template = stencilMap.get(stencilId);
    if (template != null) {
      return template;
    }

    synchronized (stencilMap.getLock()) {
      template = stencilMap.get(stencilId);
      if (template != null) {
        return template;
      }

      template = TemplateParser.parse(this, stencilId);

      stencilMap.put(stencilId, template);
    }
    return template;
  }


  /**
   * Set the default escape style.
   *
   * @param defaultEscape the new default escape style
   */
  public void setDefaultEscape(Escape defaultEscape) {
    this.defaultEscape = (defaultEscape != null) ? defaultEscape : StandardEscape.NO_ESCAPE;
  }


  /**
   * Set the default resource bundle.
   *
   * @param defaultResourceBundle the new default resource bundle
   */
  public void setDefaultResourceBundle(String defaultResourceBundle) {
    this.defaultResourceBundle = defaultResourceBundle;
  }


  /**
   * Set a default value to be included on all stencils.
   *
   * @param key   the value's key
   * @param value the value's value
   */
  public void setDefaultValue(String key, Object value) {
    if (value != null) {
      defaultValues.put(key, value);
    } else {
      defaultValues.remove(key);
    }
  }


  /**
   * Load the standard functions by default into the specified scope. If scope is null, the standard functions are not loaded.
   *
   * @param scope the new scope.
   */
  public void setStandardFunctions(String scope) {
    if (standardFunctionScope != null) {
      STANDARD_FUNCTIONS.forEach((k, v) -> defaultValues.remove(standardFunctionScope + "." + k, v));
    }
    standardFunctionScope = scope;
    if (scope != null) {
      STANDARD_FUNCTIONS.forEach((k, v) -> defaultValues.putIfAbsent(scope + "." + k, v));
    }
  }


  /**
   * Process the named stencil.
   *
   * @param stencilName the stencil's name
   * @param writer      where to write the stencil to
   * @param params      the input to the stencil
   *
   * @throws IOException      if there is a read or write error
   * @throws StencilException if there is a problem processing the stencils
   */
  public void write(String stencilName, Writer writer, Object params) throws IOException, StencilException {
    write(stencilName, writer, null, null, params);
  }


  /**
   * Process the named stencil.
   *
   * @param stencilName the stencil's name
   * @param writer      where to write the stencil to
   * @param locale      the localization (if null, uses system default)
   * @param zoneId      the time zone (if null, uses system default)
   * @param params      the input to the stencil
   *
   * @throws IOException      if the output of the stencil cannot be written
   * @throws StencilException if the processing of a stencil fails
   */
  public void write(String stencilName, Writer writer, Locale locale, ZoneId zoneId, Object params) throws IOException, StencilException {
    if (locale == null) {
      locale = Locale.getDefault();
    }
    if (zoneId == null) {
      zoneId = ZoneId.systemDefault();
    }

    StencilId stencilId = new SourceStencilId(sourceProvider, stencilName, locale, defaultResourceBundle, defaultEscape);
    Template myStencil = getTemplate(stencilId);

    Data data = Data.create(params);
    defaultValues.forEach(data::putIfMissing);
    myStencil.process(writer, locale, zoneId, data);
  }


  /**
   * Process the named stencil.
   *
   * @param stencilName the stencil's name
   * @param params      the input to the stencil
   *
   * @return the stencil
   *
   * @throws StencilException if there is a problem processing the stencil
   */
  public String write(String stencilName, Object params) throws StencilException {
    return write(stencilName, null, null, params);
  }


  /**
   * Process the named stencil.
   *
   * @param stencilName the stencil's name
   * @param params      the input to the stencil
   * @param zoneId      the date and time zone
   * @param locale      the locale
   *
   * @return the stencil
   *
   * @throws StencilException if the processing of the stencil fails
   */
  public String write(String stencilName, Locale locale, ZoneId zoneId, Object params) throws StencilException {
    StringWriter writer = new StringWriter();
    try {
      write(stencilName, writer, locale, zoneId, params);
    } catch (IOException ioException) {
      // This should not happen with a string writer
      throw new UncheckedIOException("I/O exception occurred without I/O", ioException);
    }
    return writer.toString();
  }


}
