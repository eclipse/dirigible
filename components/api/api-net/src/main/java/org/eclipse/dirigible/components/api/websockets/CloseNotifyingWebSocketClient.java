/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.websockets;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

/**
 * Tells about the close of every connection it opens, whichever side closed it. A STOMP session
 * reports a connection lost without a DISCONNECT, but not the one its own client disconnected - and
 * a listener's close callback has to fire for both.
 */
final class CloseNotifyingWebSocketClient implements WebSocketClient {

    private final WebSocketClient delegate;

    private final Runnable onClosed;

    /**
     * Instantiates the client.
     *
     * @param delegate the client opening the connections
     * @param onClosed run once a connection is closed
     */
    CloseNotifyingWebSocketClient(WebSocketClient delegate, Runnable onClosed) {
        this.delegate = delegate;
        this.onClosed = onClosed;
    }

    @Override
    public CompletableFuture<WebSocketSession> execute(WebSocketHandler webSocketHandler, String uriTemplate, Object... uriVariables) {
        return delegate.execute(notifyingOnClose(webSocketHandler), uriTemplate, uriVariables);
    }

    @Override
    public CompletableFuture<WebSocketSession> execute(WebSocketHandler webSocketHandler, WebSocketHttpHeaders headers, URI uri) {
        return delegate.execute(notifyingOnClose(webSocketHandler), headers, uri);
    }

    private WebSocketHandler notifyingOnClose(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                try {
                    super.afterConnectionClosed(session, closeStatus);
                } finally {
                    onClosed.run();
                }
            }
        };
    }
}
