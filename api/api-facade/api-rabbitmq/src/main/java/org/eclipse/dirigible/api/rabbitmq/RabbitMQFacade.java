/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMQFacade extends Thread implements IScriptingFacade {
	private static final String DIRIGIBLE_RABBITQM_USERNAME = "guest";

	private static final String DIRIGIBLE_RABBITQM_PASSWORD = "guest";

	private static final String DIRIGIBLE_RABBITQM_HOST = "127.0.0.1";
	
	private static final int DEFAULT_RABBITQM_PORT = 5672;

	private static final Logger logger = LoggerFactory.getLogger(RabbitMQFacade.class);

	private static Map<String, RabbitMQReceiverRunner> RECEIVERS = Collections.synchronizedMap(new HashMap());

	public static Connection connect() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(DIRIGIBLE_RABBITQM_HOST);
		factory.setUsername(DIRIGIBLE_RABBITQM_USERNAME);
		factory.setPassword(DIRIGIBLE_RABBITQM_PASSWORD);
		factory.setPort(DEFAULT_RABBITQM_PORT);
		factory.setConnectionTimeout(20000);
		Connection connection = null;

		try {
			connection = factory.newConnection();
		} catch (Exception e) {
			logger.error("Error establishing connection to AMQP" +  e.toString());
		}
		return connection;
	}

	public static Channel createChannel(Connection connection) {
		Channel channel = null;
		try {
			channel = connection.createChannel();
		} catch (IOException e) {
			logger.error("Error creating channel" + e.toString());
			e.printStackTrace();
		}
		return channel;
	}

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

	public static final void startReceive(String queue) {
		Connection connection = connect();
		Channel channel = createChannel(connection);
		
		RabbitMQReceiverRunner receiverRunner = null;
		receiverRunner = RECEIVERS.get(queue);
		
		if(receiverRunner == null) {
			receiverRunner = new RabbitMQReceiverRunner(connection, channel, queue);
			Thread receiverThread = new Thread(receiverRunner);
			receiverThread.setDaemon(false);
			receiverThread.start();
			
			RECEIVERS.put(queue, receiverRunner);
			logger.info("RabbitMQ receiver created for [" + queue + "]");
		}else {
			logger.warn("RabbitMQ receiver [" + queue + "] has already been started.");
		}
	}

	public static final void stopReceive(String queue) {
		RabbitMQReceiverRunner receiverRunner = null;
		receiverRunner = RECEIVERS.get(queue);
		if(receiverRunner != null) {
			receiverRunner.stop();
			RECEIVERS.remove(queue);
			logger.info("RabbitMQ receiver stopped for [" + queue + "]");
		}else {
			logger.warn("RabbitMQ receiver [" + queue + "] has not been started yet.");
		}
	}
}
