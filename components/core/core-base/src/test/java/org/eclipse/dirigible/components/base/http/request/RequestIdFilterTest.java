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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Every request gets one id, shared by its response header, its log lines and its error dispatch.
 */
class RequestIdFilterTest {

    private final RequestIdFilter filter = new RequestIdFilter();

    @Test
    void aRequestWithoutAnIdGetsANewOneOnTheResponseAndInTheMdc() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/services/anything");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> seenInMdc = new AtomicReference<>();

        filter.doFilter(request, response, chainRecording(seenInMdc));

        String requestId = response.getHeader(RequestIds.HEADER);
        assertNotNull(requestId);
        assertEquals(requestId, seenInMdc.get());
        assertEquals(requestId, RequestIds.of(request)
                                          .orElseThrow());
        assertNull(MDC.get(RequestIds.MDC_KEY), "the MDC is cleared once the request is served");
    }

    @Test
    void aWellFormedIncomingIdIsHonoured() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/services/anything");
        request.addHeader(RequestIds.HEADER, "gateway-4f1c:17");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("gateway-4f1c:17", response.getHeader(RequestIds.HEADER));
    }

    @Test
    void anIncomingIdThatCouldForgeALogLineIsReplaced() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/services/anything");
        request.addHeader(RequestIds.HEADER, "abc\n2026-10-19 [ERROR] forged");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNotEquals("abc\n2026-10-19 [ERROR] forged", response.getHeader(RequestIds.HEADER));
        assertTrue(RequestIds.isAcceptable(response.getHeader(RequestIds.HEADER)));
    }

    @Test
    void anOverlongIncomingIdIsReplaced() {
        assertFalse(RequestIds.isAcceptable("a".repeat(65)));
        assertTrue(RequestIds.isAcceptable("a".repeat(64)));
        assertFalse(RequestIds.isAcceptable(""));
        assertFalse(RequestIds.isAcceptable(null));
    }

    @Test
    void theErrorDispatchReusesTheIdOfTheRequestItReportsOn() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/services/anything");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        String requestId = response.getHeader(RequestIds.HEADER);

        // the container's error page runs after the first dispatch returned and cleared the MDC
        AtomicReference<String> seenInMdc = new AtomicReference<>();
        filter.doFilter(request, response, chainRecording(seenInMdc));

        assertEquals(requestId, seenInMdc.get());
    }

    @Test
    void aNestedDispatchRestoresTheOuterId() throws Exception {
        MDC.put(RequestIds.MDC_KEY, "outer");
        try {
            filter.doFilter(new MockHttpServletRequest("GET", "/services/inner"), new MockHttpServletResponse(), new MockFilterChain());

            assertEquals("outer", MDC.get(RequestIds.MDC_KEY));
        } finally {
            MDC.remove(RequestIds.MDC_KEY);
        }
    }

    private static MockFilterChain chainRecording(AtomicReference<String> seenInMdc) {
        return new MockFilterChain(new HttpServlet() {

            private static final long serialVersionUID = 1L;

            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) {
                seenInMdc.set(MDC.get(RequestIds.MDC_KEY));
            }
        });
    }
}
