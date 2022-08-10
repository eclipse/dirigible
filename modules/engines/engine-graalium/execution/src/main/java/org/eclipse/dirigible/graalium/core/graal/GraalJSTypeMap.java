package org.eclipse.dirigible.graalium.core.graal;

import java.util.Objects;
import java.util.function.Function;

/**
 * The Class GraalJSTypeMap.
 *
 * @param <S> the generic type
 * @param <T> the generic type
 */
public class GraalJSTypeMap<S, T> {
    
    /** The source class. */
    private final Class<S> sourceClass;
    
    /** The target class. */
    private final Class<T> targetClass;
    
    /** The converter. */
    private final Function<S, T> converter;

    /**
     * Instantiates a new graal JS type map.
     *
     * @param sourceClass the source class
     * @param targetClass the target class
     * @param converter the converter
     */
    public GraalJSTypeMap(Class<S> sourceClass, Class<T> targetClass, Function<S, T> converter) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.converter = converter;
    }

    /**
     * Gets the source class.
     *
     * @return the source class
     */
    public Class<S> getSourceClass() {
        return sourceClass;
    }

    /**
     * Gets the target class.
     *
     * @return the target class
     */
    public Class<T> getTargetClass() {
        return targetClass;
    }

    /**
     * Gets the converter.
     *
     * @return the converter
     */
    public Function<S, T> getConverter() {
        return converter;
    }

    /**
     * Equals.
     *
     * @param o the o
     * @return true, if successful
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraalJSTypeMap<?, ?> that = (GraalJSTypeMap<?, ?>) o;
        return Objects.equals(sourceClass, that.sourceClass) && Objects.equals(targetClass, that.targetClass);
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(sourceClass, targetClass);
    }
}
