/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.service;

import jakarta.jms.MessageConsumer;
import jakarta.jms.*;
import org.eclipse.dirigible.components.listeners.config.ActiveMQConnectionArtifactsFactory;
import org.eclipse.dirigible.components.listeners.config.ListenerConnectionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.IllegalStateException;

/**
 * Starts and stops the subscription of one {@code .listener} artefact. In the
 * {@link ListenerConnectionMode#SHARED shared} mode the subscription is a session on the
 * {@link SharedListenerConnection}; in the {@link ListenerConnectionMode#PER_LISTENER per-listener}
 * mode it opens, and owns, a connection of its own (#7875).
 */
public class ListenerManager {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerManager.class);

    /** The listener. */
    private final ListenerDescriptor listenerDescriptor;

    /** The connection artifacts factory. */
    private final ActiveMQConnectionArtifactsFactory connectionArtifactsFactory;

    /** The asynchronous message listener factory. */
    private final AsynchronousMessageListenerFactory asynchronousMessageListenerFactory;

    /** The connection mode. */
    private final ListenerConnectionMode connectionMode;

    /** The connection shared by every artefact in the shared mode. */
    private final SharedListenerConnection sharedConnection;

    /** The connection artifacts. */
    private ConnectionArtifacts connectionArtifacts;

    /** The handler attached to the shared connection while this listener runs on it. */
    private ListenerExceptionHandler attachedExceptionListener;

    /**
     * Instantiates a new background listener manager.
     *
     * @param listenerDescriptor the listener
     * @param connectionArtifactsFactory the connection artifacts factory
     * @param asynchronousMessageListenerFactory the asynchronous message listener factory
     * @param connectionMode how the subscription attaches to the broker
     * @param sharedConnection the connection shared by every artefact in the shared mode
     */
    public ListenerManager(ListenerDescriptor listenerDescriptor, ActiveMQConnectionArtifactsFactory connectionArtifactsFactory,
            AsynchronousMessageListenerFactory asynchronousMessageListenerFactory, ListenerConnectionMode connectionMode,
            SharedListenerConnection sharedConnection) {
        this.listenerDescriptor = listenerDescriptor;
        this.connectionArtifactsFactory = connectionArtifactsFactory;
        this.asynchronousMessageListenerFactory = asynchronousMessageListenerFactory;
        this.connectionMode = connectionMode;
        this.sharedConnection = sharedConnection;
    }

    /**
     * Start listener.
     */
    @SuppressWarnings("resource")
    public synchronized void startListener() {
        if (null != connectionArtifacts) {
            LOGGER.debug("Listener [{}] IS already configured", listenerDescriptor);
            return;
        }

        LOGGER.info("Starting a message listener for {} ...", listenerDescriptor);
        String handlerPath = listenerDescriptor.getHandlerPath();
        ListenerExceptionHandler exceptionListener = new ListenerExceptionHandler(handlerPath);
        boolean shared = connectionMode == ListenerConnectionMode.SHARED;
        // Held outside the try: a subscription refused after the connection was handed out must give
        // the connection back - close an owned one, detach from the shared one - or it leaks per pass.
        Connection connection = null;
        try {
            connection =
                    shared ? sharedConnection.attach(exceptionListener) : connectionArtifactsFactory.createConnection(exceptionListener);
            Session session = connectionArtifactsFactory.createSession(connection);

            Destination destination = createDestination(session);
            connectionArtifactsFactory.configureRedeliveryPolicy(connection, destination);

            MessageConsumer consumer = session.createConsumer(destination);

            AsynchronousMessageListener messageListener = asynchronousMessageListenerFactory.create(listenerDescriptor);
            consumer.setMessageListener(messageListener);

            connectionArtifacts = new ConnectionArtifacts(shared ? null : connection, session, consumer);
            attachedExceptionListener = shared ? exceptionListener : null;
        } catch (JMSException | RuntimeException ex) {
            releaseFailedAttempt(connection, exceptionListener, shared);
            throw ex instanceof RuntimeException runtime ? runtime
                    : new IllegalStateException("Failed to start listener for " + listenerDescriptor, ex);
        }
    }

    /**
     * Give back what a refused start took: the owned connection is closed, the shared one is left
     * running for the other listeners and only this handler is detached from it.
     */
    private void releaseFailedAttempt(Connection connection, ListenerExceptionHandler exceptionListener, boolean shared) {
        if (shared) {
            sharedConnection.detach(exceptionListener);
            return;
        }
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (JMSException | RuntimeException e) {
            LOGGER.debug("Failed to close the connection of the refused listener {}: {}", listenerDescriptor, e.getMessage(), e);
        }
    }

    /**
     * Create destination.
     *
     * @param session the session
     * @return the destination
     * @throws JMSException the JMS exception
     */
    private Destination createDestination(Session session) throws JMSException {
        String destination = listenerDescriptor.getDestination();
        ListenerType type = listenerDescriptor.getType();
        if (null == type) {
            throw new IllegalArgumentException("Type cannot be null for: " + listenerDescriptor);
        }
        return switch (type) {
            case QUEUE -> session.createQueue(destination);
            case TOPIC -> session.createTopic(destination);
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };
    }

    /**
     * Stop listener.
     */
    public synchronized void stopListener() {
        if (null == connectionArtifacts) {
            LOGGER.debug("Listener [{}] is NOT started", listenerDescriptor);
            return;
        }
        LOGGER.info("Stopping message listener for {} ...", listenerDescriptor);
        connectionArtifacts.closeAll();
        connectionArtifacts = null;
        if (attachedExceptionListener != null) {
            sharedConnection.detach(attachedExceptionListener);
            attachedExceptionListener = null;
        }
        LOGGER.info("Stopped message listener for {}", listenerDescriptor);
    }
}
