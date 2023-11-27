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
package org.eclipse.dirigible.integration.tests.api.java.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.eclipse.dirigible.components.api.messaging.MessagingFacade;
import org.eclipse.dirigible.integration.tests.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.testcontainers.shaded.org.awaitility.Awaitility;

/**
 * the test methods use the listener and handler which are defined in
 * META-INF/dirigible/integration-tests-project
 */
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class BackgroundListenersIT extends IntegrationTest {

    private static final String STOPPED_ACTIVEMQ_ERROR_MESSAGE_PATTERN = "peer \\(vm:\\/\\/localhost#\\d+\\) stopped\\.";

    @Autowired
    private BrokerService broker;

    @BeforeEach
    void setUp() {
        MessagesHolder.clearLatestReceivedMessage();
        MessagesHolder.clearLatestReceivedError();
    }

    @Nested
    class QueueListenerTest {
        private static final String QUEUE_NAME = "integration-tests-queue";

        @Test
        void testOnMessageIsCalled() {
            String testMessage = getCallerMethod();

            MessagingFacade.sendToQueue(QUEUE_NAME, testMessage);

            Awaitility.await()
                      .atMost(3, TimeUnit.SECONDS)
                      .until(() -> MessagesHolder.getLatestReceivedMessage() != null);

            assertEquals("Message is NOT received by the test queue listener handler", testMessage,
                    MessagesHolder.getLatestReceivedMessage());
        }

        @Test
        void testOnErrorIsCalled() throws Exception {
            String testMessage = getCallerMethod();

            MessagingFacade.sendToQueue(QUEUE_NAME, testMessage);

            broker.stop();

            Awaitility.await()
                      .atMost(3, TimeUnit.SECONDS)
                      .until(() -> MessagesHolder.getLatestReceivedError() != null);

            assertThat(MessagesHolder.getLatestReceivedError()).matches(STOPPED_ACTIVEMQ_ERROR_MESSAGE_PATTERN);
        }
    }

    @Nested
    class TopicListenerTest {
        private static final String TOPIC_NAME = "integration-tests-topic";

        @Test
        void testOnMessageIsCalled() {
            String testMessage = getCallerMethod();

            MessagingFacade.sendToTopic(TOPIC_NAME, testMessage);

            Awaitility.await()
                      .atMost(3, TimeUnit.SECONDS)
                      .until(() -> MessagesHolder.getLatestReceivedMessage() != null);

            assertEquals("Message is NOT received by the test topic listener handler", testMessage,
                    MessagesHolder.getLatestReceivedMessage());
        }

        @Test
        void testOnErrorIsCalled() throws Exception {
            String testMessage = getCallerMethod();

            MessagingFacade.sendToTopic(TOPIC_NAME, testMessage);

            broker.stop();

            Awaitility.await()
                      .atMost(3, TimeUnit.SECONDS)
                      .until(() -> MessagesHolder.getLatestReceivedError() != null);

            assertThat(MessagesHolder.getLatestReceivedError()).matches(STOPPED_ACTIVEMQ_ERROR_MESSAGE_PATTERN);
        }
    }

    private String getCallerMethod() {
        StackTraceElement[] stackTraceElements = Thread.currentThread()
                                                       .getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElements[2];
        return stackTraceElement.getClassName() + ":" + stackTraceElement.getMethodName();
    }

}
