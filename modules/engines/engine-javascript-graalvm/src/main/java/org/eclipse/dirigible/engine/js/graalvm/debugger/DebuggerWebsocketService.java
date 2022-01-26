/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.graalvm.debugger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.js.graalvm.processor.GraalVMJavascriptEngineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Debugger Websocket Service.
 */
@ServerEndpoint(value = "/websockets/v4/ide/debugger/{path}")
public class DebuggerWebsocketService {

	private static final Logger logger = LoggerFactory.getLogger(DebuggerWebsocketService.class);

	private static Map<String, Session> OPEN_SESSIONS = new ConcurrentHashMap<String, Session>();
	private static Map<String, DebuggerWebsocketClientEndpoint> SESSION_TO_CLIENT = new ConcurrentHashMap<String, DebuggerWebsocketClientEndpoint>();
	
	
	/**
	 * On open callback.
	 *
	 * @param session
	 *            the session
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("path") final String path) {
		logger.debug("[ws:debugger] onOpen: " + session.getId());
		try {
			DebuggerWebsocketClientEndpoint clientEndPoint = startClientWebsocket(session, path.replace("-", "/"));
			SESSION_TO_CLIENT.put(session.getId(), clientEndPoint);
		} catch (URISyntaxException e) {
			logger.error("[ws:debugger] " + e.getMessage(), e);
			try {
				session.close();
			} catch (IOException e1) {
				logger.error("[ws:debugger] " + e.getMessage(), e);
			}
		}
		OPEN_SESSIONS.put(session.getId(), session);
	}

	/**
	 * On message callback.
	 *
	 * @param message
	 *            the byte buffer
	 * @param session
	 *            the session
	 */
	@OnMessage
	public void onMessage(ByteBuffer message, Session session) {
		logger.trace("[ws:debugger] onMessage: " + new String(message.array()));
		
		DebuggerWebsocketClientEndpoint clientEndPoint = SESSION_TO_CLIENT.get(session.getId());
		
		if (clientEndPoint != null) {
			synchronized(clientEndPoint) {
				// send message to websocket
			    clientEndPoint.sendMessage(message);
			}
		}
	}

	/**
	 * On error callback.
	 *
	 * @param session
	 *            the session
	 * @param throwable
	 *            the throwable
	 */
	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.info(String.format("[ws:debugger] Session %s error %s", session.getId(), throwable.getMessage()));
		logger.error("[ws:debugger] " + throwable.getMessage(), throwable);
	}

	/**
	 * On close callback.
	 *
	 * @param session
	 *            the session
	 * @param closeReason
	 *            the close reason
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.trace(String.format("[ws:debugger] Session %s closed because of %s", session.getId(), closeReason));
		OPEN_SESSIONS.remove(session.getId());
		DebuggerWebsocketClientEndpoint clientEndPoint = SESSION_TO_CLIENT.remove(session.getId());
		try {
			if (clientEndPoint != null) {
				clientEndPoint.getSession().close();
			}
		} catch (IOException e) {
			logger.error("[ws:debugger] " + e.getMessage(), e);
		}
	}

	/**
	 * Start the WebSocket proxy
	 * 
	 * @param session the source session
	 * @param port the port for the client
	 * @throws URISyntaxException 
	 */
	private DebuggerWebsocketClientEndpoint startClientWebsocket(Session session, String path) throws URISyntaxException {
		
		String port = Configuration.get(GraalVMJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_PORT, GraalVMJavascriptEngineExecutor.DEFAULT_DEBUG_PORT);
		
		final DebuggerWebsocketClientEndpoint clientEndPoint =
				new DebuggerWebsocketClientEndpoint(new URI("ws://localhost:" + port + path));

        // add listener
        clientEndPoint.addMessageHandler(new DebuggerWebsocketClientEndpoint.MessageHandler() {
            public void handleMessage(ByteBuffer message) throws IOException {
            	synchronized(session) {
            		session.getBasicRemote().sendBinary(message);
            	}
            }
        });
        
        return clientEndPoint;
	}

}
