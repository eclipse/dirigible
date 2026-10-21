/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.aggregation;

import java.util.Map;

/**
 * Reading the JSON-shaped contributions the way the script aggregation tested them.
 */
final class Contributions {

    private Contributions() {}

    /**
     * Whether a value is truthy as JavaScript defines it: not absent, {@code false}, zero or the empty
     * string. An empty array or object is truthy.
     *
     * @param value the value
     * @return whether it is truthy
     */
    static boolean truthy(Object value) {
        if (value == null || Boolean.FALSE.equals(value)) {
            return false;
        }
        if (value instanceof Number number) {
            double numeric = number.doubleValue();
            return numeric != 0 && !Double.isNaN(numeric);
        }
        return !(value instanceof String text) || !text.isEmpty();
    }

    /**
     * Whether a contribution's property is truthy.
     *
     * @param contribution the contribution
     * @param property the property
     * @return whether the property is truthy
     */
    static boolean truthy(Map<String, Object> contribution, String property) {
        return truthy(contribution.get(property));
    }

    /**
     * Puts a value unless it is absent, as {@code JSON.stringify} leaves out an undefined property.
     *
     * @param target the target object
     * @param property the property
     * @param value the value
     */
    static void putIfPresent(Map<String, Object> target, String property, Object value) {
        if (value != null) {
            target.put(property, value);
        }
    }
}
