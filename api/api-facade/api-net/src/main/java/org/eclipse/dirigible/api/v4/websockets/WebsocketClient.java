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
package org.eclipse.dirigible.api.v4.websockets;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class WebsocketClient.
 */
@ClientEndpoint
public class WebsocketClient {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(WebsocketClient.class);
	
	/** The handler. */
	private String handler;
	
	/** The engine. */
	private String engine;
	
	/** The session. */
	private Session session;
	
	/**
	 * Gets the handler.
	 *
	 * @return the handler
	 */
	public String getHandler() {
		return handler;
	}
	
	/**
	 * Gets the engine.
	 *
	 * @return the engine
	 */
	public String getEngine() {
		return engine;
	}
	
	/**
	 * Gets the session.
	 *
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}
	
	/**
	 * Instantiates a new websocket client.
	 *
	 * @param handler the handler
	 * @param engine the engine
	 */
	public WebsocketClient(String handler, String engine) {
		this.handler = handler;
		this.engine = engine;
	}
	
    /**
     * On open.
     *
     * @param session the session
     * @throws ScriptingException the scripting exception
     */
    @OnOpen
    public void onOpen(Session session) throws ScriptingException {
    	this.session = session;
    	WebsocketsFacade.CLIENTS.add(this);
    	Map<Object, Object> context = new HashMap<>();
    	context.put("method", "onopen");
    	context.put("handler", this.handler);
    	ScriptEngineExecutorsManager.executeServiceModule(this.engine, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_OPEN, context);
    }

    /**
     * Process message.
     *
     * @param message the message
     * @throws ScriptingException the scripting exception
     */
    @OnMessage
    public void processMessage(String message) throws ScriptingException {
    	Map<Object, Object> context = new HashMap<>();
    	context.put("message", message);
    	context.put("method", "onmessage");
    	context.put("handler", this.handler);
    	ScriptEngineExecutorsManager.executeServiceModule(this.engine, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_MESSAGE, context);
    }

    /**
     * Process error.
     *
     * @param t the t
     * @throws ScriptingException the scripting exception
     */
    @OnError
    public void processError(Throwable t) throws ScriptingException {
    	if (logger.isErrorEnabled()) {logger.error(t.getMessage(), t);}
    	Map<Object, Object> context = new HashMap<>();
    	context.put("error", t.getMessage());
    	context.put("method", "onerror");
    	context.put("handler", this.handler);
    	ScriptEngineExecutorsManager.executeServiceModule(this.engine, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_ERROR, context);
    }
    
    /**
     * On close.
     *
     * @param session the session
     * @throws ScriptingException the scripting exception
     */
    @OnClose
    public void onClose(Session session) throws ScriptingException {
    	WebsocketsFacade.CLIENTS.remove(this);
    	Map<Object, Object> context = new HashMap<>();
    	context.put("method", "onclose");
    	context.put("handler", this.handler);
    	ScriptEngineExecutorsManager.executeServiceModule(this.engine, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_CLOSE, context);
    }

}
