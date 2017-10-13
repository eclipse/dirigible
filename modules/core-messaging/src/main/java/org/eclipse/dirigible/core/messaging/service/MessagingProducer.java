package org.eclipse.dirigible.core.messaging.service;

import static java.text.MessageFormat.format;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.dirigible.core.messaging.api.DestinationType;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessagingProducer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MessagingProducer.class);

	private String name;
	private DestinationType type;
	private String message;

	public MessagingProducer(String name, DestinationType type, String message) {
		this.name = name;
		this.type = type;
		this.message = message;
	}

	@Override
	public void run() {
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(MessagingManager.CONNECTOR_URL_ATTACH);

			Connection connection = connectionFactory.createConnection();
			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Destination destination = null;
			if (type.equals(DestinationType.QUEUE)) {
				destination = session.createQueue(this.name);
			} else if (type.equals(DestinationType.TOPIC)) {
				destination = session.createTopic(this.name);
			} else {
				throw new MessagingException(format("Invalid Destination Type [{0}] for destination [{1}]", this.type.name(), this.name));
			}

			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);

			TextMessage textMessage = session.createTextMessage(this.message);

			producer.send(textMessage);
			logger.trace(format("Message sent in [{0}]", this.name));

			session.close();
			connection.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
