package com.demo.service.tools;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.util.TypeLiteral;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class InstanceStub<T> implements Instance<T> {

  private final List<T> instances;

  public InstanceStub(List<T> instances) {
    this.instances = instances;
  }

  public static <T> InstanceStub<T> of(List<T> instances) {
    return new InstanceStub<>(instances);
  }

  @Override
  public Iterator<T> iterator() {
    return instances.iterator();
  }

  @Override
  public Stream<T> stream() {
    return instances.stream();
  }

  @Override
  public boolean isUnsatisfied() {
    return instances.isEmpty();
  }

  @Override
  public boolean isAmbiguous() {
    return instances.size() > 1;
  }

  @Override
  public void destroy(T instance) {
    // no-op for tests
  }

  @Override
  public T get() {
    return instances.stream().findFirst().orElse(null);
  }

  @Override
  public Handle<T> getHandle() {
    T instance = get();
    return instance == null ? null : new SimpleHandle<>(instance);
  }

  @Override
  public Iterable<? extends Handle<T>> handles() {
    return instances.stream()
        .map(SimpleHandle::new)
        .toList();
  }

  // CDI selection not required for unit tests
  @Override
  public Instance<T> select(Annotation... qualifiers) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <U extends T> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <U extends T> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
    throw new UnsupportedOperationException();
  }

  private static class SimpleHandle<T> implements Handle<T> {

    private final T instance;

    SimpleHandle(T instance) {
      this.instance = instance;
    }

    @Override
    public T get() {
      return instance;
    }

    @Override
    public Bean<T> getBean() {
      // Not available in test stub
      return null;
    }

    @Override
    public void destroy() {
      // no-op
    }

    @Override
    public void close() {
      destroy();
    }
  }
}