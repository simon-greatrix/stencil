package com.pippsford.stencil.value;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
public class ReflectedValueProvider implements ValueProvider {

  /** The bean values are taken from. */
  protected final Object bean;

  /** The parent value provider. */
  protected final ValueProvider parent;

  /** Methods that can be used to retrieve properties. */
  protected final Map<String, Method> properties = new HashMap<>();


  /**
   * New instance.
   *
   * @param parent the parent value provider
   * @param bean   the bean to provide values from
   */
  public ReflectedValueProvider(ValueProvider parent, Object bean) {
    this.parent = parent;
    this.bean = bean;
  }


  @Override
  @Nonnull
  public OptionalValue get(@Nonnull String name) {
    return getLocal(name).orDefault(() -> parent.get(name));
  }


  @Override
  @Nonnull
  public OptionalValue getLocal(@Nonnull String name) {
    Method method = properties.get(name);
    try {
      if (method != null && method.canAccess(bean)) {
        return OptionalValue.of(method.invoke(bean));
      }
      return OptionalValue.absent();
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new UncheckedCheckedException(e);
    }
  }


  /**
   * Ignore properties when visiting this provider.
   *
   * @param name the name that could be ignored
   *
   * @return true if not to visit
   */
  protected boolean ignore(String name) {
    return false;
  }


  @Override
  public void visit(BiConsumer<String, Object> visitor) {
    for (var e : properties.entrySet()) {
      String key = e.getKey();
      if (ignore(key)) {
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
