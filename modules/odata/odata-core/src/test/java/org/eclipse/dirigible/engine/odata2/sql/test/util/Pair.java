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

public class Pair<T, U> implements Serializable {
    private static final long serialVersionUID = 8719382431393826469L;

    private final T first;
    private final U second;

    // To Support Ion Serialization
    private Pair() {
        this.first = null;
        this.second = null;
    }

    public Pair(T a, U b) {
        this.first = a;
        this.second = b;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "[" + first + ", " + second + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public static <T, U> Pair<T, U> create(T a, U b) {
        return new Pair<T, U>(a, b);
    }

    @SuppressWarnings("unchecked")
    public static final <T, U> Pair<T, U> nullPair() {
        return (Pair<T, U>) NULL_PAIR;
    }

    @SuppressWarnings("rawtypes")
    public static final Pair NULL_PAIR = new Pair<Object, Object>(null, null);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
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
        Pair other = (Pair) obj;
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
        return true;
    }
}
