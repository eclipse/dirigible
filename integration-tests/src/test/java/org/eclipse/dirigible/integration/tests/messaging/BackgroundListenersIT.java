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
package org.eclipse.dirigible.integration.tests.messaging;

import static org.junit.Assert.assertEquals;
import java.util.concurrent.TimeUnit;
import org.eclipse.dirigible.components.api.messaging.MessagingFacade;
import org.eclipse.dirigible.integration.tests.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.awaitility.Awaitility;

class BackgroundListenersIT extends IntegrationTest {

    @BeforeEach
    void setUp() {
        MessagesHolder.clearLatestReceivedMessage();
        MessagesHolder.clearLatestReceivedError();
    }

    @Nested
    class QueueListenerTest {
        private static final String TEST_MESSAGE = "this-is-a-queue-test-message";
        private static final String QUEUE_NAME = "integration-tests-queue";

        @Test
        void testOnMessageIsCalled() {
            // the test uses the listener and handler which are defined in
            // META-INF/dirigible/integration-tests-project

            MessagingFacade.sendToQueue(QUEUE_NAME, TEST_MESSAGE);

            Awaitility.await()
                      .atMost(3, TimeUnit.SECONDS)
                      .pollDelay(100, TimeUnit.MILLISECONDS)
                      .until(() -> MessagesHolder.getLatestReceivedMessage() != null);

            assertEquals("Message is NOT received by the test queue listener handler", TEST_MESSAGE,
                    MessagesHolder.getLatestReceivedMessage());
        }
    }

    @Nested
    class TopicListenerTest {
        private static final String TEST_MESSAGE = "this-is-a-topic-test-message";
        private static final String TOPIC_NAME = "integration-tests-topic";

        @Test
        void testOnMessageIsCalled() {
            // the test uses the listener and handler which are defined in
            // META-INF/dirigible/integration-tests-project

            MessagingFacade.sendToTopic(TOPIC_NAME, TEST_MESSAGE);

            Awaitility.await()
                      .atMost(3, TimeUnit.SECONDS)
                      .pollDelay(100, TimeUnit.MILLISECONDS)
                      .until(() -> MessagesHolder.getLatestReceivedMessage() != null);

            assertEquals("Message is NOT received by the test topic listener handler", TEST_MESSAGE,
                    MessagesHolder.getLatestReceivedMessage());
        }
    }

}
