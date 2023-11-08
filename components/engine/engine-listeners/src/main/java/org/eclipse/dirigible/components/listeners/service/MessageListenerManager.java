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

public class MessageListenerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListenerManager.class);

    private final Listener listener;
    private final JavascriptService javascriptService;

    MessageListenerManager(Listener listener, JavascriptService javascriptService) {
        this.listener = listener;
        this.javascriptService = javascriptService;
    }

    public void startListener() {
        LOGGER.info("Starting a message listener for {} ...", listener.getName());
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ListenersManager.CONNECTOR_URL_ATTACH);
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            MessageConsumerExceptionListener exceptionListener =
                    new MessageConsumerExceptionListener(listener.getHandler(), javascriptService);
            connection.setExceptionListener(exceptionListener);

            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    MessageConsumer consumer = session.createConsumer(createDestination(session))) {
                MessageListener messageListener = new MessageListener(listener);
                consumer.setMessageListener(messageListener);
            }
        } catch (RuntimeException | JMSException ex) {
            LOGGER.error("Failed to consume messages for queue/topic [{}], type [{}], listener name [{}]", listener.getName(),
                    listener.getType(), listener.getName(), ex);
        }
    }

    private Destination createDestination(Session session) throws JMSException, IllegalArgumentException {
        return switch (listener.getKind()) {
            case 'Q' -> session.createQueue(listener.getName());
            case 'T' -> session.createTopic(listener.getName());
            default -> throw new IllegalArgumentException("Invalid listener type: " + listener.getKind());
        };
    }

    public void stop() {
        // TODO implement stop
    }

}
