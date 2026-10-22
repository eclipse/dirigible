/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.rabbitmq;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.script.ScriptEngineUnavailableException;
import org.eclipse.dirigible.components.base.script.ScriptExecutor;
import org.eclipse.dirigible.components.base.spring.BeanProvider;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * The Class RabbitMQFacade.
 */
@Component
public class RabbitMQFacade extends Thread {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQFacade.class);

    /**
     * The Constant DIRIGIBLE_RABBITMQ_USERNAME.
     */
    private static final String DIRIGIBLE_RABBITMQ_USERNAME = "guest";

    /**
     * The Constant DIRIGIBLE_RABBITMQ_PASSWORD.
     */
    private static final String DIRIGIBLE_RABBITMQ_PASSWORD = "guest";

    /**
     * The Constant RABBITMQ_CLIENT.
     */
    private static final String RABBITMQ_CLIENT = "127.0.0.1:5672";

    /**
     * The Constant DIRIGIBLE_RABBITMQ_CLIENT_URI.
     */
    private static final String DIRIGIBLE_RABBITMQ_CLIENT_URI = "DIRIGIBLE_RABBITMQ_CLIENT_URI";

    /**
     * The consumers, one per queue and handler.
     */
    private static final Map<ConsumerKey, RabbitMQReceiverRunner> CONSUMERS = Collections.synchronizedMap(new HashMap<>());

    /**
     * Send message to given queue.
     *
     * @param queue the queue being used
     * @param message the message to be delivered
     */
    public static void send(String queue, String message) {
        try {
            Connection connection = connect();
            Channel channel = createChannel(connection);

            channel.queueDeclare(queue, false, false, false, null);
            channel.basicPublish("", queue, null, message.getBytes());
            if (logger.isInfoEnabled()) {
                logger.info("Sent: " + "'" + message + "'" + " to [" + queue + "]");
            }
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error sending message: " + e.toString(), e);
            }
        }
    }

    /**
     * Start listening given queue and destination.
     *
     * @param queue the queue being used
     * @param handler the destination for the message
     * @throws ScriptEngineUnavailableException when no script engine is deployed to run the handler
     */
    public static final void startListening(String queue, String handler) {
        ScriptExecutor scriptExecutor = BeanProvider.getOptionalBean(ScriptExecutor.class)
                                                    .orElseThrow(() -> new ScriptEngineUnavailableException(handler));
        startListening(new ConsumerKey(queue, handler), new ScriptMessageListener(handler, scriptExecutor));
    }

    /**
     * Start listening on the given queue with a Java listener - no script engine is involved. A
     * listener is told apart by its identity ({@link Object#equals(Object)}), so stop it by passing the
     * same instance.
     *
     * @param queue the queue being used
     * @param listener the listener the messages are handed to
     */
    public static final void startListening(String queue, RabbitMQMessageListener listener) {
        startListening(new ConsumerKey(queue, listener), listener);
    }

    private static void startListening(ConsumerKey key, RabbitMQMessageListener listener) {
        String queue = key.queue();
        synchronized (CONSUMERS) {
            if (CONSUMERS.containsKey(key)) {
                if (logger.isWarnEnabled()) {
                    logger.warn("RabbitMQ receiver [" + queue + "] has already been started.");
                }
                return;
            }
            Connection connection = connect();
            Channel channel = createChannel(connection);
            RabbitMQReceiverRunner receiverRunner = new RabbitMQReceiverRunner(connection, channel, queue, listener);
            Thread receiverThread = new Thread(receiverRunner);
            receiverThread.setDaemon(false);
            receiverThread.start();

            CONSUMERS.put(key, receiverRunner);
        }
        if (logger.isInfoEnabled()) {
            logger.info("RabbitMQ receiver created for [" + queue + "]");
        }
    }

    /**
     * Stop listening on given queue and destination.
     *
     * @param queue the queue being used
     * @param handler the destination for the message
     */
    public static final void stopListening(String queue, String handler) {
        stopListening(new ConsumerKey(queue, handler));
    }

    /**
     * Stop listening on the given queue with a Java listener started before.
     *
     * @param queue the queue being used
     * @param listener the listener the consumer was started with
     */
    public static final void stopListening(String queue, RabbitMQMessageListener listener) {
        stopListening(new ConsumerKey(queue, listener));
    }

    private static void stopListening(ConsumerKey key) {
        String queue = key.queue();
        RabbitMQReceiverRunner receiverRunner = CONSUMERS.remove(key);
        if (receiverRunner != null) {
            receiverRunner.stop();
            if (logger.isInfoEnabled()) {
                logger.info("RabbitMQ receiver stopped for [" + queue + "]");
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("RabbitMQ receiver [" + queue + "] has not been started yet.");
            }
        }
    }

    /**
     * Connect.
     *
     * @return the connection
     */
    private static Connection connect() {
        String[] splitUri = Configuration.get(DIRIGIBLE_RABBITMQ_CLIENT_URI, RABBITMQ_CLIENT)
                                         .split(":");
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
            if (logger.isErrorEnabled()) {
                logger.error("Error establishing connection to AMQP: " + e.toString(), e);
            }
        }
        return connection;
    }

    /**
     * Creates the channel.
     *
     * @param connection the connection
     * @return the channel
     */
    private static Channel createChannel(Connection connection) {
        Channel channel = null;
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error creating channel: " + e.toString(), e);
            }
        }
        return channel;
    }

    /**
     * Identifies a consumer: its queue and its handler - the registry path of a handler script, or a
     * Java listener.
     *
     * @param queue the queue
     * @param handler the handler
     */
    private record ConsumerKey(String queue, Object handler) {
    }
}
