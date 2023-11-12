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
package org.eclipse.dirigible.components.api.messaging;

import javax.jms.JMSException;
import org.eclipse.dirigible.components.listeners.service.MessageConsumer;
import org.eclipse.dirigible.components.listeners.service.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class MessagingFacade.
 */
@Component
public class MessagingFacade {

    private static MessageConsumer messageConsumer;
    private static MessageProducer messageProducer;

    @Autowired
    public MessagingFacade(MessageConsumer messageConsumer, MessageProducer messageProducer) {
        MessagingFacade.messageConsumer = messageConsumer;
        MessagingFacade.messageProducer = messageProducer;
    }

    /**
     * Send a message to queue.
     *
     * @param queue the queue
     * @param message the message
     * @throws MessagingAPIException if fail to send the message
     */
    public static final void sendToQueue(String queue, String message) throws MessagingAPIException {
        if (null == messageProducer) {
            throw new IllegalStateException("Class is not initialized yet. Cannot call this static method before the bean is initialized");
        }

        try {
            messageProducer.sendMessageToQueue(queue, message);
        } catch (RuntimeException | JMSException ex) {
            throw new MessagingAPIException("Failed to send message to queue [" + queue + "]", ex);
        }
    }

    /**
     * Send a message to topic.
     *
     * @param topic the topic
     * @param message the message
     * @throws MessagingAPIException if fail to send the message
     */
    public static final void sendToTopic(String topic, String message) {
        if (null == messageProducer) {
            throw new IllegalStateException("Class is not initialized yet. Cannot call this static method before the bean is initialized");
        }

        try {
            messageProducer.sendMessageToTopic(topic, message);
        } catch (RuntimeException | JMSException ex) {
            throw new MessagingAPIException("Failed to send message to topic [" + topic + "]", ex);
        }

    }

    /**
     * Receive a message from queue.
     *
     * @param queue the queue
     * @param timeout the timeout in milliseconds
     * @return the message as JSON
     * @throws MessagingAPIException if fail to receive a message from the queue
     * @throws TimeoutException if timeout to get a message from the queue
     */
    public static final String receiveFromQueue(String queue, long timeout) throws MessagingAPIException {
        if (null == messageConsumer) {
            throw new IllegalStateException("Class is not initialized yet. Cannot call this static method before the bean is initialized");
        }

        try {
            return messageConsumer.receiveMessageFromQueue(queue, timeout);
        } catch (org.eclipse.dirigible.components.listeners.service.TimeoutException ex) {
            throw new TimeoutException("Timeout to get a message from queue [" + queue + "]", ex);
        } catch (RuntimeException | JMSException ex) {
            throw new MessagingAPIException("Failed to receive message from queue [" + queue + "]", ex);
        }
    }

    /**
     * Receive a message from topic.
     *
     * @param topic the topic
     * @param timeout the timeout in milliseconds
     * @return the the message as JSON
     * @throws MessagingAPIException if fail to receive a message from the topic
     * @throws TimeoutException if timeout to get a message from the topic
     */
    public static final String receiveFromTopic(String topic, long timeout) {
        if (null == messageConsumer) {
            throw new IllegalStateException("Class is not initialized yet. Cannot call this static method before the bean is initialized");
        }

        try {
            return messageConsumer.receiveMessageFromTopic(topic, timeout);
        } catch (org.eclipse.dirigible.components.listeners.service.TimeoutException ex) {
            throw new TimeoutException("Timeout to get a message from topic [" + topic + "]", ex);
        } catch (RuntimeException | JMSException ex) {
            throw new MessagingAPIException("Failed to receive message from topic [" + topic + "]", ex);
        }
    }

}
