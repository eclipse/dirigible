/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.debug.java.config;

import org.eclipse.dirigible.components.ide.debug.java.JavaDebugManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Refuses the debugger's WebSocket handshake with 503 while the debugger is unavailable, rather
 * than accepting a session that no debug bridge can serve.
 */
class JavaDebugAvailabilityInterceptor implements HandshakeInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaDebugAvailabilityInterceptor.class);

    private final JavaDebugManager manager;

    JavaDebugAvailabilityInterceptor(JavaDebugManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws IOException {
        if (manager.isAvailable()) {
            return true;
        }
        LOGGER.info("[java-debug] Refused a debug session: {}", JavaDebugManager.UNAVAILABLE_REASON);
        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        response.getHeaders()
                .setContentType(MediaType.TEXT_PLAIN);
        response.getBody()
                .write(JavaDebugManager.UNAVAILABLE_REASON.getBytes(StandardCharsets.UTF_8));
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // nothing to release: an accepted handshake carries no state of this interceptor
    }
}
