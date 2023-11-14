package org.eclipse.dirigible.components.api.messaging;

import static org.junit.Assert.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MessagingFacadeNotInitializedTest {

    private static final String QUEUE = "test-queue";
    private static final String TOPIC = "test-topic";
    private static final long TIMEOUT = 100L;
    private static final String MESSAGE = "a test message";

    @BeforeAll
    static void setUp() {
        new MessagingFacade(null, null);
    }

    @Test
    void testReceiveFromQueue() throws TimeoutException {
        assertThrows(IllegalStateException.class, () -> MessagingFacade.receiveFromQueue(QUEUE, TIMEOUT));
    }

    @Test
    void testReceiveFromTopic() throws TimeoutException {
        assertThrows(IllegalStateException.class, () -> MessagingFacade.receiveFromTopic(TOPIC, TIMEOUT));
    }

    @Test
    void testsendToQueue() throws TimeoutException {
        assertThrows(IllegalStateException.class, () -> MessagingFacade.sendToQueue(QUEUE, MESSAGE));
    }

    @Test
    void testsendToTopic() throws TimeoutException {
        assertThrows(IllegalStateException.class, () -> MessagingFacade.sendToTopic(TOPIC, MESSAGE));
    }

}
