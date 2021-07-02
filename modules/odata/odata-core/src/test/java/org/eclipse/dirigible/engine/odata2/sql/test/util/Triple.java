/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql.test.util;

import java.io.Serializable;

public class Triple<T, U, V> implements Serializable {
    private static final long serialVersionUID = 8719382431393826469L;

    private final T first;
    private final U second;
    private final V third;

    public Triple(T a, U b, V c) {
        first = a;
        second = b;
        third = c;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    public V getThird() {
        return third;
    }

   

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        result = prime * result + ((third == null) ? 0 : third.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Triple other = (Triple) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        } else if (!first.equals(other.first))
            return false;
        if (second == null) {
            if (other.second != null)
                return false;
        } else if (!second.equals(other.second))
            return false;
        if (third == null) {
            if (other.third != null)
                return false;
        } else if (!third.equals(other.third))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[" + first + ", " + second + ", " + third + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public static <T, U, V> Triple<T, U, V> create(T a, U b, V c) {
        return new Triple<T, U, V>(a, b, c);
    }

    @SuppressWarnings("unchecked")
    public static final <T, U, V> Triple<T, U, V> nullTriple() {
        return (Triple<T, U, V>) NULL_TRIPLE;
    }

    @SuppressWarnings("rawtypes")
    public static final Triple NULL_TRIPLE = new Triple<Object, Object, Object>(null, null, null);
}
