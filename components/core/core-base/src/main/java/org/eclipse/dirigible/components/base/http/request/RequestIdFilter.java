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

import java.io.IOException;

import org.slf4j.MDC;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Assigns every request its {@link RequestIds request id}.
 *
 * <p>
 * Runs on the request, async and error dispatches alike: the first one assigns the id and stores it
 * on the request, a later one - the container's error page in particular, which runs after the
 * first dispatch has returned and cleared the MDC - reuses the stored id, so the error body and its
 * log lines name the same request as the response header.
 */
class RequestIdFilter extends HttpFilter {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String requestId = RequestIds.of(request)
                                     .orElseGet(() -> assign(request, response));
        String previous = MDC.get(RequestIds.MDC_KEY);
        MDC.put(RequestIds.MDC_KEY, requestId);
        try {
            chain.doFilter(request, response);
        } finally {
            if (previous == null) {
                MDC.remove(RequestIds.MDC_KEY);
            } else {
                MDC.put(RequestIds.MDC_KEY, previous);
            }
        }
    }

    private static String assign(HttpServletRequest request, HttpServletResponse response) {
        String incoming = request.getHeader(RequestIds.HEADER);
        String requestId = RequestIds.isAcceptable(incoming) ? incoming : RequestIds.newId();
        request.setAttribute(RequestIds.ATTRIBUTE, requestId);
        response.setHeader(RequestIds.HEADER, requestId);
        return requestId;
    }
}
