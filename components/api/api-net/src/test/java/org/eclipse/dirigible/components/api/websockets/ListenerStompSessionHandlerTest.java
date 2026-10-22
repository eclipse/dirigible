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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.stomp.ConnectionLostException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.MimeType;

/**
 * The events of a STOMP session reach the Java listener of its client (#8017): the subscriptions
 * and the open event once connected, each message as text, the broker's errors and the client's own
 * failures, and the close - once, and only for a session that was opened.
 */
class ListenerStompSessionHandlerTest {

    private static final String DESTINATION = "/user/queue/reply/orders";

    private final RecordingListener listener = new RecordingListener();

    private final WebsocketClient client = new WebsocketClient("ws://localhost:1/stomp", listener, Map.of());

    private final ListenerStompSessionHandler handler = new ListenerStompSessionHandler(client, listener);

    private final StompSession session = mock(StompSession.class);

    @AfterEach
    void delistTheClient() {
        WebsocketsFacade.CLIENTS.remove(client);
    }

    @Test
    void anOpenedSessionSubscribesListsTheClientAndTellsTheListener() {
        handler.afterConnected(session, new StompHeaders());

        verify(session).subscribe(eq(DESTINATION), any(StompFrameHandler.class));
        assertThat(listener.events).containsExactly("open");
        assertThat(WebsocketsFacade.getClients()).contains(client);
        assertThat(client.getSession()).isSameAs(session);
        assertThat(client.getHandler()).isEqualTo(RecordingListener.class.getName());
    }

    @Test
    void aMessageReachesTheListenerAsTextOfItsCharset() {
        StompFrameHandler subscription = subscription();
        StompHeaders headers = new StompHeaders();
        headers.setContentType(new MimeType("text", "plain", StandardCharsets.ISO_8859_1));

        subscription.handleFrame(headers, "café".getBytes(StandardCharsets.ISO_8859_1));

        assertThat(subscription.getPayloadType(headers)).isEqualTo(byte[].class);
        assertThat(listener.events).containsExactly("open", "message:café@" + DESTINATION);
    }

    @Test
    void anEmptyMessageReachesTheListenerAsEmptyText() {
        subscription().handleFrame(new StompHeaders(), null);

        assertThat(listener.events).containsExactly("open", "message:@" + DESTINATION);
    }

    @Test
    void anErrorFrameReachesTheListenerWithTheBrokersReason() {
        StompHeaders headers = new StompHeaders();
        headers.set("message", "Unauthorized");

        handler.handleFrame(headers, "details".getBytes(StandardCharsets.UTF_8));

        assertThat(listener.events).containsExactly("error:Unauthorized");
    }

    @Test
    void aFailingCallbackIsReportedAsAnError() {
        handler.handleException(session, StompCommand.MESSAGE, new StompHeaders(), new byte[0], new IllegalStateException("boom"));

        assertThat(listener.events).containsExactly("error:boom");
    }

    @Test
    void aLostConnectionIsAClosedOneNotAnError() {
        handler.afterConnected(session, new StompHeaders());

        handler.handleTransportError(session, new ConnectionLostException("Connection closed"));
        handler.connectionClosed();
        handler.connectionClosed();

        assertThat(listener.events).containsExactly("open", "close");
        assertThat(WebsocketsFacade.getClients()).doesNotContain(client);
    }

    @Test
    void aSessionThatNeverOpenedIsNeverReportedClosed() {
        handler.connectionClosed();

        assertThat(listener.events).isEmpty();
    }

    @Test
    void aThrowingErrorHandlerDoesNotTakeTheSessionDown() {
        WebsocketClientListener throwing = new WebsocketClientListener() {

            @Override
            public void onError(String error) {
                throw new IllegalStateException("the handler failed too");
            }
        };
        ListenerStompSessionHandler throwingHandler =
                new ListenerStompSessionHandler(new WebsocketClient("ws://localhost:1/stomp", throwing, Map.of()), throwing);

        assertThatCode(
                () -> throwingHandler.handleTransportError(session, new IllegalStateException("refused"))).doesNotThrowAnyException();
    }

    private StompFrameHandler subscription() {
        handler.afterConnected(session, new StompHeaders());
        ArgumentCaptor<StompFrameHandler> subscription = ArgumentCaptor.forClass(StompFrameHandler.class);
        verify(session).subscribe(eq(DESTINATION), subscription.capture());
        return subscription.getValue();
    }

    private static final class RecordingListener implements WebsocketClientListener {

        private final List<String> events = new ArrayList<>();

        @Override
        public List<String> destinations() {
            return List.of(DESTINATION);
        }

        @Override
        public void onOpen(StompSession session) {
            events.add("open");
        }

        @Override
        public void onMessage(String message, String destination) {
            events.add("message:" + message + "@" + destination);
        }

        @Override
        public void onError(String error) {
            events.add("error:" + error);
        }

        @Override
        public void onClose() {
            events.add("close");
        }
    }
}
