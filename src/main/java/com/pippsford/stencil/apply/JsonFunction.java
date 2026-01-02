package com.pippsford.stencil.apply;

import com.pippsford.stencil.value.Data;
import com.pippsford.stencil.value.JsonConverter;
import com.pippsford.stencil.value.ValueAccessor;
import jakarta.json.JsonValue;

public class JsonFunction implements ValueProcessor {

  /** Singleton instance of this function. */
  public static final ValueProcessor INSTANCE = new JsonFunction();


  private JsonFunction() {
    // private constructor
  }


  @Override
  public Object apply(Data valueProvider, Parameter[] arguments) {
    ValueProcessor.verifyArity(arguments, 0, 2);

    // Get the data to convert to JSON
    boolean hasValue = arguments.length > 0;
    Object input = hasValue ? arguments[0].getValue() : null;
    JsonConverter converter = (JsonConverter) (arguments.length > 1 ? arguments[1].getValue() : null);

    // Handle special case where there is no input but there is a converter
    if (arguments.length == 1 && input instanceof JsonConverter jc) {
      input = null;
      hasValue = false;
      converter = jc;
    }

    // Convert the input to a Data instance
    Data data;
    if (hasValue) {
      if (input == null) {
        return JsonValue.NULL;
      }

      data = new Data(ValueAccessor.makeProvider(valueProvider.getProvider(), input));
    } else {
      data = valueProvider;
    }

    // Convert the Data instance to JSON
    return data.toJson(converter);
  }

}
