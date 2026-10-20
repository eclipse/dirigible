/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.issues;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Masks the secrets an issue's context bundle may carry before it is stored (#7948): the value of
 * every property whose name says it holds a credential, and inside any text a bearer or basic
 * credential, a JWT, a session cookie or a {@code password=}-style pair.
 *
 * <p>
 * Every pattern is linear: possessive quantifiers and no nested repetition, since the input is the
 * reporter's.
 */
final class IssueContextRedactor {

    /** What a secret is replaced with. */
    static final String MASK = "********";

    /** A property name that marks its value secret wherever it appears in the name. */
    private static final Pattern SECRET_NAME =
            Pattern.compile("(?i)authorization|cookie|token|password|passwd|secret|api[-_]?key|credential|session|jwt");

    /** {@code Authorization} scheme and credential. */
    private static final Pattern SCHEME_CREDENTIAL = Pattern.compile("(?i)\\b(Bearer|Basic|Token)\\s++[A-Za-z0-9._~+/=-]++");

    /** A JWT, which starts with an encoded <code>{"</code>. */
    private static final Pattern JWT = Pattern.compile("\\beyJ[A-Za-z0-9_-]++\\.[A-Za-z0-9_-]++\\.[A-Za-z0-9_-]*+");

    /** A session cookie as a request carries it. */
    private static final Pattern SESSION_COOKIE = Pattern.compile("(?i)\\b(JSESSIONID|SESSION|XSRF-TOKEN)=[^;\\s]++");

    /**
     * {@code password=...}, {@code token: ...} and their kin in a query string, a log line or a
     * message.
     */
    private static final Pattern SECRET_PAIR = Pattern.compile(
            "(?i)\\b(password|passwd|secret|token|access_token|refresh_token|id_token|client_secret|api[-_]?key)(\\s*+[=:]\\s*+)[^\\s&;,\"']++");

    private IssueContextRedactor() {}

    /**
     * Redacts a JSON-shaped value: maps, lists, strings and scalars.
     *
     * @param value the value
     * @return the redacted copy
     */
    static Object redact(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> redacted = new LinkedHashMap<>();
            map.forEach((key, item) -> {
                String name = String.valueOf(key);
                redacted.put(name, isSecretName(name) && item != null ? MASK : redact(item));
            });
            return redacted;
        }
        if (value instanceof List<?> list) {
            return list.stream()
                       .map(IssueContextRedactor::redact)
                       .toList();
        }
        if (value instanceof String text) {
            return redactText(text);
        }
        return value;
    }

    /**
     * Redacts the secrets inside a text.
     *
     * @param text the text
     * @return the redacted text
     */
    static String redactText(String text) {
        String redacted = SCHEME_CREDENTIAL.matcher(text)
                                           .replaceAll("$1 " + MASK);
        redacted = JWT.matcher(redacted)
                      .replaceAll(MASK);
        redacted = SESSION_COOKIE.matcher(redacted)
                                 .replaceAll("$1=" + MASK);
        return SECRET_PAIR.matcher(redacted)
                          .replaceAll("$1$2" + MASK);
    }

    private static boolean isSecretName(String name) {
        return SECRET_NAME.matcher(name)
                          .find();
    }
}
