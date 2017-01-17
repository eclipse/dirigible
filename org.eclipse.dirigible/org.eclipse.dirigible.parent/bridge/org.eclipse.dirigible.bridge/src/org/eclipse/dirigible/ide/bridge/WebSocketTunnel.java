/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.bridge;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web Socket Tunnel Endpoint
 * // connect to the dirigible tunnelling endpoint
 * var wsSocket = new WebSocket("ws://dirigible-host:8080/tunnel/ws/example.com/8080/myservice");
 * // continue the work as the external endpoint requires
 * wsSocket.send('xxx...');
 */
@ServerEndpoint("/tunnel/{protocol}/{host}/{port}/{path}")
public class WebSocketTunnel {

	private static final String PARAM_CLIENT = "client";

	private static final Logger logger = LoggerFactory.getLogger(WebSocketTunnel.class.getCanonicalName());

	static Map<String, Session> INBOUND_SESSIONS = Collections.synchronizedMap(new HashMap<String, Session>());

	@OnOpen
	public void onOpen(Session session, @PathParam("protocol") String protocol, @PathParam("host") String host, @PathParam("port") String port,
			@PathParam("path") String path) throws IOException {
		try {
			Session client;
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			String uri = protocol + "://" + host + ":" + port + "/" + path.replace("~", "/");
			logger.debug("Connecting to " + uri);
			client = container.connectToServer(WebSocketTunnelClient.class, URI.create(uri));
			session.getUserProperties().put(PARAM_CLIENT, client);
			INBOUND_SESSIONS.put(client.getId(), session);
			logger.debug("Connected to " + uri);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			session.getBasicRemote().sendText(e.getMessage());
		}
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		Session client = (Session) session.getUserProperties().get(PARAM_CLIENT);
		if (client != null) {
			try {
				client.getBasicRemote().sendText(message);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		} else {

		}
	}

	@OnError
	public void onError(Session session, Throwable t) {
		logger.error(t.getMessage(), t);
		Session client = (Session) session.getUserProperties().get(PARAM_CLIENT);
		if (client != null) {
			INBOUND_SESSIONS.remove(client.getId());
			try {
				client.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@OnClose
	public void onClose(Session session) {
		Session client = (Session) session.getUserProperties().get(PARAM_CLIENT);
		if (client != null) {
			INBOUND_SESSIONS.remove(client.getId());
			try {
				client.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}
