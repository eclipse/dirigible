package org.eclipse.dirigible.runtime.listener.message;

import org.eclipse.dirigible.runtime.listener.IListenerEventProcessor;
import org.eclipse.dirigible.runtime.listener.IListenerEventProcessorProvider;

public class MessageListenerEventProcessorProvider implements IListenerEventProcessorProvider {

	private static final String TRIGGER_TYPE_MESSAGE = "message";

	@Override
	public String getTriggerType() {
		return TRIGGER_TYPE_MESSAGE;
	}

	@Override
	public IListenerEventProcessor createListenerEventProcessor() {
		return new MessageListenerEventProcessor();
	}

}
