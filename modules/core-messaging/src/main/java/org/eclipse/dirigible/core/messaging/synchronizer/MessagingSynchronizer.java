/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

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
import org.eclipse.dirigible.core.messaging.api.DestinationType;
import org.eclipse.dirigible.core.messaging.api.IMessagingCoreService;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;
import org.eclipse.dirigible.core.messaging.service.MessagingCoreService;
import org.eclipse.dirigible.core.messaging.service.MessagingManager;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class MessagingSynchronizer.
 */
@Singleton
public class MessagingSynchronizer extends AbstractSynchronizer {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(MessagingSynchronizer.class);

	/** The Constant LISTENERS_PREDELIVERED. */
	private static final Map<String, ListenerDefinition> LISTENERS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, ListenerDefinition>());

	/** The Constant LISTENERS_SYNCHRONIZED. */
	private static final List<String> LISTENERS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	/** The messaging core service. */
	@Inject
	private MessagingCoreService messagingCoreService;

	/** The messaging manager. */
	@Inject
	private MessagingManager messagingManager;

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		MessagingSynchronizer extensionsSynchronizer = StaticInjector.getInjector().getInstance(MessagingSynchronizer.class);
		extensionsSynchronizer.synchronize();
	}

	/**
	 * Register predelivered listener.
	 *
	 * @param listenerPath the listener path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredListener(String listenerPath) throws IOException {
		InputStream in = MessagingSynchronizer.class.getResourceAsStream(listenerPath);
		String json = IOUtils.toString(in, StandardCharsets.UTF_8);
		ListenerDefinition listenerDefinition = messagingCoreService.parseListener(json);
		listenerDefinition.setLocation(listenerPath);
		LISTENERS_PREDELIVERED.put(listenerPath, listenerDefinition);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (MessagingSynchronizer.class) {
			logger.trace("Synchronizing Listeners...");
			try {
				clearCache();
				synchronizePredelivered();
				synchronizeRegistry();
				startListeners();
				cleanup();
				clearCache();
			} catch (Exception e) {
				logger.error("Synchronizing process for Listeners failed.", e);
			}
			logger.trace("Done synchronizing Listeners.");
		}
	}

	/**
	 * Start listeners.
	 */
	private void startListeners() {
		logger.trace("Start Listeners...");

		for (String listenerLocation : LISTENERS_SYNCHRONIZED) {
			if (!messagingManager.existsListener(listenerLocation)) {
				try {
					ListenerDefinition listenerDefinition = messagingCoreService.getListener(listenerLocation);
					messagingManager.startListener(listenerDefinition);
				} catch (MessagingException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		List<String> runningListeners = messagingManager.getRunningListeners();
		for (String listenerLocation : runningListeners) {
			try {
				if (!LISTENERS_SYNCHRONIZED.contains(listenerLocation)) {
					ListenerDefinition listenerDefinition = messagingCoreService.getListener(listenerLocation);
					messagingManager.stopListener(listenerDefinition);
				}
			} catch (MessagingException e) {
				logger.error(e.getMessage(), e);
			}
		}

		logger.trace("Running Listeners: " + runningListeners.size());
		logger.trace("Done starting Listeners.");
	}

	/**
	 * Clear cache.
	 */
	private void clearCache() {
		LISTENERS_SYNCHRONIZED.clear();
	}

	/**
	 * Synchronize predelivered.
	 *
	 * @throws SynchronizationException the synchronization exception
	 */
	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered Listeners...");
		// Listeners
		for (ListenerDefinition listenerDefinition : LISTENERS_PREDELIVERED.values()) {
			synchronizeListener(listenerDefinition);
		}
		logger.trace("Done synchronizing predelivered Listeners.");
	}

	/**
	 * Synchronize listener.
	 *
	 * @param listenerDefinition the listener definition
	 * @throws SynchronizationException the synchronization exception
	 */
	private void synchronizeListener(ListenerDefinition listenerDefinition) throws SynchronizationException {
		try {
			if (!messagingCoreService.existsListener(listenerDefinition.getLocation())) {
				messagingCoreService.createListener(listenerDefinition.getLocation(), listenerDefinition.getName(),
						DestinationType.values()[listenerDefinition.getType()], listenerDefinition.getModule(), listenerDefinition.getDescription());
				logger.info("Synchronized a new Listener [{}] from location: {}", listenerDefinition.getName(), listenerDefinition.getLocation());
			} else {
				ListenerDefinition existing = messagingCoreService.getListener(listenerDefinition.getLocation());
				if (!listenerDefinition.equals(existing)) {
					messagingCoreService.updateListener(listenerDefinition.getLocation(), listenerDefinition.getName(),
							DestinationType.values()[listenerDefinition.getType()], listenerDefinition.getModule(),
							listenerDefinition.getDescription());
					logger.info("Synchronized a modified Listener [{}] from location: {}", listenerDefinition.getName(),
							listenerDefinition.getLocation());
				}
			}
			LISTENERS_SYNCHRONIZED.add(listenerDefinition.getLocation());
		} catch (MessagingException e) {
			throw new SynchronizationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing Listeners from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing Listeners from Registry.");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();
		if (resourceName.endsWith(IMessagingCoreService.FILE_EXTENSION_LISTENER)) {
			ListenerDefinition listenerDefinition = messagingCoreService.parseListener(resource.getContent());
			listenerDefinition.setLocation(getRegistryPath(resource));
			synchronizeListener(listenerDefinition);
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up Listeners...");

		try {
			List<ListenerDefinition> listenerDefinitions = messagingCoreService.getListeners();
			for (ListenerDefinition listenerDefinition : listenerDefinitions) {
				if (!LISTENERS_SYNCHRONIZED.contains(listenerDefinition.getLocation())) {
					messagingCoreService.removeListener(listenerDefinition.getLocation());
					logger.warn("Cleaned up Listener [{}] from location: {}", listenerDefinition.getName(), listenerDefinition.getLocation());
				}
			}
		} catch (MessagingException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up Listeners.");
	}
}
