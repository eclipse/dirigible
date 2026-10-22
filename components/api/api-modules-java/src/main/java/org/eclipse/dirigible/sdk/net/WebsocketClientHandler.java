/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.net;

import org.eclipse.dirigible.components.api.websockets.WebsocketClientListener;

/**
 * Java handler of an outbound STOMP WebSocket client opened with
 * {@link Websockets#createWebsocket(String, WebsocketClientHandler, java.util.Map)} - the Java
 * counterpart of the handler script the scripting API names. Once connected, the client subscribes
 * to the handler's {@link #destinations()} ({@code /user/queue/reply} unless overridden) and calls
 * the lifecycle callbacks the handler overrides; the rest inherit empty defaults.
 *
 * <p>
 * The callbacks run on the client's transport threads. The component opening the connection can be
 * the handler itself, or hand over one built with its collaborators; the session
 * {@link #onOpen(org.springframework.messaging.simp.stomp.StompSession)} receives is the one to
 * send on.
 *
 * <p>
 * Example, connecting to the {@code orders} endpoint of a Dirigible instance:
 *
 * <pre>{@code
 * StompSession session = Websockets.createWebsocket("wss://dirigible.example.com/stomp", new WebsocketClientHandler() {
 *     public List<String> destinations() { return List.of("/user/queue/reply/orders"); }
 *     public void onMessage(String message, String destination) { ... }
 * }, Map.of("Authorization", "Bearer " + token));
 * session.send("/ws/stomp/orders", Map.of("text", "hello", "from", "java-client"));
 * }</pre>
 */
public interface WebsocketClientHandler extends WebsocketClientListener {
}
