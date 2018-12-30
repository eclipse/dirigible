/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
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
import org.eclipse.dirigible.core.messaging.api.IMessagingCoreService;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MessagingConsumer.
 */
public class MessagingConsumer implements Runnable, ExceptionListener {

	private static final Logger logger = LoggerFactory.getLogger(MessagingConsumer.class);

	private String name;
	private char type;
	private String handler;
	private int timeout = 1000;
	private boolean stopped;

	/**
	 * Instantiates a new messaging consumer.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param handler
	 *            the handler
	 * @param timeout
	 *            the timeout
	 */
	public MessagingConsumer(String name, char type, String handler, int timeout) {
		this.name = name;
		this.type = type;
		this.handler = handler;
		this.timeout = timeout;
	}

	/**
	 * Instantiates a new messaging consumer.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param timeout
	 *            the timeout
	 */
	public MessagingConsumer(String name, char type, int timeout) {
		this.name = name;
		this.type = type;
		this.timeout = timeout;
	}

	/**
	 * Stops to receive messages.
	 */
	public void stop() {
		this.stopped = true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		receiveMessage();
	}

	/**
	 * Receive message.
	 *
	 * @return the string
	 */
	public String receiveMessage() {
		try {
			logger.info("Starting a message listener for " + this.name);

			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(SchedulerManager.CONNECTOR_URL_ATTACH);

			Connection connection = connectionFactory.createConnection();
			connection.start();

			connection.setExceptionListener(this);

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Destination destination = null;
			if (type == IMessagingCoreService.QUEUE) {
				destination = session.createQueue(this.name);
			} else if (type == IMessagingCoreService.TOPIC) {
				destination = session.createTopic(this.name);
			} else {
				throw new MessagingException("Invalid Destination Type: " + this.type);
			}

			MessageConsumer consumer = session.createConsumer(destination);
			try {
				Message message = null;
				if (this.handler != null) {
					while (!this.stopped) {
						message = consumer.receive(this.timeout);
						if (message == null) {
							continue;
						}
						logger.trace(format("Start processing a received message in [{0}] by [{1}] ...", this.name, this.handler));
						if (message instanceof TextMessage) {
							TextMessage textMessage = (TextMessage) message;
							String text = textMessage.getText();
							String wrapper = generateWrapperOnMessage(text);
							ScriptEngineExecutorsManager.executeServiceCode(IJavascriptEngineExecutor.JAVASCRIPT_TYPE_DEFAULT, wrapper, null);
						} else {
							throw new MessagingException(format("Invalid message [{0}] has been received in destination [{1}]", message, this.name));
						}
						logger.trace(format("Done processing the received message in [{0}] by [{1}]", this.name, this.handler));
					}
				} else {
					message = consumer.receive(this.timeout);
					logger.debug(format("Received message in [{0}] by synchronous consumer.", this.name));
					if (message instanceof TextMessage) {
						TextMessage textMessage = (TextMessage) message;
						String text = textMessage.getText();
						return text;
					}
					return null;
				}
			} finally {
				consumer.close();
				session.close();
				connection.close();
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.jms.ExceptionListener#onException(javax.jms.JMSException)
	 */
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

	/**
	 * Generate wrapper on message.
	 *
	 * @param message
	 *            the message
	 * @return the string
	 */
	private String generateWrapperOnMessage(String message) {
		String wrapper = new StringBuilder().append("var handler = require('").append(escapeCodeString(this.handler))
				.append("');handler.onMessage('" + escapeCodeString(message) + "');").toString();
		return wrapper;
	}

	/**
	 * Generate wrapper on error.
	 *
	 * @param error
	 *            the error
	 * @return the string
	 */
	private String generateWrapperOnError(String error) {
		String wrapper = new StringBuilder().append("var handler = require('").append(escapeCodeString(this.handler))
				.append("');handler.onError('" + escapeCodeString(error) + "');").toString();
		return wrapper;
	}

	/**
	 * Escape code string.
	 *
	 * @param raw
	 *            the raw
	 * @return the string
	 */
	private String escapeCodeString(String raw) {
		return raw.replace("'", "&amp;");
	}
}
