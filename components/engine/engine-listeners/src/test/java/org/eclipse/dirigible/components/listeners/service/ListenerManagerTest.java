/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.service;

import jakarta.jms.MessageConsumer;
import jakarta.jms.*;
import org.eclipse.dirigible.components.listeners.config.ActiveMQConnectionArtifactsFactory;
import org.eclipse.dirigible.components.listeners.config.ListenerConnectionMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.IllegalStateException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * The Class BackgroundListenerManagerTest.
 */
@SuppressWarnings("resource")
@ExtendWith(MockitoExtension.class)
class ListenerManagerTest {

    /** The Constant TOPIC. */
    private static final String TOPIC = "test-topic";

    /** The Constant QUEUE. */
    private static final String QUEUE = "test-queue";

    /** The manager, in the per-listener mode unless a test builds the shared one. */
    private ListenerManager manager;

    /** The connection the shared mode rides. */
    @Mock
    private SharedListenerConnection sharedConnection;

    /** The listener. */
    @Mock
    private ListenerDescriptor listenerDescriptor;

    /** The connection artifacts factory. */
    @Mock
    private ActiveMQConnectionArtifactsFactory connectionArtifactsFactory;

    /** The asynchronous message listener factory. */
    @Mock
    private AsynchronousMessageListenerFactory asynchronousMessageListenerFactory;

    /** The connection. */
    @Mock
    private Connection connection;

    /** The session. */
    @Mock
    private Session session;

    /** The topic. */
    @Mock
    private Topic topic;

    /** The queue. */
    @Mock
    private Queue queue;

    /** The consumer. */
    @Mock
    private MessageConsumer consumer;

    /** The exception listener captor. */
    @Captor
    private ArgumentCaptor<ExceptionListener> exceptionListenerCaptor;

    /** The message listener captor. */
    @Captor
    private ArgumentCaptor<AsynchronousMessageListener> messageListenerCaptor;

