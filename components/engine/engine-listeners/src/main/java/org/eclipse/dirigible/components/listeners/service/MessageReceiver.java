package org.eclipse.dirigible.components.listeners.service;

import javax.annotation.Nullable;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);

    private final JavascriptService javascriptService;

    @Autowired
    public MessageReceiver(JavascriptService javascriptService) {
        this.javascriptService = javascriptService;
    }

    @Nullable
    public String receiveMessageFromQueue(String queue, int timeout) throws JMSException, TimeoutException {
        return receiveMessage(timeout, s -> s.createQueue(queue));
    }

    @Nullable
    public String receiveMessageFromTopic(String topic, int timeout) throws JMSException, TimeoutException {
        return receiveMessage(timeout, s -> s.createTopic(topic));
    }

    @Nullable
    private String receiveMessage(int timeout, DestinationCreator destinationCreator) throws JMSException, TimeoutException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ListenersManager.CONNECTOR_URL_ATTACH);
        try (Connection connection = connectionFactory.createConnection()) {

            connection.start();
            MessageConsumerExceptionListener exceptionListener = new MessageConsumerExceptionListener(javascriptService);
            connection.setExceptionListener(exceptionListener);

            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    MessageConsumer consumer = session.createConsumer(destinationCreator.create(session))) {

                Message message = consumer.receive(timeout);
                LOGGER.debug("Received message [{}] by synchronous consumer.", message);
                if (null == message) {
                    throw new TimeoutException("Timeout to get a message");
                }
                if (message instanceof TextMessage textMessage) {
                    return textMessage.getText();
                }
                throw new IllegalStateException("Received an unsupported message " + message);
            }
        }
    }

    private interface DestinationCreator {
        Destination create(Session session) throws JMSException;
    }

}
