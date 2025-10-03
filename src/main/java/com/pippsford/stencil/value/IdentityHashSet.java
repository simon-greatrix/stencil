package com.pippsford.stencil.value;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;

import jakarta.annotation.Nonnull;

/**
 * Set of objects backed by an IdentityHashMap.
 *
 * @author Simon Greatrix on 22/11/2021.
 */
public class IdentityHashSet implements Set<Object> {

  private final IdentityHashMap<Object, Boolean> identities = new IdentityHashMap<>();

  private final Set<Object> keys = identities.keySet();


  @Override
  public boolean add(Object o) {
    return identities.put(o, Boolean.TRUE) == null;
  }


  @Override
  public boolean addAll(@Nonnull Collection<?> c) {
    return keys.addAll(c);
  }


  @Override
  public void clear() {
    keys.clear();
  }


  @Override
  public boolean contains(Object o) {
    return keys.contains(o);
  }


  @Override
  public boolean containsAll(@Nonnull Collection<?> c) {
    return keys.containsAll(c);
  }


  @Override
  public boolean equals(Object o) {
    return (o instanceof Collection<?>) && keys.equals(o);
  }


  @Override
  public int hashCode() {
    return keys.hashCode();
  }


  @Override
  public boolean isEmpty() {
    return keys.isEmpty();
  }


  @Override
  @Nonnull
  public Iterator<Object> iterator() {
    return keys.iterator();
  }


  @Override
  public boolean remove(Object o) {
    return keys.remove(o);
  }


  @Override
  public boolean removeAll(@Nonnull Collection<?> c) {
    return keys.removeAll(c);
  }


  @Override
  public boolean retainAll(@Nonnull Collection<?> c) {
    return keys.retainAll(c);
  }


  @Override
  public int size() {
    return keys.size();
  }


  @Override
  @Nonnull
  public Spliterator<Object> spliterator() {
    return keys.spliterator();
  }


  @Override
  @Nonnull
  public Object[] toArray() {
    return keys.toArray();
  }


  @Override
  @Nonnull
  public <T> T[] toArray(@Nonnull T[] a) {
    return keys.toArray(a);
  }

}
