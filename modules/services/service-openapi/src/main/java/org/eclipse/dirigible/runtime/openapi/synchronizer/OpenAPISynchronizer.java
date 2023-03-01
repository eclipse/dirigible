/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.openapi.synchronizer;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.api.v3.problems.IProblemsConstants;
import org.eclipse.dirigible.api.v3.problems.ProblemsFacade;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
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

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(OpenAPISynchronizer.class);

	/** The Constant OPENAPI_PREDELIVERED. */
	private static final Map<String, OpenAPIDefinition> OPENAPI_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, OpenAPIDefinition>());

	/** The Constant OPENAPI_SYNCHRONIZED. */
	private static final List<String> OPENAPI_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	/** The open API core service. */
	private OpenAPICoreService openAPICoreService = new OpenAPICoreService();
	
	/** The synchronizer name. */
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();

	/** The Constant OPENAPI_ARTEFACT. */
	private static final OpenAPISynchronizationArtefactType OPENAPI_ARTEFACT = new OpenAPISynchronizationArtefactType();

	/**
	 * Synchronize.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (OpenAPISynchronizer.class) {
			if (beforeSynchronizing()) {
				if (logger.isTraceEnabled()) {logger.trace("Synchronizing OpenAPIs...");}
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
						if (logger.isDebugEnabled()) {logger.debug("Synchronization has been disabled");}
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {logger.error("Synchronizing process for OpenAPIs failed.", e);}
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						if (logger.isErrorEnabled()) {logger.error("Synchronizing process for OpenAPIs files failed in registering the state log.", e);}
					}
				}
				if (logger.isTraceEnabled()) {logger.trace("Done synchronizing OpenAPIs.");}
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
	 * @param path            the OpenAPI path
	 * @throws IOException Signals that an I/O exception has occurred.
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

	/**
	 * Clear cache.
	 */
	private void clearCache() {
		OPENAPI_SYNCHRONIZED.clear();
	}

	/**
	 * Synchronize predelivered.
	 *
	 * @throws SynchronizationException the synchronization exception
	 */
	private void synchronizePredelivered() throws SynchronizationException {
		if (logger.isTraceEnabled()) {logger.trace("Synchronizing predelivered OpenAPIs...");}
		// OpenAPI
		for (OpenAPIDefinition openAPIDefinition : OPENAPI_PREDELIVERED.values()) {
			synchronizeOpenAPI(openAPIDefinition);
		}
		if (logger.isTraceEnabled()) {logger.trace("Done synchronizing predelivered OpenAPIs.");}
	}

	/**
	 * Synchronize open API.
	 *
	 * @param openAPIDefinition the open API definition
	 * @throws SynchronizationException the synchronization exception
	 */
	private void synchronizeOpenAPI(OpenAPIDefinition openAPIDefinition) throws SynchronizationException {
		try {
			if (!openAPICoreService.existsOpenAPI(openAPIDefinition.getLocation())) {
				openAPICoreService.createOpenAPI(openAPIDefinition.getLocation(), openAPIDefinition.getHash());
				if (logger.isInfoEnabled()) {logger.info("Synchronized a new OpenAPI from location: {}", openAPIDefinition.getLocation());}
				applyArtefactState(openAPIDefinition, OPENAPI_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				OpenAPIDefinition existing = openAPICoreService.getOpenAPI(openAPIDefinition.getLocation());
				if (!openAPIDefinition.equals(existing)) {
					openAPICoreService.updateOpenAPI(openAPIDefinition.getLocation(), openAPIDefinition.getHash());
					if (logger.isInfoEnabled()) {logger.info("Synchronized a modified OpenAPI from location: {}", openAPIDefinition.getLocation());}
					applyArtefactState(openAPIDefinition, OPENAPI_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			OPENAPI_SYNCHRONIZED.add(openAPIDefinition.getLocation());
		} catch (OpenAPIException e) {
			applyArtefactState(openAPIDefinition, OPENAPI_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			logProblem(e.getMessage(), ERROR_TYPE, openAPIDefinition.getLocation(), OPENAPI_ARTEFACT.getId());
			throw new SynchronizationException(e);
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
		if (logger.isTraceEnabled()) {logger.trace("Synchronizing OpenAPI from Registry...");}

		super.synchronizeRegistry();

		if (logger.isTraceEnabled()) {logger.trace("Done synchronizing OpenAPI from Registry.");}
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
		if (logger.isTraceEnabled()) {logger.trace("Cleaning up OpenAPI...");}
		super.cleanup();

		try {
			List<OpenAPIDefinition> openAPIDefinitions = openAPICoreService.getOpenAPIs();
			for (OpenAPIDefinition openAPIDefinition : openAPIDefinitions) {
				if (!OPENAPI_SYNCHRONIZED.contains(openAPIDefinition.getLocation())) {
					openAPICoreService.removeOpenAPI(openAPIDefinition.getLocation());
					if (logger.isWarnEnabled()) {logger.warn("Cleaned up OpenAPI from location: {}", openAPIDefinition.getLocation());}
				}
			}
		} catch (OpenAPIException e) {
			throw new SynchronizationException(e);
		}

		if (logger.isTraceEnabled()) {logger.trace("Done cleaning up OpenAPI.");}
	}
	
	/** The Constant ERROR_TYPE. */
	private static final String ERROR_TYPE = "OPENAPI";
	
	/** The Constant MODULE. */
	private static final String MODULE = "dirigible-service-operations";
	
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
			ProblemsFacade.save(location, errorType, "", "", errorMessage, "", artifactType, MODULE, OpenAPISynchronizer.class.getName(), IProblemsConstants.PROGRAM_DEFAULT);
		} catch (ProblemsException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e.getMessage());}
		}
	}
}
