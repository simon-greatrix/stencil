package com.pippsford.stencil.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import com.pippsford.stencil.blocks.Patterns;
import com.pippsford.stencil.blocks.ProcessingMode;
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

  /** Logger for this class. */
  protected static final Logger logger = LoggerFactory.getLogger(TemplateParser.class);

  private static final Map<BlockTypes, BlockHandler> HANDLERS;

  /** Regular expression for matching key=value settings. */
  private static final Pattern PATTERN_SETTING = Patterns.get("SETTING_VALUES");



  /** A functional interface that will handle a parsed block. */
  private interface BlockHandler {

    void handle(TemplateParser tp) throws StencilParseFailedException;

  }


  /**
   * Is the text pure whitespace that contains a new-line?.
   *
   * @param text the text
   *
   * @return true if it is just whitespace with at least one new-line
   */
  private static boolean isNewLineWhitespace(String text) {
    return text.isBlank() && (text.indexOf('\n') != -1 || text.indexOf('\r') != -1);
  }


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
    logger.debug("Loading {} for parsing", stencilId);
    PreParser preParser = new PreParser(stencils, stencilId);
    String text = preParser.process();
    TemplateParser templateParser = new TemplateParser(preParser.getContext(), text);
    return templateParser.makeTemplate();
  }


  static Context updateContext(Context context, String settings, boolean allowMode) throws StencilParseFailedException {
    Matcher matcher = PATTERN_SETTING.matcher(settings);

    while (matcher.find()) {
      String key = matcher.group(1);
      String value = matcher.group(2);

      switch (key.toLowerCase(Locale.ROOT)) {
        case "mode":
          if (allowMode) {
            if (value.equalsIgnoreCase(ProcessingMode.NORMAL.name())) {
              context = context.withMode(ProcessingMode.NORMAL);
            } else if (value.equalsIgnoreCase(ProcessingMode.INVERTED.name())) {
              context = context.withMode(ProcessingMode.INVERTED);
            } else {
              throw new StencilParseFailedException("Unknown processing mode \"" + value + "\".");
            }
          } else {
            throw new StencilParseFailedException("Processing mode can only be set in [global] directives.");
          }
          break;
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

    return context;
  }


  static {
    EnumMap<BlockTypes, BlockHandler> map = new EnumMap<>(BlockTypes.class);
    map.put(BlockTypes.APPLY, TemplateParser::parseApply);
    map.put(BlockTypes.COMMENT, TemplateParser::parseComment);
    map.put(BlockTypes.ELSE, TemplateParser::parseElse);
    map.put(BlockTypes.END, TemplateParser::parseEnd);
    map.put(BlockTypes.VALUE_HERE, TemplateParser::parseValueHere);
    map.put(BlockTypes.IF, TemplateParser::parseIf);
    map.put(BlockTypes.RESOURCE_1, TemplateParser::parseResource1);
    map.put(BlockTypes.RESOURCE_2, TemplateParser::parseResource2);
    map.put(BlockTypes.INCLUDE, TemplateParser::parseInclude);
    map.put(BlockTypes.USE, TemplateParser::parseUse);
    map.put(BlockTypes.SET, TemplateParser::parseSet);
    map.put(BlockTypes.VALUE, TemplateParser::parseValue);
    map.put(BlockTypes.VALUE_COMMENT, TemplateParser::parseValueComment);
    map.put(BlockTypes.VALUE_DATE, TemplateParser::parseValueDate);
    map.put(BlockTypes.VALUE_TIME, TemplateParser::parseValueTime);
    map.put(BlockTypes.VALUE_DATE_TIME, TemplateParser::parseValueDateTime);
    map.put(BlockTypes.VALUE_DATE_TIME_2, TemplateParser::parseValueDateTime2);
    map.put(BlockTypes.VALUE_DATE_TIME_HERE, TemplateParser::parseValueDateTimeHere);
    map.put(BlockTypes.VALUE_FORMAT, TemplateParser::parseValueFormat);
    map.put(BlockTypes.VALUE_FORMAT_HERE, TemplateParser::parseValueFormatHere);
    map.put(BlockTypes.VALUE_NUMBER, TemplateParser::parseValueNumber);
    map.put(BlockTypes.VALUE_NUMBER_HERE, TemplateParser::parseValueNumberHere);
    map.put(BlockTypes.LOOP, TemplateParser::parseLoop);

    HANDLERS = Collections.unmodifiableMap(map);
  }

  final List<Block> blocks = new ArrayList<>();

  private final BlockTypes endsWith;

  private final BlockParser parser;

  private Context context;

  private FixMatch fixMatch;


  private TemplateParser(Context context, String text) {
    this.context = context;
    parser = new BlockParser(text);
    endsWith = null;
  }


  private TemplateParser(TemplateParser original, BlockTypes endsWith) {
    context = original.context;
    parser = original.parser;
    this.endsWith = endsWith;
  }


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
      blocks.add(new Static(fixMatch.text, context.getMode() == ProcessingMode.INVERTED));
      return true;
    }

    return parseBlock();
  }


  private boolean isFollowedByValue(int index) {
    index++;
    if (index >= blocks.size()) {
      return false;
    }
    Block b = blocks.get(index);
    BlockTypes t = b.getType();
    return t != null && t.isValue();
  }


  private boolean isPrecededByValue(int index) {
    index--;
    if (index < 0) {
      return false;
    }
    Block b = blocks.get(index);
    BlockTypes t = b.getType();
    return t != null && t.isValue();
  }


  /**
   * Create a template from the input.
   *
   * @return the template created
   */
  private Template makeTemplate() throws StencilParseFailedException {
    runParser();
    Block[] arrBlocks = blocks.toArray(new Block[0]);
    return new Template(arrBlocks);
  }


  private void parseApply() {
    blocks.add(new Apply(fixMatch.groups[2], fixMatch.groups[1], fixMatch.groups[3]));
  }


  private boolean parseBlock() throws StencilParseFailedException {
    HANDLERS.get(fixMatch.type).handle(this);
    return fixMatch.type != BlockTypes.ELSE && fixMatch.type != BlockTypes.END;
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


  private void parseComment() {
    blocks.add(new Comment(false));
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


  private void parseResource1() throws StencilParseFailedException {
    if (context.getBundle() == null) {
      Location location = fixMatch.getLocation();
      throw new StencilParseFailedException("Resource bundle is unset at " + location.getRow() + ":" + location.getColumn() + " in "
          + context.getStencilId().getLogId());
    }
    blocks.add(new Resource(context, context.getBundle(), fixMatch.groups[1]));
  }


  private void parseResource2() {
    blocks.add(new Resource(context, fixMatch.groups[1], fixMatch.groups[2]));
  }


  private void parseSet() throws StencilParseFailedException {
    blocks.add(SetBlock.INSTANCE);
    context = updateContext(context, fixMatch.groups[1], false);
  }


  private Template parseSubTemplate(BlockTypes newEnd) throws StencilParseFailedException {
    TemplateParser templateParser = new TemplateParser(this, newEnd);
    return templateParser.makeTemplate();
  }


  private void parseUse() throws StencilParseFailedException {
    parseBlockElseEnd((main, other) -> new UseDirective(fixMatch.groups[1], main, other));
  }


  private void parseValue() throws StencilParseFailedException {
    blocks.add(new SimpleValue(fixMatch.text, context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2]));
  }


  private void parseValueComment() {
    blocks.add(new Comment(true));
  }


  private void parseValueDate() throws StencilParseFailedException {
    blocks.add(new DateValue(fixMatch.text, context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[3]));
  }


  private void parseValueDateTime() throws StencilParseFailedException {
    blocks.add(new DateTimeValue(fixMatch.text, context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[3]));
  }


  private void parseValueDateTime2() throws StencilParseFailedException {
    blocks.add(new DateTimeValue2(fixMatch.text, context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[3], fixMatch.groups[4]));
  }


  private void parseValueDateTimeHere() throws StencilParseFailedException {
    blocks.add(new DateTimeValue(fixMatch.text, context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[4]));
  }


  private void parseValueFormat() throws StencilParseFailedException {
    blocks.add(new FormatValue(fixMatch.text, context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[3]));
  }


  private void parseValueFormatHere() throws StencilParseFailedException {
    blocks.add(new FormatValue(fixMatch.text, context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[4]));
  }


  private void parseValueHere() throws StencilParseFailedException {
    if (context.getMode() == ProcessingMode.NORMAL) {
      blocks.add(new Static(context.getEscapeStyle(fixMatch.groups[1]).escape(fixMatch.groups[3]), true));
      return;
    }

    // inverted mode, so the here-value is actually a template
    Context newContext = context
        .withEscapeStyle(context.getEscapeStyle(fixMatch.groups[1]))
        .withMode(ProcessingMode.NORMAL);
    TemplateParser newParser = new TemplateParser(newContext, fixMatch.groups[3]);
    newParser.runParser();
    blocks.addAll(newParser.blocks);

    // retain [set] changes to context
    context = context.withChanges(newContext, newParser.context);
  }


  private void parseValueNumber() throws StencilParseFailedException {
    blocks.add(new NumberValue(fixMatch.text, context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[3]));
  }


  private void parseValueNumberHere() throws StencilParseFailedException {
    blocks.add(new NumberValue(fixMatch.text, context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[4]));
  }


  private void parseValueTime() throws StencilParseFailedException {
    blocks.add(new TimeValue(fixMatch.text, context.getEscapeStyle(fixMatch.groups[1]), fixMatch.groups[2], fixMatch.groups[3]));
  }


  /**
   * A heuristic to remove whitespace which is introduced to make the template easier to read.
   *
   * @param index the index of the block to inspect
   */
  private void removeWhitespace(int index) {
    Block block = blocks.get(index);
    // Whitespace must be in a static text block
    if (!(block instanceof Static staticBlock)) {
      return;
    }

    // required static text cannot be removed even if it is whitespace
    if (staticBlock.isRequired()) {
      return;
    }

    // if it doesn't have a new-line in it, it is not template layout
    String text = staticBlock.getText();
    if (!isNewLineWhitespace(text)) {
      return;
    }

    // new line after a value should be kept as it is probably output layout
    if (isPrecededByValue(index)) {
      return;
    }

    // new line before a value should be kept as it is probably output layout
    if (isFollowedByValue(index)) {
      return;
    }

    // non-required whitespace with a new line that is neither preceded nor succeeded by a value
    blocks.remove(index);
  }


  private void runParser() throws StencilParseFailedException {
    boolean isNormal = context.getMode() == ProcessingMode.NORMAL;
    do {
      fixMatch = parser.next(isNormal);
    } while (handleMatch());

    // strip optional whitespace
    for (int i = blocks.size() - 1; i >= 0; i--) {
      removeWhitespace(i);
    }
  }

}
