package com.pippsford.stencil.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pippsford.stencil.SourceProvider;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.blocks.Apply;
import com.pippsford.stencil.blocks.Block;
import com.pippsford.stencil.blocks.BlockTypes;
import com.pippsford.stencil.blocks.Comment;
import com.pippsford.stencil.blocks.IfDirective;
import com.pippsford.stencil.blocks.Include;
import com.pippsford.stencil.blocks.Include.TemplateProvider;
import com.pippsford.stencil.blocks.LoopDirective;
import com.pippsford.stencil.blocks.Resource;
import com.pippsford.stencil.blocks.SetBlock;
import com.pippsford.stencil.blocks.Static;
import com.pippsford.stencil.blocks.Template;
import com.pippsford.stencil.blocks.UseDirective;
import com.pippsford.stencil.blocks.value.DateTimeValue;
import com.pippsford.stencil.blocks.value.DateTimeValue2;
import com.pippsford.stencil.blocks.value.DateValue;
import com.pippsford.stencil.blocks.value.FormatValue;
import com.pippsford.stencil.blocks.value.NumberValue;
import com.pippsford.stencil.blocks.value.SimpleValue;
import com.pippsford.stencil.blocks.value.TimeValue;
import com.pippsford.stencil.source.SourceStencilId;
import com.pippsford.stencil.source.StencilId;
import com.pippsford.stencil.source.StencilNotFoundException;
import com.pippsford.stencil.source.StencilStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parser for stencil templates.
 *
 * @author Simon Greatrix on 22/12/2020.
 */
public class TemplateParser {

  protected static final Logger logger = LoggerFactory.getLogger(TemplateParser.class);

  /** Regular expression for matching key=value settings. */
  private static final Pattern SETTING = BlockTypes.loadPattern("SETTING_VALUES");


  /**
   * Create a template from the input.
   *
   * @param stencils  the stencil set which will contain the template
   * @param stencilId the ID of the stencil
   *
   * @return the template created
   *
   * @throws StencilParseFailedException if the stencil is invalid
   * @throws StencilNotFoundException    if the stencil cannot be located
   * @throws StencilStorageException     if the stencil cannot be read
   */
  public static Template parse(Stencils stencils, StencilId stencilId)
      throws StencilParseFailedException, StencilNotFoundException, StencilStorageException {
    Context context = new Context(stencils, stencilId);
    logger.debug("Loading {} for parsing", stencilId);
    String text = stencilId.getText();
    logger.debug("Starting parse of {}", stencilId);

    TemplateParser templateParser = new TemplateParser();
    templateParser.context = context;
    templateParser.parser = new BlockParser(text);
    return templateParser.makeTemplate();
  }


  final List<Block> blocks = new ArrayList<>();

  Context context;

  BlockTypes endsWith;

  FixMatch fixMatch;

  BlockParser parser;


  private boolean handleMatch() throws StencilParseFailedException {
    // are we finished?
    if (fixMatch == null) {
      if (endsWith != null) {
        throw new StencilParseFailedException("Missing terminator of type: " + endsWith + " in " + context.getStencilId().getLogId());
      }
      return false;
    }

    // was the match on static text?
    if (fixMatch.type == null) {
      blocks.add(new Static(fixMatch.text, false));
      return true;
    }

    return parseBlock();
  }


  /**
   * Create a template from the input.
   *
   * @return the template created
   */
  private Template makeTemplate() throws StencilParseFailedException {
    do {
      fixMatch = parser.next();
    } while (handleMatch());

    // strip optional whitespace
    for (int i = blocks.size() - 1; i >= 0; i--) {
      removeWhitespace(i);
    }
    Block[] arrBlocks = blocks.toArray(new Block[0]);
    return new Template(arrBlocks);
  }


