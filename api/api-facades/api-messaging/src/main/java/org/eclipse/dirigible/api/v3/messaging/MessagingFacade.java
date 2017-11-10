/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.messaging;

import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.core.messaging.api.DestinationType;
import org.eclipse.dirigible.core.messaging.service.MessagingConsumer;
import org.eclipse.dirigible.core.messaging.service.MessagingProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessagingFacade implements IScriptingFacade {

	private static final Logger logger = LoggerFactory.getLogger(MessagingFacade.class);

	public static final void sendToQueue(String destination, String message) {
		MessagingProducer producer = new MessagingProducer(destination, DestinationType.QUEUE, message);
		new Thread(producer).start();
	}
	
	public static final void sendToTopic(String destination, String message) {
		MessagingProducer producer = new MessagingProducer(destination, DestinationType.TOPIC, message);
		new Thread(producer).start();
	}
	
	public static final String receiveFromQueue(String destination, int timeout) {
		MessagingConsumer consumer = new MessagingConsumer(destination, DestinationType.QUEUE, timeout);
		return consumer.receiveMessage();
	}
	
	public static final String receiveFromTopic(String destination, int timeout) {
		MessagingConsumer consumer = new MessagingConsumer(destination, DestinationType.TOPIC, timeout);
		return consumer.receiveMessage();
	}
	
}
