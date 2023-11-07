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

import static java.text.MessageFormat.format;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class MessagingConsumer.
 */
public class MessagingConsumer implements Runnable, ExceptionListener {

  private static final Logger logger = LoggerFactory.getLogger(MessagingConsumer.class);

  private static final String DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_MESSAGE = "messaging/wrappers/onMessage.js";
  private static final String DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_ERROR = "messaging/wrappers/onError.js";

  private String name;
  private char type;
  private String handler;
  private int timeout = 1000;
  private boolean stopped;

  /** The javascript service. */
  @Autowired
  private JavascriptService javascriptService;

  /**
   * Instantiates a new messaging consumer.
   *
   * @param name the name
   * @param type the type
   * @param handler the handler
   * @param timeout the timeout
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
   * @param name the name
   * @param type the type
   * @param timeout the timeout
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
      if (type == 'Q') {
        destination = session.createQueue(this.name);
      } else if (type == 'T') {
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
            if (message instanceof TextMessage) {
              Map<Object, Object> context = createMessagingContext();
              context.put("message", escapeCodeString(((TextMessage) message).getText()));
              RepositoryPath path = new RepositoryPath(DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_MESSAGE);
              JavascriptService.get()
                               .handleRequest(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
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
   *
   * @see javax.jms.ExceptionListener#onException(javax.jms.JMSException)
   */
  @Override
  public synchronized void onException(JMSException exception) {
    try {
      Map<Object, Object> context = createMessagingContext();
      context.put("error", escapeCodeString(exception.getMessage()));
      RepositoryPath path = new RepositoryPath(DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_ERROR);
      javascriptService.handleRequest(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error(e.getMessage(), e);
      }
    }
    if (logger.isErrorEnabled()) {
      logger.error(exception.getMessage(), exception);
    }
  }

  private Map<Object, Object> createMessagingContext() {
    Map<Object, Object> context = new HashMap<Object, Object>();
    context.put("handler", this.handler);
    return context;
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
