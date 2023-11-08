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
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("resource")
public class MessageListenerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListenerManager.class);

    private final Listener listener;
    private final JavascriptService javascriptService;

    private ActiveMQConnectionArtifacts connectionArtifacts;

    MessageListenerManager(Listener listener, JavascriptService javascriptService) {
        this.listener = listener;
        this.javascriptService = javascriptService;
    }

    public void startListener() {
        if (null == connectionArtifacts) {
            LOGGER.debug("Listener [{}] IS already configured", listener);
            return;
        }

        LOGGER.info("Starting a message listener for {} ...", listener.getName());
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ListenersManager.CONNECTOR_URL_ATTACH);

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            MessageConsumerExceptionListener exceptionListener =
                    new MessageConsumerExceptionListener(listener.getHandler(), javascriptService);
            connection.setExceptionListener(exceptionListener);

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(createDestination(connection));

            MessageListener messageListener = new MessageListener(listener);
            consumer.setMessageListener(messageListener);

            connectionArtifacts = new ActiveMQConnectionArtifacts(connection, session, consumer);
        } catch (RuntimeException | JMSException ex) {
            LOGGER.error("Failed to start listener for [{}]", listener, ex);
            new ActiveMQConnectionArtifacts(connection, session, consumer).close();
        }
    }

    private Destination createDestination(Connection connection) throws JMSException, IllegalArgumentException {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        return switch (listener.getKind()) {
            case 'Q' -> session.createQueue(listener.getName());
            case 'T' -> session.createTopic(listener.getName());
            default -> throw new IllegalArgumentException("Invalid listener type: " + listener.getKind());
        };
    }

    public void stop() {
        if (null != connectionArtifacts) {
            connectionArtifacts.close();
        } else {
            LOGGER.debug("Listener [{}] is NOT started", listener);
        }
    }
}
