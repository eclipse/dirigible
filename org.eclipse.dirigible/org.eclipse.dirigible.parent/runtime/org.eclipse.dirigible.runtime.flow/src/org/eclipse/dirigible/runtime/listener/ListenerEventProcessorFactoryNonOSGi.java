package org.eclipse.dirigible.runtime.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.flow.FlowsActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class ListenerEventProcessorFactoryNonOSGi {

	private static final Logger logger = Logger.getLogger(ListenerEventProcessorFactoryNonOSGi.class);

	private static final List<IListenerEventProcessorProvider> listenerEventProcessorProviders = Collections
			.synchronizedList(new ArrayList<IListenerEventProcessorProvider>());

	static String imapsListenerEventProcessorProvider = "org.eclipse.dirigible.runtime.listener.mail.ImapsListenerEventProcessorProvider";
	static String messageListenerEventProcessorProvider = "org.eclipse.dirigible.runtime.listener.message.MessageListenerEventProcessorProvider";

	static {
		listenerEventProcessorProviders.add(createListenerEventProcessorProvider(imapsListenerEventProcessorProvider));
		listenerEventProcessorProviders.add(createListenerEventProcessorProvider(messageListenerEventProcessorProvider));
	}

	private static boolean registered = false;

	public static void registerListenerEventProcessorProviders(BundleContext context) throws InvalidSyntaxException {
		logger.info("Registering Listener Event Processor Providers...");

		synchronized (ListenerEventProcessorFactoryNonOSGi.class) {
			// IListenerEventProcessorProvider services
			Collection<ServiceReference<IListenerEventProcessorProvider>> serviceReferences = context
					.getServiceReferences(IListenerEventProcessorProvider.class, null);
			for (ServiceReference<IListenerEventProcessorProvider> serviceReference : serviceReferences) {
				IListenerEventProcessorProvider listenerEventProcessorProvider = context.getService(serviceReference);
				listenerEventProcessorProviders.add(listenerEventProcessorProvider);

				logger.info(String.format("%s added to the list of available Listener Event Processor Providers",
						listenerEventProcessorProvider.getClass().getCanonicalName()));
			}
			registered = true;
		}
	}

	/**
	 * Create a Listener Event Processor instance used for local operations
	 *
	 * @param trigger
	 * @return Listener Event Processor instance
	 */
	public static IListenerEventProcessor createListenerEventProcessor(String trigger) {
		if (!registered) {
			try {
				registerListenerEventProcessorProviders(FlowsActivator.getContext());
			} catch (InvalidSyntaxException e) {
				logger.error(e.getMessage(), e);
			}
		}
		for (IListenerEventProcessorProvider listenerEventProcessorProvider : listenerEventProcessorProviders) {
			if (listenerEventProcessorProvider.getTriggerType().equals(trigger)) {
				return listenerEventProcessorProvider.createListenerEventProcessor();
			}
		}
		return null;
	}

	private static IListenerEventProcessorProvider createListenerEventProcessorProvider(String clazz) {
		try {
			return (IListenerEventProcessorProvider) Class.forName(clazz).newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
