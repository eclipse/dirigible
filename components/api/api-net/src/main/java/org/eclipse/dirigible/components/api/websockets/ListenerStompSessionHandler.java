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

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.ConnectionLostException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.util.MimeType;

/**
 * Hands the events of a STOMP session to the {@link WebsocketClientListener} of its client: the
 * subscriptions and the open event once connected, every message of a subscription, an ERROR frame,
 * a failure and - reported by the transport through {@link #connectionClosed()} - the close.
 *
 * <p>
 * Frames arrive as the bytes the broker sent ({@link RawPayloadMessageConverter}) and are decoded
 * with the charset their content type names, UTF-8 otherwise.
 */
class ListenerStompSessionHandler implements StompSessionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerStompSessionHandler.class);

    private final WebsocketClient client;

    private final WebsocketClientListener listener;

    /** Set once the session is connected, so a refused connection is never reported as closed. */
    private final AtomicBoolean open = new AtomicBoolean();

    /**
     * Instantiates the handler of a client's session.
     *
     * @param client the client whose session this is
     * @param listener the client's listener
     */
    ListenerStompSessionHandler(WebsocketClient client, WebsocketClientListener listener) {
        this.client = client;
        this.listener = listener;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        for (String destination : listener.destinations()) {
            session.subscribe(destination, new DestinationFrameHandler(destination));
        }
        open.set(true);
        client.opened(session);
        listener.onOpen(session);
    }

    /** The frames of the session itself, which a broker sends only to report an error. */
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return byte[].class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        String reason = headers.getFirst("message");
        String error = reason != null ? reason : text(headers, payload);
        LOGGER.warn("The broker of the websocket client [{}] of [{}] reported an error: {}", client.getHandler(), client.getUri(), error);
        report(error);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        LOGGER.error("Handling a [{}] frame of the websocket client [{}] of [{}] failed", command, client.getHandler(), client.getUri(),
                exception);
        report(exception.getMessage());
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        if (exception instanceof ConnectionLostException) {
            // a close without a DISCONNECT - reported to the listener as the close it is
            LOGGER.debug("The connection of the websocket client [{}] of [{}] was lost", client.getHandler(), client.getUri(), exception);
            return;
        }
        LOGGER.error("The transport of the websocket client [{}] of [{}] failed", client.getHandler(), client.getUri(), exception);
        report(exception.getMessage());
    }

    /**
     * Reports the close of the connection - once, and only for a session that was opened. Called by the
     * transport, which a throwing listener must not disturb.
     */
    void connectionClosed() {
        if (!open.compareAndSet(true, false)) {
            return;
        }
        client.closed();
        try {
            listener.onClose();
        } catch (RuntimeException e) {
            LOGGER.error("The close handler of the websocket client [{}] of [{}] failed", client.getHandler(), client.getUri(), e);
        }
    }

    /** Hands an error to the listener, which must not take the session down with it when it throws. */
    private void report(String error) {
        try {
            listener.onError(error);
        } catch (RuntimeException e) {
            LOGGER.error("The error handler of the websocket client [{}] of [{}] failed", client.getHandler(), client.getUri(), e);
        }
    }

    private static String text(StompHeaders headers, Object payload) {
        if (!(payload instanceof byte[] bytes)) {
            return "";
        }
        MimeType contentType = headers.getContentType();
        Charset charset = contentType != null && contentType.getCharset() != null ? contentType.getCharset() : StandardCharsets.UTF_8;
        return new String(bytes, charset);
    }

    /** Delivers the messages of one subscription. */
    private final class DestinationFrameHandler implements StompFrameHandler {

        private final String destination;

        DestinationFrameHandler(String destination) {
            this.destination = destination;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            listener.onMessage(text(headers, payload), destination);
        }
    }
}
