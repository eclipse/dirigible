package org.eclipse.dirigible.runtime.listener.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.dirigible.repository.ext.messaging.EMessagingException;

public class MessageListenerManager {

	private static MessageListenerManager INSTANCE;

	private List<MessageListenerEventProcessor> processors = Collections.synchronizedList(new ArrayList<MessageListenerEventProcessor>());

	public static MessageListenerManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MessageListenerManager();
		}
		return INSTANCE;
	}

	private MessageListenerManager() {

	}

	public void registerProcessor(MessageListenerEventProcessor processor) {
		processors.add(processor);
	}

	public void unregisterProcessor(MessageListenerEventProcessor processor) {
		processors.remove(processor);
	}

	public void processMessages() throws EMessagingException {
		for (MessageListenerEventProcessor processor : processors) {
			processor.processMessages();
		}
	}
}
