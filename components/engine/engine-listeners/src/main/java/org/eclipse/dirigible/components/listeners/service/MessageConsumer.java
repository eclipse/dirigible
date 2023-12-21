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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

/**
 * The Class MessageConsumer.
 */
@Component
public class MessageConsumer {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    /** The session. */
    private final Session session;

    /**
     * Instantiates a new message consumer.
     *
     * @param session the session
     */
    @Autowired
    public MessageConsumer(@Qualifier("ActiveMQSession") Session session) {
        this.session = session;
    }

    /**
     * Receive message from queue.
     *
     * @param queue the queue
     * @param timeout the timeout
     * @return the string
     * @throws JMSException the JMS exception
     * @throws TimeoutException the timeout exception
     */
    public String receiveMessageFromQueue(String queue, long timeout) throws JMSException, TimeoutException {
        return receiveMessage(timeout, session.createQueue(queue));
    }

    /**
     * Receive message from topic.
     *
     * @param topic the topic
     * @param timeout the timeout
     * @return the string
     * @throws JMSException the JMS exception
     * @throws TimeoutException the timeout exception
     */
    public String receiveMessageFromTopic(String topic, long timeout) throws JMSException, TimeoutException {
        return receiveMessage(timeout, session.createTopic(topic));
    }

    /**
     * Receive message.
     *
     * @param timeout the timeout
     * @param destination the destination
     * @return the string
     * @throws JMSException the JMS exception
     * @throws TimeoutException the timeout exception
     */
    private String receiveMessage(long timeout, Destination destination) throws JMSException, TimeoutException {
        try (jakarta.jms.MessageConsumer consumer = session.createConsumer(destination)) {

            Message message = consumer.receive(timeout);
            LOGGER.debug("Received message [{}] by synchronous consumer.", message);
            if (null == message) {
                throw new TimeoutException("Timeout to get a message");
            }
            if (message instanceof TextMessage textMessage) {
                return textMessage.getText();
            }
            throw new IllegalStateException("Received an unsupported message " + message);
        }
    }

}
