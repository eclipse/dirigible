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
package org.eclipse.dirigible.components.api.messaging;

import org.eclipse.dirigible.components.listeners.service.MessagingConsumer;
import org.eclipse.dirigible.components.listeners.service.MessagingProducer;
import org.springframework.stereotype.Component;

/**
 * The Class MessagingFacade.
 */
@Component
public class MessagingFacade {

	/**
	 * Send a message to queue.
	 *
	 * @param destination the destination
	 * @param message the message
	 */
	public static final void sendToQueue(String destination, String message) {
		MessagingProducer producer = new MessagingProducer(destination, 'Q', message);
		new Thread(producer).start();
	}
	
	/**
	 * Send a message to topic.
	 *
	 * @param destination the destination
	 * @param message the message
	 */
	public static final void sendToTopic(String destination, String message) {
		MessagingProducer producer = new MessagingProducer(destination, 'T', message);
		new Thread(producer).start();
	}
	
	/**
	 * Receive a message from queue.
	 *
	 * @param destination the destination
	 * @param timeout the timeout
	 * @return the message as JSON
	 */
	public static final String receiveFromQueue(String destination, int timeout) {
		MessagingConsumer consumer = new MessagingConsumer(destination, 'Q', timeout);
		return consumer.receiveMessage();
	}
	
	/**
	 * Receive a message from topic.
	 *
	 * @param destination the destination
	 * @param timeout the timeout
	 * @return the the message as JSON
	 */
	public static final String receiveFromTopic(String destination, int timeout) {
		MessagingConsumer consumer = new MessagingConsumer(destination, 'T', timeout);
		return consumer.receiveMessage();
	}
	
}
