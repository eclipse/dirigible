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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.activemq.ActiveMQConnection;
import org.eclipse.dirigible.components.listeners.config.ActiveMQConnectionArtifactsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.jms.Connection;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;

/**
 * The one broker connection every {@code .listener} artefact's subscription rides in the
 * {@link org.eclipse.dirigible.components.listeners.config.ListenerConnectionMode#SHARED shared}
 * mode (#7875). Opened on first use, so an instance without a single artefact never connects.
 *
 * <p>
 * A connection carries one exception listener, and every artefact has its own {@code onError}
 * handler, so this one fans a broker error out to every handler attached at the time - each
 * listener learns what it would have learnt from a connection of its own. The connection is
 * anonymous (an artefact's subscription is not durable), so a {@code failover:} transport
 * reconnects it by itself; one that is dead for good is replaced by the next attach, which is how a
 * listener started after an outage gets a live connection.
 */
@Component
class SharedListenerConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(SharedListenerConnection.class);

    private final ActiveMQConnectionArtifactsFactory connectionArtifactsFactory;

    /** The handlers of the listeners attached right now; identity-keyed, one per listener. */
    private final Set<ExceptionListener> exceptionListeners = ConcurrentHashMap.newKeySet();

    /** The live connection, or {@code null} before the first attach and after a close. */
    private Connection connection;

    SharedListenerConnection(ActiveMQConnectionArtifactsFactory connectionArtifactsFactory) {
        this.connectionArtifactsFactory = connectionArtifactsFactory;
    }

    /**
     * Attach a listener: its handler learns of the connection's errors from now on, and the returned
     * connection is the one to open its session on.
     *
     * @param exceptionListener the listener's own error handler
     * @return the shared connection
     * @throws IllegalStateException if the broker refuses the connection
     */
    synchronized Connection attach(ExceptionListener exceptionListener) {
        Connection live = liveConnection();
        exceptionListeners.add(exceptionListener);
        return live;
    }

    /**
     * Detach a listener that stopped: its handler is no longer told about the connection's errors.
     *
     * @param exceptionListener the handler given to {@link #attach}
     */
    synchronized void detach(ExceptionListener exceptionListener) {
        exceptionListeners.remove(exceptionListener);
    }

    /** Close the connection, once every listener is stopped. */
    synchronized void close() {
        exceptionListeners.clear();
        closeQuietly();
    }

    private Connection liveConnection() {
        if (connection != null && isDead(connection)) {
            LOGGER.warn("The shared connection of the .listener artefacts is gone - opening a new one.");
            closeQuietly();
        }
        if (connection == null) {
            connection = connectionArtifactsFactory.createConnection(this::fanOut);
            LOGGER.info("Opened the shared broker connection of the .listener artefacts.");
        }
        return connection;
    }

    private void fanOut(JMSException exception) {
        for (ExceptionListener exceptionListener : exceptionListeners) {
            exceptionListener.onException(exception);
        }
    }

    private static boolean isDead(Connection connection) {
        return connection instanceof ActiveMQConnection activeMQConnection
                && (activeMQConnection.isClosed() || activeMQConnection.isTransportFailed());
    }

    private void closeQuietly() {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (JMSException | RuntimeException e) {
            LOGGER.debug("Failed to close the shared connection of the .listener artefacts: {}", e.getMessage(), e);
        } finally {
            connection = null;
        }
    }
}
