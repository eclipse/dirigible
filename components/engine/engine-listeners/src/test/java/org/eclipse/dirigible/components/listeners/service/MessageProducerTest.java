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
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("resource")
@ExtendWith(MockitoExtension.class)
class MessageProducerTest {

    private static final String QUEUE = "test-queue";
    private static final String MESSAGE = "This is a test message";
    private static final String TOPIC = "test-topic";

    @InjectMocks
    private MessageProducer producer;

    @Mock
    private Session session;

    @Mock
    private javax.jms.MessageProducer jsmProducer;

    @Mock
    private Queue queue;

    @Mock
    private Topic topic;

    @Mock
    private TextMessage txtMessage;

    @Test
    void testSendMessageToTopic() throws JMSException {
        when(session.createTopic(TOPIC)).thenReturn(topic);
        when(session.createProducer(topic)).thenReturn(jsmProducer);
        when(session.createTextMessage(MESSAGE)).thenReturn(txtMessage);

        producer.sendMessageToTopic(TOPIC, MESSAGE);

        verify(jsmProducer).send(txtMessage);
    }

    @Test
    void testSendMessageToQueue() throws JMSException {
        when(session.createQueue(QUEUE)).thenReturn(queue);
        when(session.createProducer(queue)).thenReturn(jsmProducer);
        when(session.createTextMessage(MESSAGE)).thenReturn(txtMessage);

        producer.sendMessageToQueue(QUEUE, MESSAGE);

        verify(jsmProducer).send(txtMessage);
    }

}
