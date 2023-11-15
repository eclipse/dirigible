package org.eclipse.dirigible.integration.tests.messaging;

import org.eclipse.dirigible.components.api.messaging.MessagingFacade;

public class MessageProducer {

    public static void asyncSendMessageToTopic(String topic, String message) {
        new Thread(() -> MessagingFacade.sendToTopic(topic, message)).start();
    }

}
