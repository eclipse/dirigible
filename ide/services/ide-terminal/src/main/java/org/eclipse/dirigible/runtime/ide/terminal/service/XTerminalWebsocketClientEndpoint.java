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
package org.eclipse.dirigible.runtime.ide.terminal.service;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * XTerminal Websocket Proxy Client.
 */
@ClientEndpoint(subprotocols="tty")
public class XTerminalWebsocketClientEndpoint {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(XTerminalWebsocketClientEndpoint.class);
	
	/** The session. */
	private Session session = null;
    
    /** The message handler. */
    private MessageHandler messageHandler;
 
    /**
     * Instantiates a new x terminal websocket client endpoint.
     *
     * @param endpointURI the endpoint URI
     */
    public XTerminalWebsocketClientEndpoint(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
     * Callback hook for Connection open events.
     * 
     * @param session
     *            the session which is opened.
     */
    @OnOpen
    public void onOpen(Session session) {
    	if (logger.isInfoEnabled()) {logger.info("[ws:terminal-client] connected: " + session.getId());}
        this.session = session;
    }
 
    /**
     * Callback hook for Connection close events.
     * 
     * @param session
     *            the session which is getting closed.
     * @param reason
     *            the reason for connection close
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {
    	if (logger.isInfoEnabled()) {logger.info("[ws:terminal-client] disconnected: " + session.getId());}
        this.session = null;
    }
 
    /**
     * Callback hook for Message Events. This method will be invoked when a
     * client send a message.
     * 
     * @param message the message
     */
    @OnMessage
    public void onMessage(ByteBuffer message) {
        if (this.messageHandler != null) {
			try {
				this.messageHandler.handleMessage(message);
			} catch (IOException e) {
				logger.error("[ws:terminal-client] " + e.getMessage(), e);
			}
        }
    }
 
    /**
     * Register message handler.
     *
     * @param messageHandler the message handler
     */
    public void addMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
 
    /**
     * Send a message.
     * 
     * @param message the message
     */
    public void sendMessage(ByteBuffer message) {
    	synchronized(this.session) {
    		try {
				this.session.getBasicRemote().sendBinary(message);
			} catch (IOException e) {
				logger.error("[ws:terminal-client] " + e.getMessage(), e);
			}
    	}
    }
 
    /**
     * Message handler.
     * 
     */
    public static interface MessageHandler {
        
        /**
         * Handle message.
         *
         * @param message the message
         * @throws IOException Signals that an I/O exception has occurred.
         */
        public void handleMessage(ByteBuffer message) throws IOException;
    }
}