  private boolean parseBlock() throws StencilParseFailedException {
    switch (fixMatch.type) {
      case APPLY:
        blocks.add(new Apply(fixMatch.groups[2], fixMatch.groups[1], fixMatch.groups[3]));
        break;
      case COMMENT:
        blocks.add(new Comment(false));
        break;
      case ELSE:
        parseElse();
        return false;
      case END:
        parseEnd();
        return false;
      case VALUE_HERE:
        blocks.add(new Static(context.getEscapeStyle(fixMatch.groups[1]).escape(fixMatch.groups[3]), true));
        break;
      case IF:
        parseIf();
        break;
      case RESOURCE_1:
        if (context.getBundle() == null) {
          Location location = fixMatch.getLocation();
          throw new StencilParseFailedException("Resource bundle is unset at " + location.getRow() + ":" + location.getColumn() + " in "
              + context.getStencilId().getLogId());
        }
        blocks.add(new Resource(context, context.getBundle(), fixMatch.groups[1]));
        break;
      case RESOURCE_2:
        blocks.add(new Resource(context, fixMatch.groups[1], fixMatch.groups[2]));
        break;
      case INCLUDE:
        parseInclude();
        break;
      case USE:
        parseUse();
        break;
      case SET:
        blocks.add(SetBlock.INSTANCE);
        parseSet(fixMatch.groups[1]);
        break;
      case VALUE:
        blocks.add(new SimpleValue(context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2]));
        break;
      case VALUE_COMMENT:
        blocks.add(new Comment(true));
        break;
      case VALUE_DATE:
        blocks.add(new DateValue(context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[3]));
        break;
      case VALUE_TIME:
        blocks.add(new TimeValue(context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[3]));
        break;
      case VALUE_DATE_TIME:
        blocks.add(new DateTimeValue(context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[3]));
        break;
      case VALUE_DATE_TIME_2:
        blocks.add(new DateTimeValue2(context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[3], fixMatch.groups[4]));
        break;
      case VALUE_DATE_TIME_HERE:
        blocks.add(new DateTimeValue(context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[4]));
        break;
      case VALUE_FORMAT:
        blocks.add(new FormatValue(context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[3]));
        break;
      case VALUE_FORMAT_HERE:
        blocks.add(new FormatValue(context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[4]));
        break;
      case VALUE_NUMBER:
        blocks.add(new NumberValue(context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[3]));
        break;
      case VALUE_NUMBER_HERE:
        blocks.add(new NumberValue(context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[4]));
        break;
      case LOOP:
        parseLoop();
        break;
      default:
        // unreachable code
        throw new AssertionError("Unknown enumeration constant: " + fixMatch.type);
    }

