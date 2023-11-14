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
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    private final Session session;

    @Autowired
    MessageConsumer(@Qualifier("ActiveMQSession") Session session) {
        this.session = session;
    }

    public String receiveMessageFromQueue(String queue, long timeout) throws JMSException, TimeoutException {
        return receiveMessage(timeout, session.createQueue(queue));
    }

    public String receiveMessageFromTopic(String topic, long timeout) throws JMSException, TimeoutException {
        return receiveMessage(timeout, session.createTopic(topic));
    }

    private String receiveMessage(long timeout, Destination destination) throws JMSException, TimeoutException {
        try (javax.jms.MessageConsumer consumer = session.createConsumer(destination)) {

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
