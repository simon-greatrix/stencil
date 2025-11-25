package com.pippsford.stencil.value;

import java.lang.reflect.RecordComponent;

/**
 * A value provider that allows access to a Java record.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
public class RecordValueProvider extends ReflectedValueProvider {

  /**
   * New instance.
   *
   * @param parent the parent value provider
   * @param bean   the bean to provide values from
   */
  public RecordValueProvider(ValueProvider parent, Record bean) {
    super(parent, bean);

    RecordComponent[] components = bean.getClass().getRecordComponents();
    for (RecordComponent component : components) {
      properties.put(component.getName(), component.getAccessor());
    }
    if (!properties.containsKey("class")) {
      try {
        properties.put("class", bean.getClass().getMethod("getClass"));
      } catch (NoSuchMethodException e) {
        throw new InternalError("The getClass() method is missing from " + bean.getClass());
      }
    }
  }

}
