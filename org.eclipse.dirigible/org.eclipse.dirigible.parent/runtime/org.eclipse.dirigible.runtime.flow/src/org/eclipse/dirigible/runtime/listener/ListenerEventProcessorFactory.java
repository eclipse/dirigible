package org.eclipse.dirigible.runtime.listener;

import org.eclipse.dirigible.repository.ext.utils.OSGiUtils;

public class ListenerEventProcessorFactory {

	/**
	 * Create a Listener Event Processor instance used for local operations
	 *
	 * @param trigger
	 * @return Listener Event Processor instance
	 */
	public static IListenerEventProcessor createListenerEventProcessor(String trigger) {
		if (OSGiUtils.isOSGiEnvironment()) {
			return ListenerEventProcessorFactoryOSGi.createListenerEventProcessor(trigger);
		}
		return ListenerEventProcessorFactoryNonOSGi.createListenerEventProcessor(trigger);
	}
}
