/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.transport.failover.FailoverTransport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.jms.Connection;

/**
 * Over a {@code failover:} URL, a connection that carries a client id is not reconnected by the
 * transport (#7851): ActiveMQ's {@code FailoverTransport} replayed the client id while the broker
 * still held the previous socket under it, was refused, and reconnected again - a loop that kept an
 * external broker busy for as long as it lasted. Its loss reaches the exception listener instead,
 * so the caller can re-establish it at its own pace. An anonymous connection keeps the transport's
 * reconnect.
 *
 * <p>
 * Against a real embedded broker reached through {@code failover:(vm://...)}, so the transport
 * under test is the one an external {@code failover:} URL produces.
 */
class ActiveMQConnectionArtifactsFactoryFailoverTest {

    private static final String BROKER_NAME = "failover-reconnect-test";

    private BrokerService broker;
    private ActiveMQConnectionArtifactsFactory factory;
    private Connection connection;

    @BeforeEach
    void startBroker() throws Exception {
        broker = new BrokerService();
        broker.setBrokerName(BROKER_NAME);
        broker.setPersistent(false);
        broker.setUseJmx(false);
        broker.start();
        broker.waitUntilStarted();
        factory = new ActiveMQConnectionArtifactsFactory(
                new ActiveMQConnectionFactory("failover:(vm://" + BROKER_NAME + "?create=false)?initialReconnectDelay=50"));
    }

    @AfterEach
    void stopBroker() throws Exception {
        if (connection != null) {
            connection.close();
        }
        if (broker.isStarted()) {
            broker.stop();
            broker.waitUntilStopped();
        }
    }

    @Test
    void aClientIdConnectionIsNotReconnectedByTheTransport() {
        connection = factory.createConnection(exception -> {
        }, "durable-subscriber");

        assertThat(failoverOf(connection).getMaxReconnectAttempts()).isZero();
    }

    @Test
    void anAnonymousConnectionKeepsTheTransportReconnect() {
        connection = factory.createConnection(exception -> {
        });

        assertThat(failoverOf(connection).getMaxReconnectAttempts()).isEqualTo(-1);
    }

    @Test
    void theLossOfAClientIdConnectionReachesItsExceptionListener() throws Exception {
        CountDownLatch lost = new CountDownLatch(1);
        connection = factory.createConnection(exception -> lost.countDown(), "durable-subscriber");

        broker.stop();
        broker.waitUntilStopped();

        assertThat(lost.await(10, TimeUnit.SECONDS)).as("the exception listener learned that the connection is gone")
                                                    .isTrue();
    }

    private static FailoverTransport failoverOf(Connection connection) {
        FailoverTransport failover = ((ActiveMQConnection) connection).getTransport()
                                                                      .narrow(FailoverTransport.class);
        assertThat(failover).as("a failover: URL produces a FailoverTransport")
                            .isNotNull();
        return failover;
    }
}
