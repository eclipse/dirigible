package org.eclipse.dirigible.core.messaging.service;

import static java.text.MessageFormat.format;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.messaging.api.DestinationType;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessagingConsumer implements Runnable, ExceptionListener {

	private static final Logger logger = LoggerFactory.getLogger(MessagingConsumer.class);

	private String name;
	private DestinationType type;
	private String handler;

	private boolean stopped;

	public MessagingConsumer(String name, DestinationType type, String handler) {
		this.name = name;
		this.type = type;
		this.handler = handler;
	}

	public void stop() {
		this.stopped = true;
	}

	@Override
	public void run() {
		try {
			logger.info("Starting a message listener for " + this.name);

			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(MessagingManager.CONNECTOR_URL_ATTACH);

			Connection connection = connectionFactory.createConnection();
			connection.start();

			connection.setExceptionListener(this);

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Destination destination = null;
			if (type.equals(DestinationType.QUEUE)) {
				destination = session.createQueue(this.name);
			} else if (type.equals(DestinationType.TOPIC)) {
				destination = session.createTopic(this.name);
			} else {
				throw new MessagingException("Invalid Destination Type: " + this.type.name());
			}

			MessageConsumer consumer = session.createConsumer(destination);

			Message message = null;
			while (!this.stopped) {
				message = consumer.receive(1000);
				if (message == null) {
					continue;
				}
				logger.debug(format("Start processing a received message in [{0}] by [{1}] ..."), this.name, this.handler);
				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					String text = textMessage.getText();
					String wrapper = generateWrapperOnMessage(text);
					ScriptEngineExecutorsManager.executeServiceCode(IJavascriptEngineExecutor.JAVASCRIPT_TYPE_DEFAULT, wrapper, null);
				} else {
					throw new MessagingException(format("Invalid message [{0}] has been received in destination [{1}]", message, this.name));
				}
				logger.debug(format("Done processing the received message in [{0}] by [{1}]"), this.name, this.handler);
			}
			consumer.close();
			session.close();
			connection.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public synchronized void onException(JMSException exception) {
		String wrapper = generateWrapperOnError(exception.getMessage());
		try {
			ScriptEngineExecutorsManager.executeServiceCode(IJavascriptEngineExecutor.JAVASCRIPT_TYPE_DEFAULT, wrapper, null);
		} catch (ScriptingException e) {
			logger.error(e.getMessage(), e);
		}
		logger.error(exception.getMessage(), exception);
	}

	private String generateWrapperOnMessage(String message) {
		String wrapper = new StringBuilder().append("var handler = require('").append(escapeCodeString(this.handler))
				.append("');handler.onMessage('" + escapeCodeString(message) + "');").toString();
		return wrapper;
	}

	private String generateWrapperOnError(String error) {
		String wrapper = new StringBuilder().append("var handler = require('").append(escapeCodeString(this.handler))
				.append("');handler.onError('" + escapeCodeString(error) + "');").toString();
		return wrapper;
	}

	private String escapeCodeString(String raw) {
		return raw.replace("'", "&amp;");
	}
}
