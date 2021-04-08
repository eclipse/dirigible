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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabbitMQReceiverRunner implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(RabbitMQReceiverRunner.class);

	private final Connection connection;
	private final Channel channel;
	private final String queue;

	private final AtomicBoolean stopped = new AtomicBoolean(false);

	public RabbitMQReceiverRunner(Connection connection, Channel channel, String queue) {
		this.connection = connection;
		this.channel = channel;
		this.queue = queue;
	}

	/**
	 * Start the receiver
	 */
	@Override
	public void run() {
		try {
			logger.info("Starting a RabbitMQ receiver for: " + this.queue);
			channel.queueDeclare(queue, false, false, false, null);
			while (!stopped.get()) {
				Consumer consumer = new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
							byte[] body) throws IOException {
						String message = new String(body, "UTF-8");
						logger.info(" Received message:" + "'" + message + "'" + " from " + envelope.getRoutingKey());
					}
				};
				channel.basicConsume(queue, true, consumer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				channel.queueDelete(queue);
				channel.close();
				connection.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Stop the receiver
	 */
	public void stop() {
		stopped.set(true);
	}

}
