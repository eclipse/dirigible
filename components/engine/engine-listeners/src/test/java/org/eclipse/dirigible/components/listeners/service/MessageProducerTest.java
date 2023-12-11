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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class MessageProducerTest.
 */
@SuppressWarnings("resource")
@ExtendWith(MockitoExtension.class)
class MessageProducerTest {

    /** The Constant QUEUE. */
    private static final String QUEUE = "test-queue";

    /** The Constant MESSAGE. */
    private static final String MESSAGE = "This is a test message";

    /** The Constant TOPIC. */
    private static final String TOPIC = "test-topic";

    /** The producer. */
    @InjectMocks
    private MessageProducer producer;

    /** The session. */
    @Mock
    private Session session;

    /** The jsm producer. */
    @Mock
    private javax.jms.MessageProducer jsmProducer;

    /** The queue. */
    @Mock
    private Queue queue;

    /** The topic. */
    @Mock
    private Topic topic;

    /** The txt message. */
    @Mock
    private TextMessage txtMessage;

    /**
     * Test send message to topic.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void testSendMessageToTopic() throws JMSException {
        when(session.createTopic(TOPIC)).thenReturn(topic);
        when(session.createProducer(topic)).thenReturn(jsmProducer);
        when(session.createTextMessage(MESSAGE)).thenReturn(txtMessage);

        producer.sendMessageToTopic(TOPIC, MESSAGE);

        verify(jsmProducer).send(txtMessage);
    }

    /**
     * Test send message to queue.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void testSendMessageToQueue() throws JMSException {
        when(session.createQueue(QUEUE)).thenReturn(queue);
        when(session.createProducer(queue)).thenReturn(jsmProducer);
        when(session.createTextMessage(MESSAGE)).thenReturn(txtMessage);

        producer.sendMessageToQueue(QUEUE, MESSAGE);

        verify(jsmProducer).send(txtMessage);
    }

}
