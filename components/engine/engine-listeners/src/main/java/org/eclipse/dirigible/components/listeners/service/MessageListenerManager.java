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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListenerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListenerManager.class);

    private final Listener listener;
    private final Session session;

    private MessageConsumer consumer;

    MessageListenerManager(Listener listener, Session session) {
        this.listener = listener;
        this.session = session;
    }

    public synchronized void startListener() {
        if (null == consumer) {
            LOGGER.debug("Listener [{}] IS already configured", listener);
            return;
        }

        LOGGER.info("Starting a message listener for {} ...", listener);
        try {
            Destination destination = craeteDestination();
            consumer = session.createConsumer(destination);

            MessageListener messageListener = new MessageListener(listener);
            consumer.setMessageListener(messageListener);

        } catch (RuntimeException | JMSException ex) {
            LOGGER.error("Failed to start listener for [{}]", listener, ex);
        }
    }

    private Destination craeteDestination() throws JMSException {
        String destination = listener.getName();
        return switch (listener.getKind()) {
            case 'Q' -> session.createQueue(destination);
            case 'T' -> session.createTopic(destination);
            default -> throw new IllegalArgumentException("Invalid kind: " + listener.getKind());
        };
    }

    public synchronized void stopListener() {
        if (null == consumer) {
            LOGGER.debug("Listener [{}] is NOT started", listener);
        }

        try {
            consumer.close();
            consumer = null;
        } catch (JMSException ex) {
            LOGGER.warn("Failed to close " + consumer, ex);
        }
    }
}
