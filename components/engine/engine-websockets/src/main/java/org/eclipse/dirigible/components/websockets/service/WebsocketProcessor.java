/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.websockets.service;

import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.dirigible.components.base.script.ScriptEngineUnavailableException;
import org.eclipse.dirigible.components.websockets.domain.Websocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * The Class WebsocketHandler.
 */
@Component
public class WebsocketProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketProcessor.class);

    private static final String JAVA_REGISTRY_CLASS = "org.eclipse.dirigible.engine.java.websocket.JavaWebsocketRegistry";

    /** The websocket service. */
    private final WebsocketService websocketService;

    /** The dispatcher of the script handlers' events, absent where no script engine is deployed. */
    private final ObjectProvider<WebsocketScriptDispatcher> scriptDispatcher;

    private final ApplicationContext applicationContext;

    /**
     * Lazily resolved reference to the optional {@code JavaWebsocketRegistry} bean from
     * {@code engine-java}. Resolved once in {@link #initJavaRegistry()} — {@code null} when
     * {@code engine-java} is not on the classpath (e.g. in unit tests for this module).
     */
    private Object javaWebsocketRegistry;

    /** {@code contains(String)} method on the registry, cached to avoid repeated reflection. */
    private Method registryContainsMethod;

    /** {@code dispatch(String, String, String, String, String)} method on the registry, cached. */
    private Method registryDispatchMethod;

    /**
     * Instantiates a new websocket handler.
     *
     * @param websocketService the websocket service
     * @param scriptDispatcher the dispatcher of the script handlers' events
     * @param applicationContext the Spring application context
     */
    @Autowired
    public WebsocketProcessor(WebsocketService websocketService, ObjectProvider<WebsocketScriptDispatcher> scriptDispatcher,
            ApplicationContext applicationContext) {
        this.websocketService = websocketService;
        this.scriptDispatcher = scriptDispatcher;
        this.applicationContext = applicationContext;
    }

    /**
     * Attempt to locate the optional {@code JavaWebsocketRegistry} bean. Called after all beans are
     * constructed so that {@code engine-java} has time to register its beans regardless of
     * initialisation order.
     */
    @PostConstruct
    void initJavaRegistry() {
        try {
            Class<?> registryClass = Class.forName(JAVA_REGISTRY_CLASS);
            javaWebsocketRegistry = applicationContext.getBean(registryClass);
            registryContainsMethod = registryClass.getMethod("contains", String.class);
            registryDispatchMethod =
                    registryClass.getMethod("dispatch", String.class, String.class, String.class, String.class, String.class);
        } catch (ReflectiveOperationException e) {
            // engine-java not on classpath — Java websocket support disabled.
        } catch (org.springframework.beans.BeansException e) {
            // bean not registered — Java websocket support disabled.
        }
    }

    /**
     * Gets the websocket service.
     *
     * @return the websocket service
     */
    public WebsocketService getWebsocketService() {
        return websocketService;
    }

    /**
     * Process the event.
     *
     * @param endpoint the endpoint
     * @param context the context
     * @return the object
     * @throws Exception the exception
     */
    public Object processEvent(String endpoint, Map<Object, Object> context) throws Exception {
        if (hasJavaHandler(endpoint)) {
            return dispatchToJava(endpoint, context);
        }

        Websocket websocket = websocketService.findByEndpoint(endpoint);
        String module = websocket.getHandler();
        try {
            context.put("handler", module);

            if ("onmessage".equals(context.get("method"))) {
                return executeOnMessageHandler(module, context);
            } else if ("onopen".equals(context.get("method"))) {
                executeOnOpenHandler(module, context);
            } else if ("onclose".equals(context.get("method"))) {
                executeOnCloseHandler(module, context);
            } else if ("onerror".equals(context.get("method"))) {
                executeOnErrorHandler(module, context);
            }

            return null;

        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Dispatch a WebSocket event to the Java handler registered in the optional
     * {@code JavaWebsocketRegistry}. The registry itself resolves the callback shape (typed interface,
     * {@code @OnX} annotations, or reflective method names), so this module needs no
     * {@code engine-java} dependency.
     */
    private Object dispatchToJava(String endpoint, Map<Object, Object> context) {
        if (javaWebsocketRegistry == null || registryDispatchMethod == null) {
            return null;
        }
        String method = (String) context.get("method");
        try {
            return registryDispatchMethod.invoke(javaWebsocketRegistry, endpoint, method, asString(context.get("message")),
                    asString(context.get("from")), asString(context.get("error")));
        } catch (ReflectiveOperationException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            LOGGER.error("Java @Websocket handler [{}] threw on [{}]: {}", endpoint, method, cause.getMessage(), cause);
            return null;
        }
    }

    private boolean hasJavaHandler(String endpoint) {
        if (javaWebsocketRegistry == null || registryContainsMethod == null) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(registryContainsMethod.invoke(javaWebsocketRegistry, endpoint));
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    private static String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private String executeOnMessageHandler(String path, Map<Object, Object> context) {
        return scriptDispatcher(path).onMessage(path, context);
    }

    private void executeOnOpenHandler(String path, Map<Object, Object> context) {
        scriptDispatcher(path).onOpen(path, context);
    }

    private void executeOnCloseHandler(String path, Map<Object, Object> context) {
        scriptDispatcher(path).onClose(path, context);
    }

    private void executeOnErrorHandler(String path, Map<Object, Object> context) {
        scriptDispatcher(path).onError(path, context);
    }

    private WebsocketScriptDispatcher scriptDispatcher(String handler) {
        WebsocketScriptDispatcher dispatcher = scriptDispatcher.getIfAvailable();
        if (dispatcher == null) {
            throw new ScriptEngineUnavailableException(handler);
        }
        return dispatcher;
    }

}
