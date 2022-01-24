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
package org.eclipse.dirigible.core.websockets.synchronizer;

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
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType.ArtefactState;
import org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService;
import org.eclipse.dirigible.core.websockets.api.WebsocketsException;
import org.eclipse.dirigible.core.websockets.artefacts.WebsocketSynchronizationArtefactType;
import org.eclipse.dirigible.core.websockets.definition.WebsocketDefinition;
import org.eclipse.dirigible.core.websockets.service.WebsocketsCoreService;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ExtensionsSynchronizer.
 */
public class WebsocketsSynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(WebsocketsSynchronizer.class);

	private static final Map<String, WebsocketDefinition> WEBSOCKETS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, WebsocketDefinition>());

	private static final List<String> WEBSOCKETS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	private WebsocketsCoreService websocketsCoreService = new WebsocketsCoreService();
	
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();

	private static final WebsocketSynchronizationArtefactType WEBSOCKET_ARTEFACT = new WebsocketSynchronizationArtefactType();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (WebsocketsSynchronizer.class) {
			if (beforeSynchronizing()) {
				logger.trace("Synchronizing Websockets...");
				try {
					if (isSynchronizationEnabled()) {
						startSynchronization(SYNCHRONIZER_NAME);
						clearCache();
						synchronizePredelivered();
						synchronizeRegistry();
						int immutableCount = WEBSOCKETS_PREDELIVERED.size();
						int mutableCount = WEBSOCKETS_SYNCHRONIZED.size();
						cleanup();
						clearCache();
						successfulSynchronization(SYNCHRONIZER_NAME, format("Immutable: {0}, Mutable: {1}", immutableCount, mutableCount));
					} else {
						logger.debug("Synchronization has been disabled");
					}
				} catch (Exception e) {
					logger.error("Synchronizing process for Websockets failed.", e);
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						logger.error("Synchronizing process for Websockets files failed in registering the state log.", e);
					}
				}
				logger.trace("Done synchronizing Websockets.");
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		WebsocketsSynchronizer synchronizer = new WebsocketsSynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
	}

	/**
	 * Register pre-delivered websocket.
	 *
	 * @param websocketPath
	 *            the websockets path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredWebsocket(String websocketPath) throws IOException {
		InputStream in = WebsocketsSynchronizer.class.getResourceAsStream("/META-INF/dirigible" + websocketPath);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			WebsocketDefinition websocketDefinition = websocketsCoreService.parseWebsocket(json);
			websocketDefinition.setLocation(websocketPath);
			WEBSOCKETS_PREDELIVERED.put(websocketPath, websocketDefinition);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private void clearCache() {
		WEBSOCKETS_SYNCHRONIZED.clear();
	}

	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered Websockets...");
		// Websockets
		for (WebsocketDefinition websocketDefinition : WEBSOCKETS_PREDELIVERED.values()) {
			synchronizeWebsocket(websocketDefinition);
		}
		logger.trace("Done synchronizing predelivered Websockets.");
	}

	private void synchronizeWebsocket(WebsocketDefinition websocketDefinition) throws SynchronizationException {
		try {
			if (!websocketsCoreService.existsWebsocket(websocketDefinition.getLocation())) {
				websocketsCoreService.createWebsocket(websocketDefinition.getLocation(), websocketDefinition.getHandler(),
						websocketDefinition.getEndpoint(), websocketDefinition.getDescription());
				logger.info("Synchronized a new Websocket [{}] with Endpoint [{}] from location: {}", websocketDefinition.getHandler(),
						websocketDefinition.getEndpoint(), websocketDefinition.getLocation());
				applyArtefactState(websocketDefinition, WEBSOCKET_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				WebsocketDefinition existing = websocketsCoreService.getWebsocket(websocketDefinition.getLocation());
				if (!websocketDefinition.equals(existing)) {
					websocketsCoreService.updateWebsocket(websocketDefinition.getLocation(), websocketDefinition.getHandler(),
							websocketDefinition.getEndpoint(), websocketDefinition.getDescription());
					logger.info("Synchronized a modified Extension [{}] for Extension Point [{}] from location: {}", websocketDefinition.getHandler(),
							websocketDefinition.getEndpoint(), websocketDefinition.getLocation());
					applyArtefactState(websocketDefinition, WEBSOCKET_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			WEBSOCKETS_SYNCHRONIZED.add(websocketDefinition.getLocation());
		} catch (WebsocketsException e) {
			applyArtefactState(websocketDefinition, WEBSOCKET_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			throw new SynchronizationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing Websockets from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing Websockets from Registry.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.
	 * repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();

		if (resourceName.endsWith(IWebsocketsCoreService.FILE_EXTENSION_WEBSOCKET)) {
			WebsocketDefinition websocketDefinition = websocketsCoreService.parseWebsocket(resource.getContent());
			websocketDefinition.setLocation(getRegistryPath(resource));
			synchronizeWebsocket(websocketDefinition);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up Extension Points and Extensions...");
		super.cleanup();

		try {
			List<WebsocketDefinition> websocketDefinitions = websocketsCoreService.getWebsockets();
			for (WebsocketDefinition websocketDefinition : websocketDefinitions) {
				if (!WEBSOCKETS_SYNCHRONIZED.contains(websocketDefinition.getLocation())) {
					websocketsCoreService.removeWebsocket(websocketDefinition.getLocation());
					logger.warn("Cleaned up Extension for Module [{}] from location: {}", websocketDefinition.getHandler(),
							websocketDefinition.getLocation());
				}
			}
		} catch (WebsocketsException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up Extension Points and Extensions.");
	}
}
