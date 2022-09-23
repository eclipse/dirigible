/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.messaging.service;

import static java.text.MessageFormat.format;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.dirigible.core.messaging.api.IMessagingCoreService;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MessagingProducer.
 */
public class MessagingProducer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MessagingProducer.class);

	private String name;
	private char type;
	private String message;

	/**
	 * Instantiates a new messaging producer.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param message
	 *            the message
	 */
	public MessagingProducer(String name, char type, String message) {
		this.name = name;
		this.type = type;
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(SchedulerManager.CONNECTOR_URL_ATTACH);

			Connection connection = connectionFactory.createConnection();
			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			try {
				Destination destination = null;
				if (type == IMessagingCoreService.QUEUE) {
					destination = session.createQueue(this.name);
				} else if (type == IMessagingCoreService.TOPIC) {
					destination = session.createTopic(this.name);
				} else {
					throw new MessagingException(format("Invalid Destination Type [{0}] for destination [{1}]", this.type, this.name));
				}

				MessageProducer producer = session.createProducer(destination);
				producer.setDeliveryMode(DeliveryMode.PERSISTENT);

				TextMessage textMessage = session.createTextMessage(this.message);

				producer.send(textMessage);
				if (logger.isTraceEnabled()) {logger.trace(format("Message sent in [{0}]", this.name));}
			} finally {
				session.close();
				connection.close();
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}
	}
}
