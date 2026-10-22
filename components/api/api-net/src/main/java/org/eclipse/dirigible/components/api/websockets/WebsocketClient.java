/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.websockets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

import org.eclipse.dirigible.components.base.script.ScriptExecutor;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

/**
 * An outbound STOMP client. Its handler is either a script, named by its registry path, or a Java
 * {@link WebsocketClientListener}, named by its class.
 */
@ClientEndpoint
public class WebsocketClient {

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(WebsocketClient.class);

    /** The uri. */
    private String uri;

    /** The handler. */
    private String handler;

    /** The session. */
    private StompSession session;

    /** The executor of the handler's script; {@code null} for a client with a Java listener. */
    private final ScriptExecutor scriptExecutor;

    /**
     * The Java listener of the client's events; {@code null} for a client whose handler is a script.
     */
    private final WebsocketClientListener listener;

    /** The headers of the STOMP CONNECT frame. */
    private final Map<String, String> connectHeaders;

    /**
     * Instantiates a new websocket client that connects with a bare CONNECT frame.
     *
     * @param uri the uri
     * @param scriptExecutor the executor of the handler's script
     * @param handler the handler
     */
    public WebsocketClient(String uri, ScriptExecutor scriptExecutor, String handler) {
        this(uri, scriptExecutor, handler, Map.of());
    }

    /**
     * Instantiates a new websocket client.
     *
     * @param uri the uri
     * @param scriptExecutor the executor of the handler's script
     * @param handler the handler
     * @param connectHeaders the headers of the STOMP CONNECT frame - a Dirigible {@code /stomp}
     *        endpoint needs {@code Authorization: Bearer <token>} there, since an anonymous CONNECT is
     *        refused
     */
    public WebsocketClient(String uri, ScriptExecutor scriptExecutor, String handler, Map<String, String> connectHeaders) {
        this.uri = uri;
        this.scriptExecutor = scriptExecutor;
        this.handler = handler;
        this.listener = null;
        this.connectHeaders = Map.copyOf(connectHeaders);
    }

    /**
     * Instantiates a new websocket client whose handler is a Java listener.
     *
     * @param uri the uri
     * @param listener the listener of the client's events - its class name is the client's handler
     * @param connectHeaders the headers of the STOMP CONNECT frame - a Dirigible {@code /stomp}
     *        endpoint needs {@code Authorization: Bearer <token>} there, since an anonymous CONNECT is
     *        refused
     */
    public WebsocketClient(String uri, WebsocketClientListener listener, Map<String, String> connectHeaders) {
        this.uri = uri;
        this.scriptExecutor = null;
        this.handler = listener.getClass()
                               .getName();
        this.listener = listener;
        this.connectHeaders = Map.copyOf(connectHeaders);
    }

    /**
     * Connect.
     *
     * @return the stomp session
     * @throws InterruptedException the interrupted exception
     * @throws ExecutionException the execution exception
     */
    public StompSession connect() throws InterruptedException, ExecutionException {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        WebSocketClient transport = new SockJsClient(transports);
        StompHeaders headers = new StompHeaders();
        connectHeaders.forEach(headers::add);
        session = listener == null ? connectScriptClient(transport, headers) : connectListenerClient(transport, headers);
        return session;
    }

    private StompSession connectScriptClient(WebSocketClient transport, StompHeaders headers)
            throws InterruptedException, ExecutionException {
        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSessionHandler sessionHandler = new ClientStompSessionHandler();
        return stompClient.connectAsync(uri, (WebSocketHttpHeaders) null, headers, sessionHandler)
                          .get();
    }

    /**
     * Connects a client whose listener receives every message as text and hears of the close, while a
     * payload it sends is converted as a scripting client's is.
     */
    private StompSession connectListenerClient(WebSocketClient transport, StompHeaders headers)
            throws InterruptedException, ExecutionException {
        ListenerStompSessionHandler sessionHandler = new ListenerStompSessionHandler(this, listener);
        WebSocketStompClient stompClient =
                new WebSocketStompClient(new CloseNotifyingWebSocketClient(transport, sessionHandler::connectionClosed));
        stompClient.setMessageConverter(
                new CompositeMessageConverter(List.of(new RawPayloadMessageConverter(), new MappingJackson2MessageConverter())));
        return stompClient.connectAsync(uri, (WebSocketHttpHeaders) null, headers, sessionHandler)
                          .get();
    }

    /**
     * Records the connected session of a client with a Java listener and lists the client.
     *
     * @param session the connected session
     */
    void opened(StompSession session) {
        this.session = session;
        WebsocketsFacade.CLIENTS.add(this);
    }

    /** Delists a client with a Java listener whose connection was closed. */
    void closed() {
        WebsocketsFacade.CLIENTS.remove(this);
    }

    /**
     * Gets the uri.
     *
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Gets the executor of the handler's script.
     *
     * @return the script executor; {@code null} for a client with a Java listener
     */
    public ScriptExecutor getScriptExecutor() {
        return scriptExecutor;
    }

    /**
     * Gets the handler.
     *
     * @return the handler
     */
    public String getHandler() {
        return handler;
    }

    /**
     * Gets the session.
     *
     * @return the session
     */
    public StompSession getSession() {
        return session;
    }

    /**
     * On open.
     *
     * @param session the session
     * @throws Exception the scripting exception
     */
    @OnOpen
    public void onOpen(StompSession session) throws Exception {
        this.session = session;
        WebsocketsFacade.CLIENTS.add(this);
        Map<Object, Object> context = new HashMap<>();
        context.put("method", "onopen");
        context.put("handler", this.handler);
        RepositoryPath path = new RepositoryPath(handler);
        getScriptExecutor().execute(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
    }

    /**
     * Process message.
     *
     * @param message the message
     * @throws Exception the scripting exception
     */
    @OnMessage
    public void processMessage(String message) throws Exception {
        Map<Object, Object> context = new HashMap<>();
        context.put("message", message);
        context.put("method", "onmessage");
        context.put("handler", this.handler);
        RepositoryPath path = new RepositoryPath(WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_MESSAGE);
        getScriptExecutor().execute(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
    }

    /**
     * Process error.
     *
     * @param t the t
     * @throws Exception the scripting exception
     */
    @OnError
    public void processError(Throwable t) throws Exception {
        if (logger.isErrorEnabled()) {
            logger.error(t.getMessage(), t);
        }
        Map<Object, Object> context = new HashMap<>();
        context.put("error", t.getMessage());
        context.put("method", "onerror");
        context.put("handler", this.handler);
        RepositoryPath path = new RepositoryPath(WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_ERROR);
        getScriptExecutor().execute(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
    }

    /**
     * On close.
     *
     * @param session the session
     * @throws Exception the scripting exception
     */
    @OnClose
    public void onClose(Session session) throws Exception {
        WebsocketsFacade.CLIENTS.remove(this);
        Map<Object, Object> context = new HashMap<>();
        context.put("method", "onclose");
        context.put("handler", this.handler);
        RepositoryPath path = new RepositoryPath(WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_CLOSE);
        getScriptExecutor().execute(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
    }

}
