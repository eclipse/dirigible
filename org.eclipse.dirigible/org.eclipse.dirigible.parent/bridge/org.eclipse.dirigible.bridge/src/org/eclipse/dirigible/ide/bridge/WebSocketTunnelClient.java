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

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client side of the WebSocket Tunnel
 */
@ClientEndpoint
public class WebSocketTunnelClient {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketTunnelClient.class.getCanonicalName());

	@OnOpen
	public void onOpen(Session session) throws IOException {
		logger.error("opened tunnelled session");
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		Session inbound = WebSocketTunnel.INBOUND_SESSIONS.get(session.getId());
		if (inbound != null) {
			try {
				inbound.getBasicRemote().sendText(message);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			logger.error("Received a message for unbound session");
		}
	}

	@OnError
	public void onError(Session session, Throwable t) throws Throwable {
		logger.error(t.getMessage(), t);
		Session inbound = WebSocketTunnel.INBOUND_SESSIONS.get(session.getId());
		if (inbound != null) {
			try {
				inbound.getBasicRemote().sendText(t.getMessage());
				inbound.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			logger.error("Received an error message for unbound session");
		}
	}

	@OnClose
	public void onClose(Session session) {
		Session inbound = WebSocketTunnel.INBOUND_SESSIONS.get(session.getId());
		if (inbound != null) {
			try {
				inbound.getBasicRemote().sendText("closed by the peer");
				inbound.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			logger.error("Received closing for unbound session");
		}
	}

}
