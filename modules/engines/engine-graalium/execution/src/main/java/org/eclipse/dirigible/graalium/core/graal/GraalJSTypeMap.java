package org.eclipse.dirigible.graalium.core.graal;

import java.util.Objects;
import java.util.function.Function;

public class GraalJSTypeMap<S, T> {
    private final Class<S> sourceClass;
    private final Class<T> targetClass;
    private final Function<S, T> converter;

    public GraalJSTypeMap(Class<S> sourceClass, Class<T> targetClass, Function<S, T> converter) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.converter = converter;
    }

    public Class<S> getSourceClass() {
        return sourceClass;
    }

    public Class<T> getTargetClass() {
        return targetClass;
    }

    public Function<S, T> getConverter() {
        return converter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraalJSTypeMap<?, ?> that = (GraalJSTypeMap<?, ?>) o;
        return Objects.equals(sourceClass, that.sourceClass) && Objects.equals(targetClass, that.targetClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceClass, targetClass);
    }
}
