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
package org.eclipse.dirigible.api.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMQFacade extends Thread implements IScriptingFacade {

	private static final Logger logger = LoggerFactory.getLogger(RabbitMQFacade.class);

	private static final String DIRIGIBLE_RABBITMQ_USERNAME = "guest";

	private static final String DIRIGIBLE_RABBITMQ_PASSWORD = "guest";

	//private static final String DIRIGIBLE_RABBITMQ_HOST = "127.0.0.1";

	//private static final int DEFAULT_RABBITQM_PORT = 5672;
	
	private static final String RABBITMQ_CLIENT = "127.0.0.1:5672";
	
	private static final String DIRIGIBLE_RABBITMQ_CLIENT_URI = "DIRIGIBLE_RABBITMQ_CLIENT_URI";

	private static Map<String, RabbitMQReceiverRunner> CONSUMERS = Collections.synchronizedMap(new HashMap());

	/**
	 * Send message to given queue
	 * 
	 * @param queue   the queue being used
	 * @param message the message to be delivered
	 */
	public static void send(String queue, String message) {
		try {
			Connection connection = connect();
			Channel channel = createChannel(connection);

			channel.queueDeclare(queue, false, false, false, null);
			channel.basicPublish("", queue, null, message.getBytes());
			logger.info("Sent: " + "'" + message + "'" + " to [" + queue + "]");
		} catch (IOException e) {
			logger.error("Error sending message" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Start listening given queue and destination
	 * 
	 * @param queue   the queue being used
	 * @param handler the destination for the message
	 */
	public static final void startListening(String queue, String handler) {
		Connection connection = connect();
		Channel channel = createChannel(connection);
		String location = null;

		RabbitMQReceiverRunner receiverRunner = null;
		location = createLocation(queue, handler);
		receiverRunner = CONSUMERS.get(location);

		if (receiverRunner == null) {
			receiverRunner = new RabbitMQReceiverRunner(connection, channel, queue, handler);
			Thread receiverThread = new Thread(receiverRunner);
			receiverThread.setDaemon(false);
			receiverThread.start();

			CONSUMERS.put(location, receiverRunner);
			logger.info("RabbitMQ receiver created for [" + queue + "]");
		} else {
			logger.warn("RabbitMQ receiver [" + queue + "] has already been started.");
		}
	}

	/**
	 * Stop listening on given queue and destination
	 * 
	 * @param queue   the queue being used
	 * @param handler the destination for the message
	 */
	public static final void stopListening(String queue, String handler) {
		RabbitMQReceiverRunner receiverRunner = null;
		String location = null;

		location = createLocation(queue, handler);
		receiverRunner = CONSUMERS.get(location);
		if (receiverRunner != null) {
			receiverRunner.stop();
			CONSUMERS.remove(location);
			logger.info("RabbitMQ receiver stopped for [" + queue + "]");
		} else {
			logger.warn("RabbitMQ receiver [" + queue + "] has not been started yet.");
		}
	}

	private static Connection connect() {
		String[] splitUri = Configuration.get(DIRIGIBLE_RABBITMQ_CLIENT_URI, RABBITMQ_CLIENT).split(":");
        String host = splitUri[0];
        int port = Integer.parseInt(splitUri[1]);
        
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setUsername(DIRIGIBLE_RABBITMQ_USERNAME);
		factory.setPassword(DIRIGIBLE_RABBITMQ_PASSWORD);
		factory.setPort(port);
		factory.setConnectionTimeout(20000);
		Connection connection = null;

		try {
			connection = factory.newConnection();
		} catch (Exception e) {
			logger.error("Error establishing connection to AMQP" + e.toString());
		}
		return connection;
	}

	private static Channel createChannel(Connection connection) {
		Channel channel = null;
		try {
			channel = connection.createChannel();
		} catch (IOException e) {
			logger.error("Error creating channel" + e.toString());
			e.printStackTrace();
		}
		return channel;
	}

	/**
	 * Create internal identifier for a consumer
	 * 
	 * @param queue   the queue being used
	 * @param handler the destination for the message
	 * @return the identifier
	 */
	private static String createLocation(String queue, String handler) {
		return "[" + queue + "]:[" + handler + "]";
	}
}
