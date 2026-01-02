package com.pippsford.stencil.apply;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.pippsford.stencil.value.Data;

/**
 * A function that returns the output of "printStackTrace" on a Throwable.
 *
 * @author Simon Greatrix on 03/02/2022.
 */
public class StackTraceFunction implements ValueProcessor {

  /** The singleton instance of this function. */
  public static final StackTraceFunction INSTANCE = new StackTraceFunction();


  private StackTraceFunction() {
    // do nothing
  }


  @Override
  public Object apply(Data valueProvider, Parameter[] arguments) {
    ValueProcessor.verifyArity(arguments, 0, 1);

    Object value = (arguments.length > 0) ? arguments[0].getValue() : valueProvider.get("cause");
    if (value == null) {
      return "(No causative exception specified)";
    }
    if (!(value instanceof Throwable)) {
      return "(ERROR: cause was a " + value.getClass() + ")";
    }

    StringWriter writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);
    ((Throwable) value).printStackTrace(printWriter);
    printWriter.flush();
    printWriter.close();
    return writer.toString();
  }

}
