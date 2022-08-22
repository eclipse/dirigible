/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.messaging.synchronizer;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.api.v3.problems.IProblemsConstants;
import org.eclipse.dirigible.api.v3.problems.ProblemsFacade;
import org.eclipse.dirigible.core.messaging.api.IMessagingCoreService;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;
import org.eclipse.dirigible.core.messaging.service.MessagingCoreService;
import org.eclipse.dirigible.core.messaging.service.SchedulerManager;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType.ArtefactState;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.engine.messaging.artefacts.ListenerSynchronizationArtefactType;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MessagingSynchronizer.
 */
public class MessagingSynchronizer extends AbstractSynchronizer {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(MessagingSynchronizer.class);

	/** The Constant LISTENERS_PREDELIVERED. */
	private static final Map<String, ListenerDefinition> LISTENERS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, ListenerDefinition>());

	/** The Constant LISTENERS_SYNCHRONIZED. */
	private static final List<String> LISTENERS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());
	
	/** The Constant LISTENERS_MODIFIED. */
	private static final List<String> LISTENERS_MODIFIED = Collections.synchronizedList(new ArrayList<String>());

	/** The messaging core service. */
	private MessagingCoreService messagingCoreService = new MessagingCoreService();

	/** The messaging manager. */
	private SchedulerManager messagingManager = new SchedulerManager();
	
	/** The synchronizer name. */
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();

	/** The Constant LISTENER_ARTEFACT. */
	private static final ListenerSynchronizationArtefactType LISTENER_ARTEFACT = new ListenerSynchronizationArtefactType();

	/**
	 * Synchronize.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (MessagingSynchronizer.class) {
			if (beforeSynchronizing()) {
				logger.trace("Synchronizing Listeners...");
				try {
					if (isSynchronizationEnabled()) {
						startSynchronization(SYNCHRONIZER_NAME);
						clearCache();
						synchronizePredelivered();
						synchronizeRegistry();
						startListeners();
						int immutableCount = LISTENERS_PREDELIVERED.size();
						int mutableCount = LISTENERS_SYNCHRONIZED.size();
						cleanup();
						clearCache();
						successfulSynchronization(SYNCHRONIZER_NAME, format("Immutable: {0}, Mutable: {1}", immutableCount, mutableCount));
					} else {
						logger.debug("Synchronization has been disabled");
					}
				} catch (Exception e) {
					logger.error("Synchronizing process for Listeners failed.", e);
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						logger.error("Synchronizing process for Listeners files failed in registering the state log.", e);
					}
				}
				logger.trace("Done synchronizing Listeners.");
				afterSynchronizing();
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		MessagingSynchronizer synchronizer = new MessagingSynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
	}

	/**
	 * Register predelivered listener.
	 *
	 * @param listenerPath
	 *            the listener path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredListener(String listenerPath) throws IOException {
		InputStream in = MessagingSynchronizer.class.getResourceAsStream("/META-INF/dirigible" + listenerPath);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			ListenerDefinition listenerDefinition = messagingCoreService.parseListener(json);
			listenerDefinition.setLocation(listenerPath);
			LISTENERS_PREDELIVERED.put(listenerPath, listenerDefinition);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Synchronize registry.
	 *
	 * @throws SynchronizationException the synchronization exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing Listeners from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing Listeners from Registry.");
	}

	/**
	 * Synchronize resource.
	 *
	 * @param resource the resource
	 * @throws SynchronizationException the synchronization exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.
	 * repository.api.IResource)
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

	/**
	 * Cleanup.
	 *
	 * @throws SynchronizationException the synchronization exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up Listeners...");
		super.cleanup();

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

	/**
	 * Start listeners.
	 */
	private void startListeners() {
		logger.trace("Start Listeners...");
		
		// Stop modified listeners first
		for (String listenerLocation : LISTENERS_MODIFIED) {
			if (messagingManager.existsListener(listenerLocation)) {
				ListenerDefinition listenerDefinition = null;
				try {
					listenerDefinition = messagingCoreService.getListener(listenerLocation);
					messagingManager.stopListener(listenerDefinition);
					applyArtefactState(listenerDefinition, LISTENER_ARTEFACT, ArtefactState.SUCCESSFUL_DELETE);
				} catch (MessagingException e) {
					logger.error(e.getMessage(), e);
					applyArtefactState(listenerDefinition, LISTENER_ARTEFACT, ArtefactState.FAILED_DELETE, e.getMessage());
				}
			}
		}

		// Start all the synchronized listeners (if not started)
		for (String listenerLocation : LISTENERS_SYNCHRONIZED) {
			if (!messagingManager.existsListener(listenerLocation)) {
				ListenerDefinition listenerDefinition = null;
				try {
					listenerDefinition = messagingCoreService.getListener(listenerLocation);
					messagingManager.startListener(listenerDefinition);
					applyArtefactState(listenerDefinition, LISTENER_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
				} catch (MessagingException e) {
					logger.error(e.getMessage(), e);
					applyArtefactState(listenerDefinition, LISTENER_ARTEFACT, ArtefactState.FAILED_CREATE, e.getMessage());
				}
			}
		}

		// Stop all the running listeners that are not available in the last synchronization
		List<String> runningListeners = messagingManager.getRunningListeners();
		for (String listenerLocation : runningListeners) {
			ListenerDefinition listenerDefinition = null;
			try {
				if (!LISTENERS_SYNCHRONIZED.contains(listenerLocation)) {
					listenerDefinition = messagingCoreService.getListener(listenerLocation);
					messagingManager.stopListener(listenerDefinition);
					applyArtefactState(listenerDefinition, LISTENER_ARTEFACT, ArtefactState.SUCCESSFUL_DELETE);
				}
			} catch (MessagingException e) {
				logger.error(e.getMessage(), e);
				applyArtefactState(listenerDefinition, LISTENER_ARTEFACT, ArtefactState.FAILED_DELETE, e.getMessage());
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
		LISTENERS_MODIFIED.clear();
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
				messagingCoreService.createListener(listenerDefinition.getLocation(), listenerDefinition.getName(), listenerDefinition.getType(),
						listenerDefinition.getHandler(), listenerDefinition.getDescription());
				logger.info("Synchronized a new Listener [{}] from location: {}", listenerDefinition.getName(), listenerDefinition.getLocation());
				applyArtefactState(listenerDefinition, LISTENER_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				ListenerDefinition existing = messagingCoreService.getListener(listenerDefinition.getLocation());
				if (!listenerDefinition.equals(existing)) {
					messagingCoreService.updateListener(listenerDefinition.getLocation(), listenerDefinition.getName(), listenerDefinition.getType(),
							listenerDefinition.getHandler(), listenerDefinition.getDescription());
					logger.info("Synchronized a modified Listener [{}] from location: {}", listenerDefinition.getName(),
							listenerDefinition.getLocation());
					applyArtefactState(listenerDefinition, LISTENER_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
					LISTENERS_MODIFIED.add(listenerDefinition.getLocation());
				}
			}
			LISTENERS_SYNCHRONIZED.add(listenerDefinition.getLocation());
		} catch (MessagingException e) {
			applyArtefactState(listenerDefinition, LISTENER_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			logProblem(e.getMessage(), ERROR_TYPE, listenerDefinition.getLocation(), LISTENER_ARTEFACT.getId());
			throw new SynchronizationException(e);
		}
	}
	
	/** The Constant ERROR_TYPE. */
	private static final String ERROR_TYPE = "LISTENER";
	
	/** The Constant MODULE. */
	private static final String MODULE = "dirigible-engine-listener";
	
	/**
	 * Use to log problem from artifact processing.
	 *
	 * @param errorMessage the error message
	 * @param errorType the error type
	 * @param location the location
	 * @param artifactType the artifact type
	 */
	private static void logProblem(String errorMessage, String errorType, String location, String artifactType) {
		try {
			ProblemsFacade.save(location, errorType, "", "", errorMessage, "", artifactType, MODULE, MessagingSynchronizer.class.getName(), IProblemsConstants.PROGRAM_DEFAULT);
		} catch (ProblemsException e) {
			logger.error(e.getMessage(), e.getMessage());
		}
	}

}
