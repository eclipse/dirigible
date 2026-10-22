/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.template.edm;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The ECMAScript semantics of the values the EDM transformation reads and writes: how a member is
 * read, which value is truthy, when two values are strictly equal, which keys a {@code for-in}
 * enumerates and in which order, and how a value reads as text.
 *
 * <p>
 * The transformation was a script, and the {@code .model} it writes is compared byte for byte, so
 * these rules are part of its output: the order of an object's keys, for one, is the order of the
 * {@code .model}'s. A value is a {@code Map<String, Object>} (an object, its keys in insertion
 * order), a {@code List<Object>} (an array), a {@code String}, a {@code Number}, a {@code Boolean},
 * {@code null}, or {@link #UNDEFINED}.
 */
final class EcmaValues {

    /**
     * The {@code undefined} value: what reading an absent member gives, and what assigning it stores.
     */
    static final Object UNDEFINED = Undefined.VALUE;

    /** The largest array index: {@code 2^32 - 2}. */
    private static final long MAX_ARRAY_INDEX = 4_294_967_294L;

    /** The most significant digits a double needs to be read back exactly. */
    private static final int MAX_DOUBLE_DIGITS = 17;

    /** The decimal exponent up to which a number is written without an exponent. */
    private static final int MAX_PLAIN_EXPONENT = 21;

    /** The decimal exponent above which a fraction is written without an exponent. */
    private static final int MIN_PLAIN_EXPONENT = -6;

    /** The type of {@link #UNDEFINED}, so that it reads as what it stands for. */
    private enum Undefined {
        VALUE;

        @Override
        public String toString() {
            return "undefined";
        }
    }

    /**
     * Not instantiable.
     */
    private EcmaValues() {}

    /**
     * Reads a member, as {@code target[key]} does.
     *
     * @param target the value read from
     * @param key the member name
     * @return the member, or {@link #UNDEFINED} when the value has none
     * @throws IllegalArgumentException when the value is {@code null} or {@code undefined}, which have
     *         no members to read
     */
    static Object member(Object target, String key) {
        if (target instanceof Map<?, ?> object) {
            return object.containsKey(key) ? object.get(key) : UNDEFINED;
        }
        if (target instanceof List<?> array) {
            long index = arrayIndex(key);
            return index >= 0 && index < array.size() ? array.get((int) index) : UNDEFINED;
        }
        if (target instanceof String string) {
            long index = arrayIndex(key);
            return index >= 0 && index < string.length() ? String.valueOf(string.charAt((int) index)) : UNDEFINED;
        }
        if (target == null || target == UNDEFINED) {
            throw new IllegalArgumentException("Cannot read [" + key + "] of " + target);
        }
        return UNDEFINED;
    }

    /**
     * The keys a {@code for-in} loop enumerates: an object's own keys, an array's or a string's
     * indices, and nothing for any other value.
     *
     * @param value the value
     * @return the keys, in enumeration order
     */
    static List<String> keys(Object value) {
        if (value instanceof Map<?, ?> object) {
            return ownKeys(object);
        }
        int length = indexCount(value);
        List<String> keys = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            keys.add(Integer.toString(i));
        }
        return keys;
    }

    /**
     * The number of indices a value has: an array's elements, a string's characters, none otherwise.
     *
     * @param value the value
     * @return the count
     */
    private static int indexCount(Object value) {
        if (value instanceof List<?> array) {
            return array.size();
        }
        if (value instanceof String string) {
            return string.length();
        }
        return 0;
    }

    /**
     * The own keys of an object in the order ECMAScript enumerates them: the array indices first, in
     * ascending numeric order, then every other key in insertion order.
     *
     * @param object the object
     * @return the keys
     */
    static List<String> ownKeys(Map<?, ?> object) {
        List<String> indices = new ArrayList<>();
        List<String> names = new ArrayList<>(object.size());
        for (Object key : object.keySet()) {
            String name = (String) key;
            if (arrayIndex(name) >= 0) {
                indices.add(name);
            } else {
                names.add(name);
            }
        }
        if (indices.isEmpty()) {
            return names;
        }
        indices.sort((first, second) -> Long.compare(arrayIndex(first), arrayIndex(second)));
        indices.addAll(names);
        return indices;
    }

    /**
     * Tests a value the way a condition does.
     *
     * @param value the value
     * @return false for {@code undefined}, {@code null}, {@code false}, zero, NaN and the empty string;
     *         true otherwise
     */
    static boolean isTruthy(Object value) {
        if (value == null || value == UNDEFINED) {
            return false;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            double numeric = number.doubleValue();
            return numeric != 0 && !Double.isNaN(numeric);
        }
        if (value instanceof String string) {
            return !string.isEmpty();
        }
        return true;
    }

    /**
     * Compares two values the way {@code ===} does.
     *
     * @param first the first value
     * @param second the second value
     * @return whether they are strictly equal
     */
    static boolean strictlyEquals(Object first, Object second) {
        if (first instanceof Number left && second instanceof Number right) {
            return left.doubleValue() == right.doubleValue();
        }
        if (first instanceof String || first instanceof Boolean) {
            return first.equals(second);
        }
        return first == second;
    }

    /**
     * Converts a value to text the way a string concatenation does.
     *
     * @param value the value
     * @return the text
     */
    static String toText(Object value) {
        if (value instanceof String string) {
            return string;
        }
        if (value instanceof Number number) {
            return numberToText(number.doubleValue());
        }
        if (value instanceof List<?> array) {
            return join(array, ",");
        }
        if (value instanceof Map<?, ?>) {
            return "[object Object]";
        }
        return String.valueOf(value);
    }

    /**
     * Joins the elements of an array the way {@code Array.prototype.join} does.
     *
     * @param array the array
     * @param separator the separator
     * @return the text
     */
    static String join(List<?> array, String separator) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (i > 0) {
                text.append(separator);
            }
            Object element = array.get(i);
            if (element != null && element != UNDEFINED) {
                text.append(toText(element));
            }
        }
        return text.toString();
    }

    /**
     * Removes the leading and trailing white space and line terminators, as
     * {@code String.prototype.trim} does.
     *
     * @param text the text
     * @return the trimmed text
     */
    static String trim(String text) {
        int start = 0;
        int end = text.length();
        while (start < end && isWhiteSpace(text.charAt(start))) {
            start++;
        }
        while (end > start && isWhiteSpace(text.charAt(end - 1))) {
            end--;
        }
        return text.substring(start, end);
    }

    /**
     * Writes a number the way {@code Number.prototype.toString} does: the shortest digits that read
     * back as the same double, the closest of them to its exact value, in plain notation between
     * {@code 1e-7} and {@code 1e21} and in exponent notation outside.
     *
     * @param number the number
     * @return the text
     */
    static String numberToText(double number) {
        if (Double.isNaN(number)) {
            return "NaN";
        }
        if (Double.isInfinite(number)) {
            return number > 0 ? "Infinity" : "-Infinity";
        }
        if (number == 0) {
            // Negative zero, too.
            return "0";
        }
        BigDecimal shortest = shortestDecimal(Math.abs(number));
        String digits = shortest.unscaledValue()
                                .toString();
        int length = digits.length();
        int exponent = length - shortest.scale();
        StringBuilder text = new StringBuilder(number < 0 ? "-" : "");
        if (length <= exponent && exponent <= MAX_PLAIN_EXPONENT) {
            text.append(digits)
                .append("0".repeat(exponent - length));
        } else if (0 < exponent && exponent <= MAX_PLAIN_EXPONENT) {
            text.append(digits, 0, exponent)
                .append('.')
                .append(digits, exponent, length);
        } else if (MIN_PLAIN_EXPONENT < exponent && exponent <= 0) {
            text.append("0.")
                .append("0".repeat(-exponent))
                .append(digits);
        } else {
            text.append(digits.charAt(0));
            if (length > 1) {
                text.append('.')
                    .append(digits, 1, length);
            }
            text.append('e')
                .append(exponent - 1 >= 0 ? '+' : '-')
                .append(Math.abs(exponent - 1));
        }
        return text.toString();
    }

    /**
     * The decimal with the fewest significant digits that reads back as the double, and of those the
     * closest to its exact value, the one with an even last digit on a tie. At a given number of digits
     * only the two decimals around the exact value can read back as it, any other being further away on
     * the same side.
     *
     * @param number a positive finite double
     * @return the decimal, without trailing zeros
     */
    private static BigDecimal shortestDecimal(double number) {
        BigDecimal exact = new BigDecimal(number);
        for (int digits = 1; digits < MAX_DOUBLE_DIGITS; digits++) {
            BigDecimal below = exact.round(new MathContext(digits, RoundingMode.DOWN));
            BigDecimal above = exact.round(new MathContext(digits, RoundingMode.UP));
            boolean belowReadsBack = below.doubleValue() == number;
            boolean aboveReadsBack = above.doubleValue() == number;
            if (belowReadsBack && aboveReadsBack) {
                int comparison = exact.subtract(below)
                                      .compareTo(above.subtract(exact));
                BigDecimal closest = comparison < 0 || (comparison == 0 && isEven(below)) ? below : above;
                return closest.stripTrailingZeros();
            }
            if (belowReadsBack || aboveReadsBack) {
                return (belowReadsBack ? below : above).stripTrailingZeros();
            }
        }
        return exact.round(new MathContext(MAX_DOUBLE_DIGITS, RoundingMode.HALF_EVEN))
                    .stripTrailingZeros();
    }

    /**
     * Tests whether the last significant digit of a decimal is even.
     *
     * @param decimal the decimal
     * @return whether its unscaled value is even
     */
    private static boolean isEven(BigDecimal decimal) {
        return !decimal.unscaledValue()
                       .testBit(0);
    }

    /**
     * The array index a key names: a canonical decimal integer from 0 to {@code 2^32 - 2}.
     *
     * @param key the key
     * @return the index, or -1 when the key is not one
     */
    private static long arrayIndex(String key) {
        int length = key.length();
        if (length == 0 || length > 10 || (length > 1 && key.charAt(0) == '0')) {
            return -1;
        }
        long index = 0;
        for (int i = 0; i < length; i++) {
            char c = key.charAt(i);
            if (c < '0' || c > '9') {
                return -1;
            }
            index = index * 10 + (c - '0');
        }
        return index <= MAX_ARRAY_INDEX ? index : -1;
    }

    /**
     * Tests whether a character is ECMAScript white space or a line terminator.
     *
     * @param c the character
     * @return whether {@code trim} removes it
     */
    private static boolean isWhiteSpace(char c) {
        return c == '\t' || c == '\n' || c == '\u000B' || c == '\f' || c == '\r' || c == ' ' || c == ' ' || c == '﻿'
                || Character.getType(c) == Character.SPACE_SEPARATOR;
    }

}
