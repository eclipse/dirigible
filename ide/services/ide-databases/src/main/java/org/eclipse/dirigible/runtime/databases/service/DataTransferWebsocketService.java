/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.databases.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.database.transfer.api.DataTransferDefinition;
import org.eclipse.dirigible.database.transfer.api.DataTransferException;
import org.eclipse.dirigible.database.transfer.api.IDataTransferCallbackHandler;
import org.eclipse.dirigible.database.transfer.callbacks.WriterDataTransferCallbackHandler;
import org.eclipse.dirigible.runtime.databases.processor.DatabaseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

/**
 * The Data Transfer Websocket Service.
 */
@ServerEndpoint("/websockets/v4/ide/data/transfer")
public class DataTransferWebsocketService {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DataTransferWebsocketService.class);

	/** The processor. */
	private DatabaseProcessor processor = new DatabaseProcessor();

	/** The Constant HANDLERS. */
	private static final Map<String, IDataTransferCallbackHandler> HANDLERS = new HashMap<String, IDataTransferCallbackHandler>();

	/**
	 * On open callback.
	 *
	 * @param session the session
	 */
	@OnOpen
	public void onOpen(Session session) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("[ws:transfer] Session %s openned.", session.getId()));
		}
	}

	/**
	 * On message callback.
	 *
	 * @param message the message
	 * @param session the session
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("[ws:ws:transfer] Session %s received message: %s.", session.getId(), message));
		}
		IDataTransferCallbackHandler maybeHandler = HANDLERS.get(session.getId());
		if (message != null && message.startsWith("{")) {
			// start

			if (maybeHandler != null) {
				maybeHandler.stopTransfer();
				HANDLERS.remove(session.getId());
			}
			try {
				DataTransferDefinition definition = GsonHelper.fromJson(message, DataTransferDefinition.class);

				PipedOutputStream pos = new PipedOutputStream();
				PipedInputStream pis = new PipedInputStream(pos, 1024);
				BufferedReader reader = new BufferedReader(new InputStreamReader(pis));
				Writer writer = new BufferedWriter(new OutputStreamWriter(pos));
				final IDataTransferCallbackHandler handler = new WriterDataTransferCallbackHandler(writer,
						session.getId() + new Date().getTime());
				HANDLERS.put(session.getId(), handler);
				new Thread(new Runnable() {
					public void run() {
						try {
							processor.transferData(definition, handler);
							HANDLERS.remove(session.getId());
							// reader.close();
						} catch (DataTransferException e) {
							if (logger.isErrorEnabled()) {
								logger.error(e.getMessage(), e);
							}
						}
					}
				}).start();

				new Thread(new Runnable() {
					public void run() {
						reader.lines().forEach(line -> {
							try {
								session.getBasicRemote().sendText(line);
							} catch (IOException e) {
								if (logger.isErrorEnabled()) {
									logger.error(e.getMessage(), e);
								}
							}
						});
					}
				}).start();

			} catch (JsonSyntaxException e) {
				if (logger.isErrorEnabled()) {
					logger.error(e.getMessage(), e);
				}
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error(e.getMessage(), e);
				}
			}
		} else {
			if ("stop".equalsIgnoreCase(message)) {
				if (maybeHandler != null) {
					maybeHandler.stopTransfer();
					HANDLERS.remove(session.getId());
				}
			}
		}

	}

	/**
	 * On error callback.
	 *
	 * @param session   the session
	 * @param throwable the throwable
	 */
	@OnError
	public void onError(Session session, Throwable throwable) {
		if (logger.isErrorEnabled()) {
			logger.error(
					String.format("[ws:ws:transfer] Session %s error %s", session.getId(), throwable.getMessage()));
		}
		if (logger.isErrorEnabled()) {
			logger.error("[ws:ws:transfer] " + throwable.getMessage(), throwable);
		}
	}

	/**
	 * On close callback.
	 *
	 * @param session     the session
	 * @param closeReason the close reason
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		if (logger.isDebugEnabled()) {
			logger.debug(
					String.format("[ws:ws:transfer] Session %s closed because of %s", session.getId(), closeReason));
		}
	}

}
