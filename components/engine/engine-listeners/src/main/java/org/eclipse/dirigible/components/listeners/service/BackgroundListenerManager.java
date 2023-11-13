/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.service;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.eclipse.dirigible.components.listeners.config.ActiveMQConnectionArtifactsFactory;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.components.listeners.domain.ListenerKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BackgroundListenerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundListenerManager.class);

    private final Listener listener;
    private final ActiveMQConnectionArtifactsFactory connectionArtifactsFactory;
    private ConnectionArtifacts connectionArtifacts;

    BackgroundListenerManager(Listener listener, ActiveMQConnectionArtifactsFactory connectionArtifactsFactory) {
        this.listener = listener;
        this.connectionArtifactsFactory = connectionArtifactsFactory;
    }

    @SuppressWarnings("resource")
    synchronized void startListener() {
        if (null != connectionArtifacts) {
            LOGGER.debug("Listener [{}] IS already configured", listener);
            return;
        }

        LOGGER.info("Starting a message listener for {} ...", listener);
        try {
            String handlerPath = listener.getHandler();
            ExceptionListener exceptionListener = new BackgroundExceptionListener(handlerPath);

            Connection connection = connectionArtifactsFactory.createConnection(exceptionListener);
            Session session = connectionArtifactsFactory.createSession(connection);

            Destination destination = craeteDestination(session);
            MessageConsumer consumer = session.createConsumer(destination);

            BackgroundMessageListener messageListener = new BackgroundMessageListener(listener);
            consumer.setMessageListener(messageListener);

            connectionArtifacts = new ConnectionArtifacts(connection, session, consumer);
        } catch (JMSException ex) {
            throw new IllegalStateException("Failed to start listener for " + listener, ex);
        }
    }

    private Destination craeteDestination(Session session) throws JMSException {
        String destination = listener.getName();
        ListenerKind kind = listener.getKind();
        return switch (kind) {
            case QUEUE -> session.createQueue(destination);
            case TOPIC -> session.createTopic(destination);
            default -> throw new IllegalArgumentException("Invalid kind: " + kind);
        };
    }

    synchronized void stopListener() {
        if (null == connectionArtifacts) {
            LOGGER.debug("Listener [{}] is NOT started", listener);
            return;
        }
        LOGGER.info("Stopping message listener for {} ...", listener);
        connectionArtifacts.closeAll();
        connectionArtifacts = null;
        LOGGER.info("Stopped message listener for {}", listener);
    }
}
