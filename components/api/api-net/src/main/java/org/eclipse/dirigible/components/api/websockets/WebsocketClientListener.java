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

import java.util.List;

import org.springframework.messaging.simp.stomp.StompSession;

/**
 * The Java handler of an outbound STOMP client - the counterpart of the handler script a scripting
 * client names. Once connected, the client subscribes to the {@link #destinations()} and reports
 * its lifecycle and every message arriving on them here.
 *
 * <p>
 * The callbacks run on the threads of the client's transport. A callback that throws is reported to
 * {@link #onError(String)}, and the connection stays open.
 */
public interface WebsocketClientListener {

    /** The user queue a scripting client subscribes to - the destination a listener has by default. */
    String DEFAULT_DESTINATION = "/user/queue/reply";

    /**
     * The destinations the client subscribes to once it is connected. A Dirigible endpoint answers on
     * {@code /user/queue/reply/<endpoint>}.
     *
     * @return the destinations; {@value #DEFAULT_DESTINATION} by default
     */
    default List<String> destinations() {
        return List.of(DEFAULT_DESTINATION);
    }

    /**
     * Fires once the client is connected and subscribed.
     *
     * @param session the STOMP session, to send on - the same one the connect call returns
     */
    default void onOpen(StompSession session) {
        // intentional no-op default
    }

    /**
     * Fires for every message arriving on one of the {@link #destinations()}.
     *
     * @param message the body of the message, as text
     * @param destination the destination it arrived on
     */
    default void onMessage(String message, String destination) {
        // intentional no-op default
    }

    /**
     * Fires when the broker sends an error, the transport fails, or another callback throws.
     *
     * @param error what went wrong
     */
    default void onError(String error) {
        // intentional no-op default
    }

    /** Fires once when the connection of an opened client is closed, by either side. */
    default void onClose() {
        // intentional no-op default
    }
}
