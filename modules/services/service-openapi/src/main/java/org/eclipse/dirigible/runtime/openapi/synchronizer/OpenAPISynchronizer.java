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
package org.eclipse.dirigible.runtime.openapi.synchronizer;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType.ArtefactState;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.runtime.openapi.api.IOpenAPICoreService;
import org.eclipse.dirigible.runtime.openapi.api.OpenAPIException;
import org.eclipse.dirigible.runtime.openapi.artefacts.OpenAPISynchronizationArtefactType;
import org.eclipse.dirigible.runtime.openapi.definition.OpenAPIDefinition;
import org.eclipse.dirigible.runtime.openapi.service.OpenAPICoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class OpenAPISynchronizer.
 */
public class OpenAPISynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(OpenAPISynchronizer.class);

	private static final Map<String, OpenAPIDefinition> OPENAPI_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, OpenAPIDefinition>());

	private static final List<String> OPENAPI_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	private OpenAPICoreService openAPICoreService = new OpenAPICoreService();
	
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();

	private static final OpenAPISynchronizationArtefactType OPENAPI_ARTEFACT = new OpenAPISynchronizationArtefactType();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (OpenAPISynchronizer.class) {
			if (beforeSynchronizing()) {
				logger.trace("Synchronizing OpenAPIs...");
				try {
					if (isSynchronizationEnabled()) {
						startSynchronization(SYNCHRONIZER_NAME);
						clearCache();
						synchronizePredelivered();
						synchronizeRegistry();
						int immutableCount = OPENAPI_PREDELIVERED.size();
						int mutableCount = OPENAPI_SYNCHRONIZED.size();
						cleanup();
						clearCache();
						successfulSynchronization(SYNCHRONIZER_NAME, format("Immutable: {0}, Mutable: {1}", immutableCount, mutableCount));
					} else {
						logger.debug("Synchronization has been disabled");
					}
				} catch (Exception e) {
					logger.error("Synchronizing process for OpenAPIs failed.", e);
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						logger.error("Synchronizing process for OpenAPIs files failed in registering the state log.", e);
					}
				}
				logger.trace("Done synchronizing OpenAPIs.");
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		OpenAPISynchronizer synchronizer = new OpenAPISynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
	}

	/**
	 * Register pre-delivered OpenAPI.
	 *
	 * @param path
	 *            the OpenAPI path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws IOException
	 */
	public void registerPredeliveredOpenAPI(String path) throws IOException {
		InputStream in = OpenAPISynchronizer.class.getResourceAsStream("/META-INF/dirigible" + path);
		try {
			String content = IOUtils.toString(in, StandardCharsets.UTF_8);
			OpenAPIDefinition openAPIDefinition = new OpenAPIDefinition();
			openAPIDefinition.setLocation(path);
			openAPIDefinition.setHash(DigestUtils.md5Hex(content));
			OPENAPI_PREDELIVERED.put(path, openAPIDefinition);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private void clearCache() {
		OPENAPI_SYNCHRONIZED.clear();
	}

	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered OpenAPIs...");
		// OpenAPI
		for (OpenAPIDefinition openAPIDefinition : OPENAPI_PREDELIVERED.values()) {
			synchronizeOpenAPI(openAPIDefinition);
		}
		logger.trace("Done synchronizing predelivered OpenAPIs.");
	}

	private void synchronizeOpenAPI(OpenAPIDefinition openAPIDefinition) throws SynchronizationException {
		try {
			if (!openAPICoreService.existsOpenAPI(openAPIDefinition.getLocation())) {
				openAPICoreService.createOpenAPI(openAPIDefinition.getLocation(), openAPIDefinition.getHash());
				logger.info("Synchronized a new OpenAPI from location: {}", openAPIDefinition.getLocation());
				applyArtefactState(openAPIDefinition, OPENAPI_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				OpenAPIDefinition existing = openAPICoreService.getOpenAPI(openAPIDefinition.getLocation());
				if (!openAPIDefinition.equals(existing)) {
					openAPICoreService.updateOpenAPI(openAPIDefinition.getLocation(), openAPIDefinition.getHash());
					logger.info("Synchronized a modified OpenAPI from location: {}", openAPIDefinition.getLocation());
					applyArtefactState(openAPIDefinition, OPENAPI_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			OPENAPI_SYNCHRONIZED.add(openAPIDefinition.getLocation());
		} catch (OpenAPIException e) {
			applyArtefactState(openAPIDefinition, OPENAPI_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			throw new SynchronizationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing OpenAPI from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing OpenAPI from Registry.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.
	 * repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();

		try {
			if (resourceName.endsWith(IOpenAPICoreService.FILE_EXTENSION_OPENAPI)) {
				String path = getRegistryPath(resource);
				
				OpenAPIDefinition openAPIDefinition = new OpenAPIDefinition();
				openAPIDefinition.setLocation(path);
				openAPIDefinition.setHash(DigestUtils.md5Hex(resource.getContent()));
				synchronizeOpenAPI(openAPIDefinition);
			}
		} catch (RepositoryReadException e) {
			throw new SynchronizationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up OpenAPI...");
		super.cleanup();

		try {
			List<OpenAPIDefinition> openAPIDefinitions = openAPICoreService.getOpenAPIs();
			for (OpenAPIDefinition openAPIDefinition : openAPIDefinitions) {
				if (!OPENAPI_SYNCHRONIZED.contains(openAPIDefinition.getLocation())) {
					openAPICoreService.removeOpenAPI(openAPIDefinition.getLocation());
					logger.warn("Cleaned up OpenAPI from location: {}", openAPIDefinition.getLocation());
				}
			}
		} catch (OpenAPIException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up OpenAPI.");
	}
}
