package com.pippsford.stencil.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.blocks.Patterns;
import com.pippsford.stencil.source.StencilId;
import com.pippsford.stencil.source.StencilNotFoundException;
import com.pippsford.stencil.source.StencilStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pre-parse a document to find and process all [global] directives.
 */
public class PreParser {
  /** Logger for this class. */
  protected static final Logger logger = LoggerFactory.getLogger(PreParser.class);

  /** Regular expression for matching the [global] directive. */
  private static final Pattern PATTERN_GLOBAL = Patterns.get("GLOBAL");

  /** Regular expression for identifying if a [global] directive should be ignored. */
  private static final Pattern PATTERN_IGNORE = Patterns.get("!IGNORE!");

  /** Regular expression for matching key=value settings. */
  private static final Pattern PATTERN_SETTING = Patterns.get("SETTING_VALUES");

  private Context context;


  public PreParser(Stencils stencils, StencilId stencilId) {
    this.context = new Context(stencils, stencilId);
  }


  public Context getContext() {
    return context;
  }


  public String process() throws StencilNotFoundException, StencilStorageException, StencilParseFailedException {
    String text = context.getStencilId().getText();

    logger.debug("Starting parse of {}", context.getStencilId());

    Matcher matcher = PATTERN_GLOBAL.matcher(text);
    if (!matcher.find()) {
      // No [global] blocks
      return text;
    }

    StringBuilder builder = new StringBuilder();
    do {
      // is this ignored?
      if (matcher.group(1) != null) {
        // remove the first 'ignore'
        Matcher ignore = PATTERN_IGNORE.matcher(matcher.group());
        String replacement = ignore.replaceFirst("");
        matcher.appendReplacement(builder, replacement);
        continue;
      }

      // parse the settings
      String settingsText = matcher.group(2);
      Matcher setting = PATTERN_SETTING.matcher(settingsText);
      while (setting.find()) {
        context = TemplateParser.updateContext(context, matcher.group(), true);
      }
      matcher.appendReplacement(builder, "");
    } while (matcher.find());

    matcher.appendTail(builder);

    return builder.toString();
  }

}
