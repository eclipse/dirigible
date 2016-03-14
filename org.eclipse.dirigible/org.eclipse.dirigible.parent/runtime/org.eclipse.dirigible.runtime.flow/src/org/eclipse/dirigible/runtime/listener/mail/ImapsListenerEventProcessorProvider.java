package org.eclipse.dirigible.runtime.listener.mail;

import org.eclipse.dirigible.runtime.listener.IListenerEventProcessor;
import org.eclipse.dirigible.runtime.listener.IListenerEventProcessorProvider;

public class ImapsListenerEventProcessorProvider implements IListenerEventProcessorProvider {

	private static final String TRIGGER_TYPE_IMAPS = "imaps";

	@Override
	public String getTriggerType() {
		return TRIGGER_TYPE_IMAPS;
	}

	@Override
	public IListenerEventProcessor createListenerEventProcessor() {
		return new ImapsListenerEventProcessor();
	}

}
