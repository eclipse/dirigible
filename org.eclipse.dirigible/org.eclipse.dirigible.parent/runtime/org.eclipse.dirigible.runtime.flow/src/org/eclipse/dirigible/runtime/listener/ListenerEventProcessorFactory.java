package org.eclipse.dirigible.runtime.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.dirigible.repository.logging.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class ListenerEventProcessorFactory {

	private static final Logger logger = Logger.getLogger(ListenerEventProcessorFactory.class);

	private static final List<IListenerEventProcessorProvider> listenerEventProcessorProviders = Collections
			.synchronizedList(new ArrayList<IListenerEventProcessorProvider>());

	public static void registerListenerEventProcessorProviders(BundleContext context) throws InvalidSyntaxException {
		logger.info("Registering Listener Event Processor Providers...");

		// IListenerEventProcessorProvider services
		Collection<ServiceReference<IListenerEventProcessorProvider>> serviceReferences = context
				.getServiceReferences(IListenerEventProcessorProvider.class, null);
		for (ServiceReference<IListenerEventProcessorProvider> serviceReference : serviceReferences) {
			IListenerEventProcessorProvider listenerEventProcessorProvider = context.getService(serviceReference);
			listenerEventProcessorProviders.add(listenerEventProcessorProvider);

			logger.info(String.format("%s added to the list of available Listener Event Processor Providers",
					listenerEventProcessorProvider.getClass().getCanonicalName()));
		}
	}

	/**
	 * Create a Listener Event Processor instance used for local operations
	 *
	 * @param trigger
	 * @return Listener Event Processor instance
	 */
	public static IListenerEventProcessor createListenerEventProcessor(String trigger) {
		for (IListenerEventProcessorProvider listenerEventProcessorProvider : listenerEventProcessorProviders) {
			if (listenerEventProcessorProvider.getTriggerType().equals(trigger)) {
				return listenerEventProcessorProvider.createListenerEventProcessor();
			}
		}
		return null;
	}
}
