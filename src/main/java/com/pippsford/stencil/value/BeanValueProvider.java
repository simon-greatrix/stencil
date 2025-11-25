package com.pippsford.stencil.value;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.pippsford.common.UncheckedCheckedException;
import jakarta.annotation.Nonnull;

/**
 * A value provider that allows access to a Java bean.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
public class BeanValueProvider extends ReflectedValueProvider {

  /**
   * New instance.
   *
   * @param parent the parent value provider
   * @param bean   the bean to provide values from
   */
  public BeanValueProvider(ValueProvider parent, Object bean) {
    super(parent, bean);
    BeanInfo beanInfo;
    try {
      beanInfo = Introspector.getBeanInfo(bean.getClass());
    } catch (IntrospectionException e) {
      // I do not know of any way in which this code is actually reachable
      throw new UndeclaredThrowableException(e);
    }
    for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
      Method method = descriptor.getReadMethod();
      if (method != null) {
        properties.put(descriptor.getName(), method);
      }
    }
  }

  @Override
  protected boolean ignore(String name) {
    // skip "class"
    return "class".equals(name);
  }
}