    /** The asynchronous message listener. */
    @Mock
    private AsynchronousMessageListener asynchronousMessageListener;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        lenient().when(asynchronousMessageListenerFactory.create(listenerDescriptor))
                 .thenReturn(asynchronousMessageListener);
        manager = new ListenerManager(listenerDescriptor, connectionArtifactsFactory, asynchronousMessageListenerFactory,
                ListenerConnectionMode.PER_LISTENER, sharedConnection);
    }

    private ListenerManager sharedManager() {
        return new ListenerManager(listenerDescriptor, connectionArtifactsFactory, asynchronousMessageListenerFactory,
                ListenerConnectionMode.SHARED, sharedConnection);
    }

    /**
     * In the shared mode (#7875) the listener attaches to the one connection every artefact rides and
     * opens only a session on it - no connection of its own.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void inTheSharedModeTheListenerIsASessionOnTheSharedConnection() throws JMSException {
        when(sharedConnection.attach(any(ExceptionListener.class))).thenReturn(connection);
        when(connectionArtifactsFactory.createSession(connection)).thenReturn(session);
        when(listenerDescriptor.getType()).thenReturn(ListenerType.TOPIC);
        when(listenerDescriptor.getDestination()).thenReturn(TOPIC);
        when(session.createTopic(TOPIC)).thenReturn(topic);
        when(session.createConsumer(topic)).thenReturn(consumer);

        ListenerManager shared = sharedManager();
        shared.startListener();

        verify(connectionArtifactsFactory, never()).createConnection(any(ExceptionListener.class));
        verify(sharedConnection).attach(exceptionListenerCaptor.capture());
        assertThat(exceptionListenerCaptor.getValue()).isInstanceOf(ListenerExceptionHandler.class);
        verify(connectionArtifactsFactory).configureRedeliveryPolicy(connection, topic);
        verifyConfiguredMessageListener();
    }

    /**
     * Stopping closes what the listener owns - its consumer and session - and detaches its handler; the
     * shared connection stays up for the other artefacts.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void inTheSharedModeStoppingLeavesTheConnectionToTheOtherListeners() throws JMSException {
        when(sharedConnection.attach(any(ExceptionListener.class))).thenReturn(connection);
        when(connectionArtifactsFactory.createSession(connection)).thenReturn(session);
        when(listenerDescriptor.getType()).thenReturn(ListenerType.QUEUE);
        when(listenerDescriptor.getDestination()).thenReturn(QUEUE);
        when(session.createQueue(QUEUE)).thenReturn(queue);
        when(session.createConsumer(queue)).thenReturn(consumer);

        ListenerManager shared = sharedManager();
        shared.startListener();
        shared.stopListener();

        verify(consumer).close();
        verify(session).close();
        verify(connection, never()).close();
        verify(sharedConnection).attach(exceptionListenerCaptor.capture());
        verify(sharedConnection).detach(exceptionListenerCaptor.getValue());
    }

    /**
     * A subscription the broker refuses after the attach must give the attach back, or every retried
     * pass would leave one more handler listening on the shared connection.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void inTheSharedModeARefusedSubscriptionDetachesItsHandler() throws JMSException {
        when(sharedConnection.attach(any(ExceptionListener.class))).thenReturn(connection);
        when(connectionArtifactsFactory.createSession(connection)).thenThrow(JMSException.class);

        ListenerManager shared = sharedManager();
        assertThrows(IllegalStateException.class, shared::startListener);

        verify(sharedConnection).attach(exceptionListenerCaptor.capture());
        verify(sharedConnection).detach(exceptionListenerCaptor.getValue());
        verify(connection, never()).close();
    }

    /**
     * In the per-listener mode the same refusal closes the connection the attempt opened (#7264).
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void aRefusedSubscriptionClosesTheConnectionItOpened() throws JMSException {
        when(connectionArtifactsFactory.createConnection(any(ExceptionListener.class))).thenReturn(connection);
        when(connectionArtifactsFactory.createSession(connection)).thenThrow(JMSException.class);

        assertThrows(IllegalStateException.class, () -> manager.startListener());

        verify(connection).close();
    }

    /**
     * Test start listener on start error.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void testStartListenerOnStartError() throws JMSException {
        when(connectionArtifactsFactory.createConnection(any(ExceptionListener.class))).thenReturn(connection);
        when(connectionArtifactsFactory.createSession(connection)).thenThrow(JMSException.class);

        assertThrows(IllegalStateException.class, () -> manager.startListener());
    }

    /**
     * Test start listener for unsupported listener kind.
     */
    @Test
    void testStartListenerForUnsupportedListenerType() {
        when(listenerDescriptor.getType()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> manager.startListener());
    }

    /**
     * Test start listener for queue.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void testStartListenerForQueue() throws JMSException {
        mockConnectionAndSession();
        when(listenerDescriptor.getType()).thenReturn(ListenerType.QUEUE);
        when(listenerDescriptor.getDestination()).thenReturn(QUEUE);

        when(session.createQueue(QUEUE)).thenReturn(queue);
        when(session.createConsumer(queue)).thenReturn(consumer);

        manager.startListener();

        manager.startListener();

        // verify second call to start doesn't create new artifacts
        verifyConfiguredExceptionListener();
        verifyConfiguredMessageListener();
        verify(session).createQueue(QUEUE);
        verify(session).createConsumer(queue);
        verify(connectionArtifactsFactory).createConnection(any(ExceptionListener.class));
        verify(connectionArtifactsFactory).createSession(connection);
    }

    /**
     * Mock connection and session.
     *
     * @throws JMSException the JMS exception
     */
    private void mockConnectionAndSession() throws JMSException {
        when(connectionArtifactsFactory.createConnection(any(ExceptionListener.class))).thenReturn(connection);
        when(connectionArtifactsFactory.createSession(connection)).thenReturn(session);
    }

    /**
     * Verify configured exception listener.
     */
    private void verifyConfiguredExceptionListener() {
        verify(connectionArtifactsFactory).createConnection(exceptionListenerCaptor.capture());
        ExceptionListener actualExceptionListener = exceptionListenerCaptor.getValue();
        assertThat(actualExceptionListener).isInstanceOf(ListenerExceptionHandler.class);
    }

    /**
     * Verify configured message listener.
     *
     * @throws JMSException the JMS exception
     */
    private void verifyConfiguredMessageListener() throws JMSException {
        verify(consumer).setMessageListener(messageListenerCaptor.capture());
        assertThat(messageListenerCaptor.getValue()).isInstanceOf(AsynchronousMessageListener.class);
    }

    /**
     * Test stop.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void testStop() throws JMSException {
        testStartListenerForTopic();

        manager.stopListener();

        manager.stopListener();

        InOrder inOrder = Mockito.inOrder(consumer, session, connection);

        // verify second call to stop doesn't close the artifacts again
        inOrder.verify(consumer)
               .close();

        inOrder.verify(session)
               .close();

        inOrder.verify(connection)
               .close();
    }

    /**
     * Test start listener for topic.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void testStartListenerForTopic() throws JMSException {
        mockConnectionAndSession();
        when(listenerDescriptor.getType()).thenReturn(ListenerType.TOPIC);
        when(listenerDescriptor.getDestination()).thenReturn(TOPIC);

        when(session.createTopic(TOPIC)).thenReturn(topic);
        when(session.createConsumer(topic)).thenReturn(consumer);

        manager.startListener();

        verifyConfiguredExceptionListener();
        verifyConfiguredMessageListener();
    }

}
