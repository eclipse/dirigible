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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.activemq.ActiveMQConnection;
import org.eclipse.dirigible.components.listeners.config.ActiveMQConnectionArtifactsFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.jms.Connection;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;

/**
 * The one connection the {@code .listener} artefacts share (#7875): opened on the first attach,
 * reused by every later one, its errors fanned out to every attached handler, and replaced once it
 * is dead.
 */
class SharedListenerConnectionTest {

    private ActiveMQConnectionArtifactsFactory factory;
    private Connection connection;
    private SharedListenerConnection shared;

    @BeforeEach
    void setUp() {
        factory = mock(ActiveMQConnectionArtifactsFactory.class);
        connection = mock(Connection.class);
        when(factory.createConnection(any())).thenReturn(connection);
        shared = new SharedListenerConnection(factory);
    }

    @Test
    void everyListenerAttachesToTheSameConnection() {
        Connection first = shared.attach(mock(ExceptionListener.class));
        Connection second = shared.attach(mock(ExceptionListener.class));

        assertThat(first).isSameAs(connection);
        assertThat(second).isSameAs(connection);
        verify(factory, times(1)).createConnection(any());
    }

    @Test
    void anErrorReachesEveryAttachedHandlerAndNoDetachedOne() {
        ExceptionListener kept = mock(ExceptionListener.class);
        ExceptionListener detached = mock(ExceptionListener.class);
        shared.attach(kept);
        shared.attach(detached);
        shared.detach(detached);

        JMSException error = new JMSException("broker gone");
        fanOut().onException(error);

        verify(kept).onException(error);
        verify(detached, never()).onException(any());
    }

    @Test
    void aDeadConnectionIsReplacedByTheNextAttach() throws JMSException {
        ActiveMQConnection dead = mock(ActiveMQConnection.class);
        when(dead.isTransportFailed()).thenReturn(true);
        Connection replacement = mock(Connection.class);
        when(factory.createConnection(any())).thenReturn(dead, replacement);

        shared.attach(mock(ExceptionListener.class));
        Connection attached = shared.attach(mock(ExceptionListener.class));

        assertThat(attached).isSameAs(replacement);
        verify(dead).close();
    }

    @Test
    void closeClosesTheConnectionAndTheNextAttachOpensANewOne() throws JMSException {
        shared.attach(mock(ExceptionListener.class));

        shared.close();
        shared.attach(mock(ExceptionListener.class));

        verify(connection).close();
        verify(factory, times(2)).createConnection(any());
    }

    @Test
    void closingBeforeTheFirstAttachConnectsNothing() {
        shared.close();

        verify(factory, never()).createConnection(any());
    }

    private ExceptionListener fanOut() {
        ArgumentCaptor<ExceptionListener> captor = ArgumentCaptor.forClass(ExceptionListener.class);
        verify(factory).createConnection(captor.capture());
        return captor.getValue();
    }
}
