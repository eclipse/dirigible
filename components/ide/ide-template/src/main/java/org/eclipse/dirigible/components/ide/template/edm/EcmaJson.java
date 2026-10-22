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

import java.util.List;
import java.util.Map;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * JSON read and written as {@code JSON.parse} and {@code JSON.stringify} do, for the
 * {@link EcmaValues values} of the EDM transformation.
 *
 * <p>
 * Reading accepts exactly what {@code JSON.parse} accepts - no comments, quotes other than double
 * ones, trailing commas, leading zeros or trailing content - and a key that repeats keeps the
 * position of its first occurrence and the value of its last. Writing orders an object's keys as
 * ECMAScript enumerates them, leaves out a member holding {@code undefined}, writes a number in its
 * ECMAScript form ({@code 1}, not {@code 1.0}; {@code 1e+21}) and a non-finite one as {@code null},
 * and escapes in a string only the quote, the backslash, the control characters and a lone
 * surrogate.
 */
final class EcmaJson {

    private static final JsonMapper JSON = JsonMapper.builder()
                                                     .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                                                     .build();

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    /**
     * Not instantiable.
     */
    private EcmaJson() {}

    /**
     * Reads a JSON text.
     *
     * @param text the text
     * @return the value it denotes
     * @throws JacksonException when the text is not JSON
     */
    static Object parse(String text) {
        return JSON.readValue(text, Object.class);
    }

    /**
     * Writes a value as {@code JSON.stringify(value, null, indent)} does.
     *
     * @param value the value, neither {@code undefined} nor holding a cycle
     * @param indent the indentation of one nesting level
     * @return the JSON text
     */
    static String stringify(Object value, String indent) {
        StringBuilder text = new StringBuilder(4096);
        write(text, value, indent, "");
        return text.toString();
    }

    private static void write(StringBuilder text, Object value, String indent, String currentIndent) {
        if (value instanceof Map<?, ?> object) {
            writeObject(text, object, indent, currentIndent);
        } else if (value instanceof List<?> array) {
            writeArray(text, array, indent, currentIndent);
        } else if (value instanceof String string) {
            writeString(text, string);
        } else if (value instanceof Number number) {
            double numeric = number.doubleValue();
            text.append(Double.isFinite(numeric) ? EcmaValues.numberToText(numeric) : "null");
        } else if (value instanceof Boolean bool) {
            text.append(bool.booleanValue());
        } else {
            text.append("null");
        }
    }

    private static void writeObject(StringBuilder text, Map<?, ?> object, String indent, String currentIndent) {
        String memberIndent = currentIndent + indent;
        boolean empty = true;
        for (String key : EcmaValues.ownKeys(object)) {
            Object member = object.get(key);
            if (member == EcmaValues.UNDEFINED) {
                continue;
            }
            text.append(empty ? "{\n" : ",\n")
                .append(memberIndent);
            writeString(text, key);
            text.append(": ");
            write(text, member, indent, memberIndent);
            empty = false;
        }
        text.append(empty ? "{}" : "\n" + currentIndent + "}");
    }

    private static void writeArray(StringBuilder text, List<?> array, String indent, String currentIndent) {
        if (array.isEmpty()) {
            text.append("[]");
            return;
        }
        String elementIndent = currentIndent + indent;
        text.append('[');
        for (int i = 0; i < array.size(); i++) {
            text.append(i == 0 ? "\n" : ",\n")
                .append(elementIndent);
            // An undefined element is written as null, as JSON has no undefined.
            write(text, array.get(i), indent, elementIndent);
        }
        text.append('\n')
            .append(currentIndent)
            .append(']');
    }

    private static void writeString(StringBuilder text, String string) {
        text.append('"');
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            switch (c) {
                case '"' -> text.append("\\\"");
                case '\\' -> text.append("\\\\");
                case '\b' -> text.append("\\b");
                case '\f' -> text.append("\\f");
                case '\n' -> text.append("\\n");
                case '\r' -> text.append("\\r");
                case '\t' -> text.append("\\t");
                default -> {
                    if (c < 0x20 || isLoneSurrogate(string, i)) {
                        writeUnicodeEscape(text, c);
                    } else {
                        text.append(c);
                    }
                }
            }
        }
        text.append('"');
    }

    private static boolean isLoneSurrogate(String string, int index) {
        char c = string.charAt(index);
        if (Character.isHighSurrogate(c)) {
            return index + 1 >= string.length() || !Character.isLowSurrogate(string.charAt(index + 1));
        }
        if (Character.isLowSurrogate(c)) {
            return index == 0 || !Character.isHighSurrogate(string.charAt(index - 1));
        }
        return false;
    }

    private static void writeUnicodeEscape(StringBuilder text, char c) {
        text.append("\\u")
            .append(HEX_DIGITS[(c >> 12) & 0xF])
            .append(HEX_DIGITS[(c >> 8) & 0xF])
            .append(HEX_DIGITS[(c >> 4) & 0xF])
            .append(HEX_DIGITS[c & 0xF]);
    }

}
