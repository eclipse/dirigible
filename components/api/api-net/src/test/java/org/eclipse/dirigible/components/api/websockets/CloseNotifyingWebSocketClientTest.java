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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;

/**
 * The close of a connection is told whichever side closed it - the STOMP session reports only the
 * one the server closed.
 */
class CloseNotifyingWebSocketClientTest {

    @Test
    void theCloseIsToldAfterTheConnectionsOwnHandlerHeardOfIt() throws Exception {
        WebSocketClient delegate = mock(WebSocketClient.class);
        AtomicInteger closes = new AtomicInteger();
        WebSocketHandler stompHandler = mock(WebSocketHandler.class);
        URI uri = URI.create("ws://localhost:1/stomp");

        new CloseNotifyingWebSocketClient(delegate, closes::incrementAndGet).execute(stompHandler, null, uri);

        ArgumentCaptor<WebSocketHandler> opened = ArgumentCaptor.forClass(WebSocketHandler.class);
        verify(delegate).execute(opened.capture(), any(), eq(uri));
        WebSocketSession session = mock(WebSocketSession.class);
        opened.getValue()
              .afterConnectionClosed(session, CloseStatus.NORMAL);

        verify(stompHandler).afterConnectionClosed(session, CloseStatus.NORMAL);
        assertThat(closes).hasValue(1);
    }

    @Test
    void aConnectionByUriTemplateIsToldToo() throws Exception {
        WebSocketClient delegate = mock(WebSocketClient.class);
        AtomicInteger closes = new AtomicInteger();

        new CloseNotifyingWebSocketClient(delegate, closes::incrementAndGet).execute(mock(WebSocketHandler.class),
                "ws://localhost:1/stomp");

        ArgumentCaptor<WebSocketHandler> opened = ArgumentCaptor.forClass(WebSocketHandler.class);
        verify(delegate).execute(opened.capture(), eq("ws://localhost:1/stomp"));
        opened.getValue()
              .afterConnectionClosed(mock(WebSocketSession.class), CloseStatus.GOING_AWAY);

        assertThat(closes).hasValue(1);
    }
}
