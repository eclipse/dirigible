package org.eclipse.dirigible.api.v3.messaging;

import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.core.messaging.api.DestinationType;
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
	
}
