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

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * The Class MessageProducer.
 */
@Component
public class MessageProducer {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

    /** The session. */
    private final Session session;

    /**
     * Instantiates a new message producer.
     *
     * @param session the session
     */
    @Autowired
    public MessageProducer(@Qualifier("ActiveMQSession") Session session) {
        this.session = session;
    }

    /**
     * Send message to topic.
     *
     * @param topic the topic
     * @param message the message
     * @throws JMSException the JMS exception
     */
    public void sendMessageToTopic(String topic, String message) throws JMSException {
        Destination destination = session.createTopic(topic);
        sendMessage(message, destination);
    }

    /**
     * Send message to queue.
     *
     * @param queue the queue
     * @param message the message
     * @throws JMSException the JMS exception
     */
    public void sendMessageToQueue(String queue, String message) throws JMSException {
        Destination destination = session.createQueue(queue);
        sendMessage(message, destination);
    }

    /**
     * Send message.
     *
     * @param message the message
     * @param destination the destination
     * @throws JMSException the JMS exception
     */
    private void sendMessage(String message, Destination destination) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        try {
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            TextMessage textMessage = session.createTextMessage(message);

            producer.send(textMessage);
            LOGGER.trace("Message sent in [{}]", destination);
        } finally {
            producer.close();
        }
    }

}
