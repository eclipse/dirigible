/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.service;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.dirigible.components.listeners.domain.ListenerKind;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.modules.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.nio.file.Path;

import static java.text.MessageFormat.format;

/**
 * The Class MessagingConsumer.
 */
public class MessagingConsumer implements Runnable, ExceptionListener {

    private static final Logger logger = LoggerFactory.getLogger(MessagingConsumer.class);
    private final String name;
    private final ListenerKind type;
    private String handler;
    private int timeout = 1000;
    private boolean stopped;

    /**
     * Instantiates a new messaging consumer.
     *
     * @param name the name
     * @param type the type
     * @param handler the handler
     * @param timeout the timeout
     */
    public MessagingConsumer(String name, ListenerKind type, String handler, int timeout) {
        this.name = name;
        this.type = type;
        this.handler = handler;
        this.timeout = timeout;
    }

    /**
     * Instantiates a new messaging consumer.
     *
     * @param name the name
     * @param type the type
     * @param timeout the timeout
     */
    public MessagingConsumer(String name, ListenerKind type, int timeout) {
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
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (this.handler != null) {
            while (!this.stopped && !Thread.currentThread()
                                           .isInterrupted()) {
                receiveMessage();
            }
        }
    }

    /**
     * Receive message.
     *
     * @return the string
     */
    public String receiveMessage() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Starting a message listener for {} ...", this.name);
            }

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ListenersManager.CONNECTOR_URL_ATTACH);

            Connection connection = connectionFactory.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = null;
            if (ListenerKind.QUEUE.equals(type)) {
                destination = session.createQueue(this.name);
            } else if (ListenerKind.TOPIC.equals(type)) {
                destination = session.createTopic(this.name);
            } else {
                throw new Exception("Invalid Destination Type: " + this.type);
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
                        if (logger.isTraceEnabled()) {
                            logger.trace(format("Start processing a received message in [{0}] by [{1}] ...", this.name, this.handler));
                        }
                        if (message instanceof TextMessage textMessage) {
                            String messageAsString = escapeCodeString(textMessage.getText());
                            executeOnMessageHandler(messageAsString);
                        } else {
                            throw new Exception(format("Invalid message [{0}] has been received in destination [{1}]", message, this.name));
                        }
                        if (logger.isTraceEnabled()) {
                            logger.trace(format("Done processing the received message in [{0}] by [{1}]", this.name, this.handler));
                        }
                    }
                } else {
                    message = consumer.receive(this.timeout);
                    if (logger.isDebugEnabled()) {
                        logger.debug(format("Received message in [{0}] by synchronous consumer.", this.name));
                    }
                    if (message instanceof TextMessage textMessage) {
                        return textMessage.getText();
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
     *
     * @see javax.jms.ExceptionListener#onException(javax.jms.JMSException)
     */
    @Override
    public synchronized void onException(JMSException exception) {
        try {
            String errorMessage = escapeCodeString(exception.getMessage());
            executeOnErrorHandler(errorMessage);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
        if (logger.isErrorEnabled()) {
            logger.error(exception.getMessage(), exception);
        }
    }

    private void executeOnMessageHandler(String message) {
        executeHandler("onMessage", message);
    }

    private void executeOnErrorHandler(String errorMessage) {
        executeHandler("onError", errorMessage);
    }

    private void executeHandler(String methodName, String message) {
        try (DirigibleJavascriptCodeRunner runner = new DirigibleJavascriptCodeRunner()) {
            Module module = runner.run(Path.of(handler));
            runner.runMethod(module, methodName, message);
        }
    }

    /**
     * Escape code string.
     *
     * @param raw the raw
     * @return the string
     */
    private String escapeCodeString(String raw) {
        return raw.replace("'", "&amp;");
    }
}
