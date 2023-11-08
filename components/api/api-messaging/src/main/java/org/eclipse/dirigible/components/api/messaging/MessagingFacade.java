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
import org.eclipse.dirigible.components.listeners.service.MessageReceiver;
import org.eclipse.dirigible.components.listeners.service.MessagingProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class MessagingFacade.
 */
@Component
public class MessagingFacade {

    private static MessageReceiver messageReceiver;

    @Autowired
    public MessagingFacade(MessageReceiver messageReceiver) {
        MessagingFacade.messageReceiver = messageReceiver;
    }

    /**
     * Send a message to queue.
     *
     * @param destination the destination
     * @param message the message
     */
    public static final void sendToQueue(String destination, String message) {
        MessagingProducer producer = new MessagingProducer(destination, 'Q', message);
        new Thread(producer).start();
    }

    /**
     * Send a message to topic.
     *
     * @param destination the destination
     * @param message the message
     */
    public static final void sendToTopic(String destination, String message) {
        MessagingProducer producer = new MessagingProducer(destination, 'T', message);
        new Thread(producer).start();
    }

    /**
     * Receive a message from queue.
     *
     * @param destination the destination
     * @param timeout the timeout
     * @return the message as JSON
     * @throws MessagingAPIException if fail to receive a message from the queue * @throws
     *         TimeoutException if timeout to get a message from the queue
     */
    public static final String receiveFromQueue(String queue, int timeout) throws MessagingAPIException {
        try {
            return messageReceiver.receiveMessageFromQueue(queue, timeout);
        } catch (org.eclipse.dirigible.components.listeners.service.TimeoutException ex) {
            throw new TimeoutException("Timeout to get a message from queue [" + queue + "]", ex);
        } catch (RuntimeException | JMSException ex) {
            throw new MessagingAPIException("Failed to receive message from queue [" + queue + "]", ex);
        }
    }

    /**
     * Receive a message from topic.
     *
     * @param destination the destination
     * @param timeout the timeout
     * @return the the message as JSON
     * @throws MessagingAPIException if fail to receive a message from the topic
     * @throws TimeoutException if timeout to get a message from the topic
     */
    public static final String receiveFromTopic(String topic, int timeout) {
        try {
            return messageReceiver.receiveMessageFromTopic(topic, timeout);
        } catch (org.eclipse.dirigible.components.listeners.service.TimeoutException ex) {
            throw new TimeoutException("Timeout to get a message from topic [" + topic + "]", ex);
        } catch (RuntimeException | JMSException ex) {
            throw new MessagingAPIException("Failed to receive message from topic [" + topic + "]", ex);
        }
    }

}
