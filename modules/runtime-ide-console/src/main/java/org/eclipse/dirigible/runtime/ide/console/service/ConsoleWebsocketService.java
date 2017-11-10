/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.ide.console.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ConsoleWebsocketService.
 */
@Singleton
@ServerEndpoint("/websockets/v3/ide/console")
public class ConsoleWebsocketService {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ConsoleWebsocketService.class);

	/** The open sessions. */
	private static Map<String, Session> OPEN_SESSIONS = new ConcurrentHashMap<String, Session>();

	/**
	 * On open.
	 *
	 * @param session the session
	 */
	@OnOpen
	public void onOpen(Session session) {
		OPEN_SESSIONS.put(session.getId(), session);
		logger.info("[ws:console] onOpen: " + session.getId());
	}

	/**
	 * On message.
	 *
	 * @param message the message
	 * @param session the session
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		logger.trace("[ws:console] onMessage: " + message);
	}

	/**
	 * On error.
	 *
	 * @param session the session
	 * @param throwable the throwable
	 */
	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.info(String.format("[ws:console] Session %s error %s", session.getId(), throwable.getMessage()));
		logger.error("[ws:console] " + throwable.getMessage(), throwable);
	}

	/**
	 * On close.
	 *
	 * @param session the session
	 * @param closeReason the close reason
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("[ws:console] Session %s closed because of %s", session.getId(), closeReason));
		OPEN_SESSIONS.remove(session.getId());
	}

	/**
	 * Distribute.
	 *
	 * @param record the record
	 */
	public static void distribute(ConsoleLogRecord record) {
		for (Session session : OPEN_SESSIONS.values()) {
			synchronized (session) {
				try {
					session.getBasicRemote().sendText(GsonHelper.GSON.toJson(record));
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

}
