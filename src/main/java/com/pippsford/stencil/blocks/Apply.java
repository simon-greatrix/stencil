package com.pippsford.stencil.blocks;

import java.io.Writer;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pippsford.stencil.apply.LocalizedValueProcessor;
import com.pippsford.stencil.apply.Parameter;
import com.pippsford.stencil.apply.ParameterDatum;
import com.pippsford.stencil.apply.ParameterLiteral;
import com.pippsford.stencil.apply.ParameterNull;
import com.pippsford.stencil.apply.ValueProcessor;
import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.ValueAccessor;

/**
 * Implement the "apply" block.
 *
 * @author Simon Greatrix on 04/01/2021.
 */
public class Apply implements Block {

  /** Pattern to match an ID. */
  protected static final Pattern PATTERN_ID = Patterns.get("!ID_CHARS!");

  /** Pattern to match a literal. */
  protected static final Pattern PATTERN_LITERAL = Patterns.get("!LITERAL!");

  /** Pattern to match separators like comma or whitespace. */
  protected static final Pattern PATTERN_SEPARATOR = Patterns.get("!SEPARATOR!");

  /** The optional arguments to the function. */
  protected final Parameter[] arguments;

  /**
   * The parameter to output.
   */
  protected final String[] functionName;

  /**
   * The output value key.
   */
  protected final String[] outParameter;


  /**
   * New instance.
   *
   * @param function     the ID of the function to apply
   * @param outParameter an optional parameter to store results in
   * @param arguments    the arguments to the function as comma separated list of IDs.
   */
  public Apply(String function, String outParameter, String arguments) {
    functionName = ValueAccessor.toKey(function);

    if (outParameter != null) {
      this.outParameter = ValueAccessor.toKey(outParameter);
    } else {
      this.outParameter = null;
    }

    if (arguments != null) {
      ArrayList<Parameter> parameterList = new ArrayList<>();
      Matcher matchSeparator = PATTERN_SEPARATOR.matcher(arguments);
      Matcher matchLiteral = PATTERN_LITERAL.matcher(arguments);
      Matcher matchId = PATTERN_ID.matcher(arguments);

      int position = 0;
      int length = arguments.length();
      boolean lastWasSeparator = true;
      while (position < length) {
        if (lastWasSeparator && matchId.region(position, length).lookingAt()) {
          parameterList.add(new ParameterDatum(null, matchId.group()));
          position = matchId.end();
          lastWasSeparator = false;
        }
        if (lastWasSeparator && matchLiteral.region(position, length).lookingAt()) {
          String quoted = matchLiteral.group();
          String unquoted = quoted.substring(1, quoted.length() - 1);
          String unescaped = unquoted.replace("''", "'");
          parameterList.add(new ParameterLiteral(unescaped));
          position = matchLiteral.end();
          lastWasSeparator = false;
        }
        if (matchSeparator.region(position, length).lookingAt()) {
          if (lastWasSeparator) {
            parameterList.add(ParameterNull.INSTANCE);
          }
          position = matchSeparator.end();
          lastWasSeparator = true;
        }
      }
      this.arguments = parameterList.toArray(new Parameter[0]);
    } else {
      this.arguments = new Parameter[0];
    }
  }


  @Override
  public BlockTypes getType() {
    return BlockTypes.APPLY;
  }


  @Override
  public void process(Writer out, Locale locale, ZoneId zoneId, Data data) {
    Object function = data.get(functionName).value();

    int argCount = arguments.length;
    Parameter[] thisArguments = new Parameter[argCount];
    for (int i = 0; i < argCount; i++) {
      thisArguments[i] = arguments[i].withData(data);
    }

    if (function instanceof ValueProcessor) {
      Object result = ((ValueProcessor) function).apply(data, thisArguments);
      if (outParameter != null) {
        data.put(outParameter, result);
      }
      return;
    }

    if (function instanceof LocalizedValueProcessor) {
      Object result = ((LocalizedValueProcessor) function).apply(data, locale, zoneId, thisArguments);
      if (outParameter != null) {
        data.put(outParameter, result);
      }
      return;
    }

    throw new IllegalStateException(
        "Expected a value processor at \"" + String.join(".", functionName) + "\" but got " + (function == null ? "null" : function.getClass()));
  }

}
