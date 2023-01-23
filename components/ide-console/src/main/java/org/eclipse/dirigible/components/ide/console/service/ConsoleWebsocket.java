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
package org.eclipse.dirigible.components.ide.console.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The Console Websocket Service.
 */
@Component
@ServerEndpoint(BaseEndpoint.PREFIX_ENDPOINT_WEBSOCKETS + "ide/console")
public class ConsoleWebsocket {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ConsoleWebsocket.class);

	/** The open sessions. */
	private static Map<String, Session> OPEN_SESSIONS = new ConcurrentHashMap<String, Session>();

	/**
	 * On open callback.
	 *
	 * @param session the session
	 */
	@OnOpen
	public void onOpen(Session session) {
		OPEN_SESSIONS.put(session.getId(), session);
		if (logger.isDebugEnabled()) {logger.debug(String.format("[ws:console] Session %s openned.", session.getId()));}
	}

	/**
	 * On message callback.
	 *
	 * @param message the message
	 * @param session the session
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		if (logger.isDebugEnabled()) {logger.debug(String.format("[ws:console] Session %s received message: %s.", session.getId(), message));}
	}

	/**
	 * On error callback.
	 *
	 * @param session the session
	 * @param throwable the throwable
	 */
	@OnError
	public void onError(Session session, Throwable throwable) {
		if (logger.isErrorEnabled()) {logger.error(String.format("[ws:console] Session %s error %s", session.getId(), throwable.getMessage()));}
		if (logger.isErrorEnabled()) {logger.error("[ws:console] " + throwable.getMessage(), throwable);}
	}

	/**
	 * On close callback.
	 *
	 * @param session the session
	 * @param closeReason the close reason
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		if (logger.isDebugEnabled()) {logger.debug(String.format("[ws:console] Session %s closed because of %s", session.getId(), closeReason));}
		OPEN_SESSIONS.remove(session.getId());
	}

	/**
	 * Distribute message to all the listeners.
	 *
	 * @param record the record
	 */
	public static void distribute(ConsoleLogRecord record) {
		for (Session session : OPEN_SESSIONS.values()) {
			synchronized (session) {
				try {
					if (session.isOpen()) {
						session.getBasicRemote().sendText(GsonHelper.toJson(record));
					}
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

}
