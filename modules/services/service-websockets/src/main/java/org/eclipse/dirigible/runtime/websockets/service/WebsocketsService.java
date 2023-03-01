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
package org.eclipse.dirigible.runtime.websockets.service;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.api.v4.websockets.WebsocketsFacade;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.websockets.api.WebsocketsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Websockets Service.
 */
@ServerEndpoint("/websockets/v4/service/{endpoint}")
public class WebsocketsService {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(WebsocketsService.class);
	
	/** The handler. */
	private WebsocketHandler handler = null;
	
	/**
	 * On open callback.
	 *
	 * @param session
	 *            the session
	 * @param endpoint
	 *            the endpoint
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("endpoint") String endpoint) {
		if (logger.isDebugEnabled()) {logger.debug(String.format("[websocket] Endpoint '%s' openned.", endpoint));}
		Map<Object, Object> context = new HashMap<>();
    	context.put("method", "onopen");
    	try {
    		getHandler().processEvent(endpoint, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_OPEN, context);
		} catch (WebsocketsException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}
	}
	

	/**
	 * On message callback.
	 *
	 * @param message
	 *            the message
	 * @param session
	 *            the session
	 * @param endpoint
	 *            the endpoint
	 */
	@OnMessage
	public void onMessage(String message, Session session, @PathParam("endpoint") String endpoint) {
		if (logger.isTraceEnabled()) {logger.trace(String.format("[websocket] Endpoint '%s' received message:%s ", endpoint, message));}
		Map<Object, Object> context = new HashMap<>();
		context.put("message", message);
    	context.put("method", "onmessage");
    	try {
    		getHandler().processEvent(endpoint, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_MESSAGE, context);
		} catch (WebsocketsException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}
	}

	/**
	 * On error callback.
	 *
	 * @param session
	 *            the session
	 * @param throwable
	 *            the throwable
	 * @param endpoint
	 *            the endpoint
	 */
	@OnError
	public void onError(Session session, Throwable throwable, @PathParam("endpoint") String endpoint) {
		if (logger.isErrorEnabled()) {logger.error(String.format("[ws:console] Endpoint '%s' error %s", endpoint, throwable.getMessage()));}
		if (logger.isErrorEnabled()) {logger.error("[websocket] " + throwable.getMessage(), throwable);}
		Map<Object, Object> context = new HashMap<>();
		context.put("error", throwable.getMessage());
    	context.put("method", "onerror");
    	try {
    		getHandler().processEvent(endpoint, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_ERROR, context);
		} catch (WebsocketsException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}
	}

	/**
	 * On close callback.
	 *
	 * @param session
	 *            the session
	 * @param closeReason
	 *            the close reason
	 * @param endpoint
	 *            the endpoint
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason, @PathParam("endpoint") String endpoint) {
		if (logger.isDebugEnabled()) {logger.debug(String.format("[websocket] Endpoint '%s' closed because of %s", endpoint, closeReason));}
		Map<Object, Object> context = new HashMap<>();
    	context.put("method", "onclose");
    	try {
    		getHandler().processEvent(endpoint, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_CLOSE, context);
		} catch (WebsocketsException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}
	}
	
	/**
	 * Gets the handler.
	 *
	 * @return the handler
	 */
	protected synchronized WebsocketHandler getHandler() {
		if (this.handler == null) {
			handler = (WebsocketHandler) StaticObjects.get(StaticObjects.WEBSOCKET_HANDLER);
		}
		return this.handler;
	}

}