    return true;
  }


  private void parseBlockElseEnd(BiFunction<Template, Template, Block> blockCreator) throws StencilParseFailedException {
    try {
      Template main = parseSubTemplate(BlockTypes.ELSE);
      Template other = parseSubTemplate(BlockTypes.END);
      blocks.add(blockCreator.apply(main, other));
    } catch (StencilParseFailedException e) {
      Location location = fixMatch.getLocation();
      throw new StencilParseFailedException(
          "Parse problem in block " + fixMatch.type + " at " + location.getRow() + ":" + location.getColumn() + " in "
              + context.getStencilId().getLogId(), e
      );
    }
  }


  private void parseElse() throws StencilParseFailedException {
    // Check for unexpected [else].
    if (BlockTypes.ELSE != endsWith) {
      Location location = fixMatch.getLocation();
      throw new StencilParseFailedException(
          "Unexpected [else] at " + location.getRow() + ":" + location.getColumn() + " in " + context.getStencilId().getLogId());
    }
  }


  private void parseEnd() throws StencilParseFailedException {
    if (BlockTypes.END == endsWith) {
      return;
    }

    // Missing the optional ELSE, so pushback the END and pretend we saw an else.
    if (BlockTypes.ELSE == endsWith) {
      parser.pushback(fixMatch);
      return;
    }

    // Unexpected [end]
    Location location = fixMatch.getLocation();
    throw new StencilParseFailedException("Unexpected [end] at " + location.getRow() + ":" + location.getColumn() + " in " + context.getStencilId().getLogId());

  }


  private void parseIf() throws StencilParseFailedException {
    parseBlockElseEnd((main, other) -> new IfDirective(fixMatch.groups[1], main, other));
  }


  private void parseInclude() throws StencilParseFailedException {
    if (context.getStencilId().isMessage()) {
      throw new StencilParseFailedException("The [include] directive is not allowed within messages.");
    }

    // Clean up the path.
    String path = fixMatch.groups[1];
    if (!path.startsWith("/")) {
      // A relative path.
      String oldPath = ((SourceStencilId) context.getStencilId()).getSource().getPath();
      int lastSlash = oldPath.lastIndexOf('/');
      if (lastSlash != -1) {
        path = oldPath.substring(0, lastSlash + 1) + path;
      }
    }

    SourceProvider sourceProvider = context.getStencils().getSourceProvider();
    TemplateProvider included;
    try {
      final StencilId stencilId = new SourceStencilId(sourceProvider, path, context.getLocale(), context.getBundle(), context.getEscapeStyle());
      included = () -> context.getStencils().getTemplate(stencilId);
    } catch (StencilNotFoundException e) {
      throw new StencilParseFailedException("Invalid path specified for include: " + path, e);
    }
    blocks.add(new Include(included));
  }


  private void parseLoop() throws StencilParseFailedException {
    parseBlockElseEnd((main, other) -> new LoopDirective(fixMatch.groups[1], main, other));
  }


  private void parseSet(String settings) throws StencilParseFailedException {
    if (settings == null) {
      return;
    }
    Matcher matcher = SETTING.matcher(settings);
    while (matcher.find()) {
      String key = matcher.group(1);
      String value = matcher.group(2);

      switch (key.toLowerCase(Locale.ROOT)) {
        case "bundle":
          // Set the default bundle
          context = context.withBundle(value);
          break;
        case "escape":
          // set the default escape scheme
          context = context.withEscapeStyle(context.getStencils().getEscapeResolver().forName(value, context.getEscapeStyle()));
          break;
        case "version":
          // ignore - intended to set a parsing version
          break;
        default:
          throw new StencilParseFailedException("Unrecognised setting \"" + key + "\".");
      }
    }
  }


  private Template parseSubTemplate(BlockTypes newEnd) throws StencilParseFailedException {
    TemplateParser templateParser = new TemplateParser();
    templateParser.context = context;
    templateParser.parser = parser;
    templateParser.endsWith = newEnd;
    return templateParser.makeTemplate();
  }


  private void parseUse() throws StencilParseFailedException {
    parseBlockElseEnd((main, other) -> new UseDirective(fixMatch.groups[1], main, other));
  }


  /**
   * A heuristic to remove whitespace which is introduced to make the template easier to read.
   *
   * @param index the index of the block to inspect
   */
  private void removeWhitespace(int index) {
    Block block = blocks.get(index);
    // Whitespace must be in a static text block
    if (!(block instanceof Static)) {
      return;
    }

    // required static text cannot be removed even if it is whitespace
    Static staticBlock = (Static) block;
    if (staticBlock.isRequired()) {
      return;
    }

    // if it doesn't have a new-line in it, it is not template layout
    String text = staticBlock.getText();
    if (!text.isBlank() || (text.indexOf('\n') == -1 && text.indexOf('\r') == -1)) {
      return;
    }

    // new line after a value should be kept as it is probably output layout
    if (index > 0) {
      Block b = blocks.get(index - 1);
      BlockTypes t = b.getType();
      if (t != null && t.isValue()) {
        return;
      }
    }

    // new line before a value should be kept as it is probably output layout
    if (index < blocks.size() - 1) {
      Block b = blocks.get(index + 1);
      BlockTypes t = b.getType();
      if (t != null && t.isValue()) {
        return;
      }
    }

    // non-required whitespace with a new line that is neither preceded nor succeeded by a value
    blocks.remove(index);
  }

}
