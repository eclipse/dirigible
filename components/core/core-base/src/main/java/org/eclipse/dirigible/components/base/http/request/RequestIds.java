/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.http.request;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;

import jakarta.servlet.ServletRequest;

/**
 * The request id every HTTP request carries (#7948).
 *
 * <p>
 * {@link RequestIdFilter} assigns it before anything else runs - honouring a well-formed incoming
 * {@value #HEADER} - and exposes it three ways: as the {@value #HEADER} response header, as the
 * {@value #MDC_KEY} logback MDC key for the request's thread, and as a request attribute, which
 * outlives the MDC into the container's error dispatch. Error bodies carry it as {@value #MDC_KEY},
 * so a user can quote it and an operator can find the request's log lines.
 */
public final class RequestIds {

    /** The request and response header. */
    public static final String HEADER = "X-Request-Id";

    /** The MDC key, and the name of the property in an error body. */
    public static final String MDC_KEY = "requestId";

    /** The request attribute holding the id for the whole exchange, error dispatch included. */
    public static final String ATTRIBUTE = RequestIds.class.getName();

    /** The longest incoming id that is honoured. */
    private static final int MAX_LENGTH = 64;

    private RequestIds() {}

    /**
     * The id of the request the current thread serves.
     *
     * @return the id, empty outside a request
     */
    public static Optional<String> current() {
        return Optional.ofNullable(MDC.get(MDC_KEY));
    }

    /**
     * The id assigned to a request.
     *
     * @param request the request
     * @return the id, empty when the filter did not run for it
     */
    public static Optional<String> of(ServletRequest request) {
        return request.getAttribute(ATTRIBUTE) instanceof String id ? Optional.of(id) : Optional.empty();
    }

    /**
     * Mints a new id.
     *
     * @return the id
     */
    static String newId() {
        return UUID.randomUUID()
                   .toString();
    }

    /**
     * Whether an incoming id is honoured: 1 to 64 letters, digits, {@code .}, {@code _}, {@code :} or
     * {@code -}. Anything else - a header that would forge a log line, or one long enough to bloat it -
     * is replaced by a minted id.
     *
     * @param candidate the incoming value
     * @return whether it is used as is
     */
    static boolean isAcceptable(String candidate) {
        if (candidate == null || candidate.isEmpty() || candidate.length() > MAX_LENGTH) {
            return false;
        }
        for (int i = 0; i < candidate.length(); i++) {
            char c = candidate.charAt(i);
            boolean allowed = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '_' || c == ':'
                    || c == '-';
            if (!allowed) {
                return false;
            }
        }
        return true;
    }
}
