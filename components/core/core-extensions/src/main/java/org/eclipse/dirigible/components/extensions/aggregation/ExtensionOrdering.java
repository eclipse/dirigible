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

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The ordering every contribution list is sorted by: an entry with an {@code order} before one
 * without, entries with an {@code order} by its integer value, entries without one by a text
 * property.
 *
 * <p>
 * The {@code order} is read as JavaScript's {@code parseInt} reads it - a number is truncated, a
 * string contributes its leading integer - because the script aggregation compared it that way. An
 * {@code order} that holds no integer at all is treated as absent, so the ordering stays a total
 * one.
 */
final class ExtensionOrdering {

    private static final String ORDER = "order";

    /** A leading integer, of at most nine digits so that it always fits an int. */
    private static final Pattern LEADING_INTEGER = Pattern.compile("^\\s*([+-]?\\d{1,9})");

    private ExtensionOrdering() {}

    /**
     * Orders by {@code order}, then case-insensitively by the given text property, as perspectives,
     * views, shells and projections are ordered.
     *
     * @param textProperty the text property, e.g. {@code label}
     * @return the comparator
     */
    static Comparator<Map<String, Object>> byOrderThenText(String textProperty) {
        Collator collator = Collator.getInstance(Locale.ROOT);
        return byOrderThen(Comparator.comparing(entry -> text(entry, textProperty).toLowerCase(Locale.ROOT), collator));
    }

    /**
     * Orders by {@code order}, then by the given text property compared code unit by code unit, as
     * themes and locales are ordered.
     *
     * @param textProperty the text property, e.g. {@code name}
     * @return the comparator
     */
    static Comparator<Map<String, Object>> byOrderThenExactText(String textProperty) {
        return byOrderThen(Comparator.comparing(entry -> text(entry, textProperty)));
    }

    private static Comparator<Map<String, Object>> byOrderThen(Comparator<Map<String, Object>> unordered) {
        return (first, second) -> {
            OptionalInt firstOrder = order(first);
            OptionalInt secondOrder = order(second);
            if (firstOrder.isPresent() && secondOrder.isPresent()) {
                return Integer.compare(firstOrder.getAsInt(), secondOrder.getAsInt());
            }
            if (firstOrder.isEmpty() && secondOrder.isEmpty()) {
                return unordered.compare(first, second);
            }
            return firstOrder.isPresent() ? -1 : 1;
        };
    }

    /**
     * Reads an entry's {@code order}.
     *
     * @param entry the entry
     * @return the order, empty when the entry has none
     */
    static OptionalInt order(Map<String, Object> entry) {
        Object order = entry.get(ORDER);
        if (order instanceof Number number) {
            return OptionalInt.of(number.intValue());
        }
        if (order instanceof String text) {
            Matcher matcher = LEADING_INTEGER.matcher(text);
            if (matcher.find()) {
                return OptionalInt.of(Integer.parseInt(matcher.group(1)));
            }
        }
        return OptionalInt.empty();
    }

    private static String text(Map<String, Object> entry, String property) {
        Object value = entry.get(property);
        return value == null ? "" : value.toString();
    }
}
