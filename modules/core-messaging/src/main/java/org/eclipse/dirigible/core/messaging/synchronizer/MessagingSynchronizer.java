package org.eclipse.dirigible.core.messaging.synchronizer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.messaging.api.IMessagingCoreService;
import org.eclipse.dirigible.core.messaging.api.ListenerType;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;
import org.eclipse.dirigible.core.messaging.service.MessagingCoreService;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MessagingSynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(MessagingSynchronizer.class);

	private static final Map<String, ListenerDefinition> LISTENERS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, ListenerDefinition>());

	private static final List<String> LISTENERS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	@Inject
	private MessagingCoreService extensionsCoreService;

	public static final void forceSynchronization() {
		MessagingSynchronizer extensionsSynchronizer = StaticInjector.getInjector().getInstance(MessagingSynchronizer.class);
		extensionsSynchronizer.synchronize();
	}

	public void registerPredeliveredListener(String extensionPointPath) throws IOException {
		InputStream in = MessagingSynchronizer.class.getResourceAsStream(extensionPointPath);
		String json = IOUtils.toString(in, StandardCharsets.UTF_8);
		ListenerDefinition listenerDefinition = extensionsCoreService.parseListener(json);
		listenerDefinition.setLocation(extensionPointPath);
		LISTENERS_PREDELIVERED.put(extensionPointPath, listenerDefinition);
	}

	@Override
	public void synchronize() {
		synchronized (MessagingSynchronizer.class) {
			logger.trace("Synchronizing Listeners...");
			try {
				clearCache();
				synchronizePredelivered();
				synchronizeRegistry();
				cleanup();
				clearCache();
			} catch (Exception e) {
				logger.error("Synchronizing process for Listeners failed.", e);
			}
			logger.trace("Done synchronizing Listeners.");
		}
	}

	private void clearCache() {
		LISTENERS_SYNCHRONIZED.clear();
	}

	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered Listeners...");
		// Listeners
		for (ListenerDefinition listenerDefinition : LISTENERS_PREDELIVERED.values()) {
			synchronizeListener(listenerDefinition);
		}
		logger.trace("Done synchronizing predelivered Listeners.");
	}

	private void synchronizeListener(ListenerDefinition listenerDefinition) throws SynchronizationException {
		try {
			if (!extensionsCoreService.existsListener(listenerDefinition.getLocation())) {
				extensionsCoreService.createListener(listenerDefinition.getLocation(), listenerDefinition.getName(),
						ListenerType.values()[listenerDefinition.getType()], listenerDefinition.getModule(), listenerDefinition.getDescription());
				logger.info("Synchronized a new Extension Point [{}] from location: {}", listenerDefinition.getName(),
						listenerDefinition.getLocation());
			} else {
				ListenerDefinition existing = extensionsCoreService.getListener(listenerDefinition.getLocation());
				if (!listenerDefinition.equals(existing)) {
					extensionsCoreService.updateListener(listenerDefinition.getLocation(), listenerDefinition.getName(),
							ListenerType.values()[listenerDefinition.getType()], listenerDefinition.getModule(), listenerDefinition.getDescription());
					logger.info("Synchronized a modified Extension Point [{}] from location: {}", listenerDefinition.getName(),
							listenerDefinition.getLocation());
				}
			}
			LISTENERS_SYNCHRONIZED.add(listenerDefinition.getLocation());
		} catch (MessagingException e) {
			throw new SynchronizationException(e);
		}
	}

	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing Listeners from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing Listeners from Registry.");
	}

	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();
		if (resourceName.endsWith(IMessagingCoreService.FILE_EXTENSION_LISTENER)) {
			ListenerDefinition listenerDefinition = extensionsCoreService.parseListener(resource.getContent());
			listenerDefinition.setLocation(getRegistryPath(resource));
			synchronizeListener(listenerDefinition);
		}

	}

	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up Listeners...");

		try {
			List<ListenerDefinition> listenerDefinitions = extensionsCoreService.getListeners();
			for (ListenerDefinition listenerDefinition : listenerDefinitions) {
				if (!LISTENERS_SYNCHRONIZED.contains(listenerDefinition.getLocation())) {
					extensionsCoreService.removeListener(listenerDefinition.getLocation());
					logger.warn("Cleaned up Extension Point [{}] from location: {}", listenerDefinition.getName(), listenerDefinition.getLocation());
				}
			}
		} catch (MessagingException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up Listeners.");
	}
}
