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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;
import jakarta.jms.BytesMessage;
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
 * The Class MessageConsumerTest.
 */
@SuppressWarnings("resource")
@ExtendWith(MockitoExtension.class)
class MessageConsumerTest {

    /** The Constant QUEUE. */
    private static final String QUEUE = "test-queue";

    /** The Constant TIMEOUT. */
    private static final long TIMEOUT = 100L;

    /** The Constant MESSAGE. */
    private static final String MESSAGE = "This is a test message";

    /** The Constant TOPIC. */
    private static final String TOPIC = "test-topic";

    /** The consumer. */
    @InjectMocks
    private MessageConsumer consumer;

    /** The session. */
    @Mock
    private Session session;

    /** The jsm consumer. */
    @Mock
    private jakarta.jms.MessageConsumer jsmConsumer;

    /** The queue. */
    @Mock
    private Queue queue;

    /** The topic. */
    @Mock
    private Topic topic;

    /** The txt message. */
    @Mock
    private TextMessage txtMessage;

    /** The byte message. */
    @Mock
    private BytesMessage byteMessage;

    /**
     * Test receive message from queue.
     *
     * @throws TimeoutException the timeout exception
     * @throws JMSException the JMS exception
     */
    @Test
    void testReceiveMessageFromQueue() throws TimeoutException, JMSException {
        when(session.createQueue(QUEUE)).thenReturn(queue);
        when(session.createConsumer(queue)).thenReturn(jsmConsumer);
        when(jsmConsumer.receive(TIMEOUT)).thenReturn(txtMessage);
        when(txtMessage.getText()).thenReturn(MESSAGE);

        String actualMessage = consumer.receiveMessageFromQueue(QUEUE, TIMEOUT);

        assertThat(actualMessage).isEqualTo(MESSAGE);
    }

    /**
     * Test receive message from queue on timeout.
     *
     * @throws TimeoutException the timeout exception
     * @throws JMSException the JMS exception
     */
    @Test
    void testReceiveMessageFromQueueOnTimeout() throws TimeoutException, JMSException {
        when(session.createQueue(QUEUE)).thenReturn(queue);
        when(session.createConsumer(queue)).thenReturn(jsmConsumer);
        when(jsmConsumer.receive(TIMEOUT)).thenReturn(null);

        assertThrows(TimeoutException.class, ()->  consumer.receiveMessageFromQueue(QUEUE, TIMEOUT));
    }

    /**
     * Test receive message from queue on unsupported message is received.
     *
     * @throws TimeoutException the timeout exception
     * @throws JMSException the JMS exception
     */
    @Test
    void testReceiveMessageFromQueueOnUnsupportedMessageIsReceived() throws TimeoutException, JMSException {
        when(session.createQueue(QUEUE)).thenReturn(queue);
        when(session.createConsumer(queue)).thenReturn(jsmConsumer);
        when(jsmConsumer.receive(TIMEOUT)).thenReturn(byteMessage);

        assertThrows(IllegalStateException.class, ()->  consumer.receiveMessageFromQueue(QUEUE, TIMEOUT));
    }

    /**
     * Test receive message from topic.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void testReceiveMessageFromTopic() throws JMSException {
        when(session.createTopic(TOPIC)).thenReturn(topic);
        when(session.createConsumer(topic)).thenReturn(jsmConsumer);
        when(jsmConsumer.receive(TIMEOUT)).thenReturn(txtMessage);
        when(txtMessage.getText()).thenReturn(MESSAGE);

        String actualMessage = consumer.receiveMessageFromTopic(TOPIC, TIMEOUT);

        assertThat(actualMessage).isEqualTo(MESSAGE);
    }

    /**
     * Test receive message from topic on timeout.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void testReceiveMessageFromTopicOnTimeout() throws JMSException {
        when(session.createTopic(TOPIC)).thenReturn(topic);
        when(session.createConsumer(topic)).thenReturn(jsmConsumer);
        when(jsmConsumer.receive(TIMEOUT)).thenReturn(null);

        assertThrows(TimeoutException.class, ()->  consumer.receiveMessageFromTopic(TOPIC, TIMEOUT));
    }

}
