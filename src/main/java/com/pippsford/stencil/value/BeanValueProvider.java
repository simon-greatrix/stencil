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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pippsford.common.UncheckedCheckedException;

/**
 * A value provider that allows access to a Java bean.
 *
 * @author Simon Greatrix on 03/01/2021.
 */
public class BeanValueProvider implements ValueProvider {

  private final Object bean;

  private final ValueProvider parent;

  private final Map<String, Method> properties = new HashMap<>();


  /**
   * New instance.
   *
   * @param parent the parent value provider
   * @param bean   the bean to provide values from
   */
  public BeanValueProvider(ValueProvider parent, Object bean) {
    this.parent = parent;
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
    this.bean = bean;
  }


  @Nullable
  @Override
  public Object get(@Nonnull String name) {
    Method method = properties.get(name);
    try {
      return method != null && method.canAccess(bean) ? method.invoke(bean) : parent.get(name);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new UncheckedCheckedException(e);
    }
  }


  @Nullable
  @Override
  public Object getLocal(@Nonnull String name) {
    Method method = properties.get(name);
    try {
      return method != null && method.canAccess(bean) ? method.invoke(bean) : null;
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new UncheckedCheckedException(e);
    }
  }


  @Override
  public void visit(BiConsumer<String, Object> visitor) {
    for (var e : properties.entrySet()) {
      // skip "class"
      String key = e.getKey();
      if ("class".equals(key)) {
        continue;
      }

      Method method = e.getValue();
      if (method.canAccess(bean)) {
        try {
          visitor.accept(key, method.invoke(bean));
        } catch (IllegalAccessException | InvocationTargetException ex) {
          visitor.accept(key, "<<< UNAVAILABLE : INTERNAL ERROR >>>");
        }
      }
    }
  }

}
