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
import static org.junit.Assert.assertThrows;
import org.eclipse.dirigible.DirigibleApplication;
import org.eclipse.dirigible.components.api.messaging.MessagingFacade;
import org.eclipse.dirigible.components.api.messaging.TimeoutException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {DirigibleApplication.class})
@AutoConfigureMockMvc
class MessagingFacadeIT {

    private static final String TEST_MESSAGE = "Test message";
    private static final String TEST_MESSAGE_2 = "Test message 2";
    private static final long TIMEOUT_MILLIS = 500L;

    @Nested
    class QueueTest {
        private static final String QUEUE = "my-test-queue";

        @Test
        void testSendReceiveOneMessage() {
            MessagingFacade.sendToQueue(QUEUE, TEST_MESSAGE);
            String actualMessage = MessagingFacade.receiveFromQueue(QUEUE, TIMEOUT_MILLIS);

            assertEquals("Unexpected message", TEST_MESSAGE, actualMessage);
        }

        @Test
        void testSendReceiveTwoMessages() {
            MessagingFacade.sendToQueue(QUEUE, TEST_MESSAGE);
            MessagingFacade.sendToQueue(QUEUE, TEST_MESSAGE_2);
            String actualMessage = MessagingFacade.receiveFromQueue(QUEUE, TIMEOUT_MILLIS);
            String actualMessage2 = MessagingFacade.receiveFromQueue(QUEUE, TIMEOUT_MILLIS);

            assertEquals("Unexpected message", TEST_MESSAGE, actualMessage);
            assertEquals("Unexpected message", TEST_MESSAGE_2, actualMessage2);
        }

        @Test
        void testReceiveOnTimeout() {
            assertThrows(TimeoutException.class, () -> MessagingFacade.receiveFromQueue(QUEUE, TIMEOUT_MILLIS));
        }
    }

}
