/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.afterburner.core.engine;

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
