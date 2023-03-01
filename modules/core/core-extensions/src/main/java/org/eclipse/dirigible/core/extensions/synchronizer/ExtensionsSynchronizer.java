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
package org.eclipse.dirigible.core.extensions.synchronizer;

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
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.artefacts.ExtensionPointSynchronizationArtefactType;
import org.eclipse.dirigible.core.extensions.artefacts.ExtensionSynchronizationArtefactType;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType.ArtefactState;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ExtensionsSynchronizer.
 */
public class ExtensionsSynchronizer extends AbstractSynchronizer {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ExtensionsSynchronizer.class);

	/** The Constant EXTENSION_POINTS_PREDELIVERED. */
	private static final Map<String, ExtensionPointDefinition> EXTENSION_POINTS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, ExtensionPointDefinition>());

	/** The Constant EXTENSIONS_PREDELIVERED. */
	private static final Map<String, ExtensionDefinition> EXTENSIONS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, ExtensionDefinition>());

	/** The Constant EXTENSION_POINTS_SYNCHRONIZED. */
	private static final List<String> EXTENSION_POINTS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	/** The Constant EXTENSIONS_SYNCHRONIZED. */
	private static final List<String> EXTENSIONS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	/** The extensions core service. */
	private ExtensionsCoreService extensionsCoreService = new ExtensionsCoreService();
	
	/** The synchronizer name. */
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();
	
	/** The Constant EXTENSION_ARTEFACT. */
	private static final ExtensionSynchronizationArtefactType EXTENSION_ARTEFACT = new ExtensionSynchronizationArtefactType();
	
	/** The Constant EXTENSION_POINT_ARTEFACT. */
	private static final ExtensionPointSynchronizationArtefactType EXTENSION_POINT_ARTEFACT = new ExtensionPointSynchronizationArtefactType();

	/**
	 * Synchronize.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (ExtensionsSynchronizer.class) {
			if (beforeSynchronizing()) {
				if (logger.isTraceEnabled()) {logger.trace("Synchronizing Extension Points and Extensions...");}
				try {
					if (isSynchronizationEnabled()) {
						startSynchronization(SYNCHRONIZER_NAME);
						clearCache();
						synchronizePredelivered();
						synchronizeRegistry();
						int immutableExtensionPointsCount = EXTENSION_POINTS_PREDELIVERED.size();
						int immutableExtensionsCount = EXTENSIONS_PREDELIVERED.size();
						int mutableExtensionPointsCount = EXTENSION_POINTS_SYNCHRONIZED.size();
						int mutableExtensionsCount = EXTENSIONS_SYNCHRONIZED.size();
						cleanup();
						clearCache();
						successfulSynchronization(SYNCHRONIZER_NAME, format("Immutable Extension Points: {0}, Immutable Extensions: {1}, Mutable Extension Points: {2}, Mutable Extensions: {3}", 
								immutableExtensionPointsCount, immutableExtensionsCount, mutableExtensionPointsCount, mutableExtensionsCount));
					} else {
						if (logger.isDebugEnabled()) {logger.debug("Synchronization has been disabled");}
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {logger.error("Synchronizing process for Extension Points and Extensions failed.", e);}
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						if (logger.isErrorEnabled()) {logger.error("Synchronizing process for Extension Points and Extensions files failed in registering the state log.", e);}
					}
				}
				if (logger.isTraceEnabled()) {logger.trace("Done synchronizing Extension Points and Extensions.");}
				afterSynchronizing();
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		ExtensionsSynchronizer synchronizer = new ExtensionsSynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
	}

	/**
	 * Register pre-delivered extension point.
	 *
	 * @param extensionPointPath
	 *            the extension point path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredExtensionPoint(String extensionPointPath) throws IOException {
		InputStream in = ExtensionsSynchronizer.class.getResourceAsStream("/META-INF/dirigible" + extensionPointPath);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.parseExtensionPoint(json);
			extensionPointDefinition.setLocation(extensionPointPath);
			EXTENSION_POINTS_PREDELIVERED.put(extensionPointPath, extensionPointDefinition);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Register pre-delivered extension.
	 *
	 * @param extensionPath
	 *            the extension path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredExtension(String extensionPath) throws IOException {
		InputStream in = ExtensionsSynchronizer.class.getResourceAsStream("/META-INF/dirigible" + extensionPath);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			ExtensionDefinition extensionDefinition = extensionsCoreService.parseExtension(json);
			extensionDefinition.setLocation(extensionPath);
			EXTENSIONS_PREDELIVERED.put(extensionPath, extensionDefinition);
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
		EXTENSION_POINTS_SYNCHRONIZED.clear();
		EXTENSIONS_SYNCHRONIZED.clear();
	}

	/**
	 * Synchronize predelivered.
	 *
	 * @throws SynchronizationException the synchronization exception
	 */
	private void synchronizePredelivered() throws SynchronizationException {
		if (logger.isTraceEnabled()) {logger.trace("Synchronizing predelivered Extension Points and Extensions...");}
		// Extension Points
		for (ExtensionPointDefinition extensionPointDefinition : EXTENSION_POINTS_PREDELIVERED.values()) {
			synchronizeExtensionPoint(extensionPointDefinition);
		}
		// Extensions
		for (ExtensionDefinition extensionDefinition : EXTENSIONS_PREDELIVERED.values()) {
			synchronizeExtension(extensionDefinition);
		}
		if (logger.isTraceEnabled()) {logger.trace("Done synchronizing predelivered Extension Points and Extensions.");}
	}

	/**
	 * Synchronize extension point.
	 *
	 * @param extensionPointDefinition the extension point definition
	 * @throws SynchronizationException the synchronization exception
	 */
	private void synchronizeExtensionPoint(ExtensionPointDefinition extensionPointDefinition) throws SynchronizationException {
		try {
			if (!extensionsCoreService.existsExtensionPoint(extensionPointDefinition.getLocation())) {
				extensionsCoreService.createExtensionPoint(extensionPointDefinition.getLocation(), extensionPointDefinition.getName(),
						extensionPointDefinition.getDescription());
				if (logger.isInfoEnabled()) {logger.info("Synchronized a new Extension Point [{}] from location: {}", extensionPointDefinition.getName(),
						extensionPointDefinition.getLocation());}
				applyArtefactState(extensionPointDefinition, EXTENSION_POINT_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				ExtensionPointDefinition existing = extensionsCoreService.getExtensionPoint(extensionPointDefinition.getLocation());
				if (!extensionPointDefinition.equals(existing)) {
					extensionsCoreService.updateExtensionPoint(extensionPointDefinition.getLocation(), extensionPointDefinition.getName(),
							extensionPointDefinition.getDescription());
					if (logger.isInfoEnabled()) {logger.info("Synchronized a modified Extension Point [{}] from location: {}", extensionPointDefinition.getName(),
							extensionPointDefinition.getLocation());}
					applyArtefactState(extensionPointDefinition, EXTENSION_POINT_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			EXTENSION_POINTS_SYNCHRONIZED.add(extensionPointDefinition.getLocation());
		} catch (ExtensionsException e) {
			applyArtefactState(extensionPointDefinition, EXTENSION_POINT_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			logProblem(e.getMessage(), ERROR_TYPE, extensionPointDefinition.getLocation(), EXTENSION_POINT_ARTEFACT.getId());
			throw new SynchronizationException(e);
		}
	}

	/**
	 * Synchronize extension.
	 *
	 * @param extensionDefinition the extension definition
	 * @throws SynchronizationException the synchronization exception
	 */
	private void synchronizeExtension(ExtensionDefinition extensionDefinition) throws SynchronizationException {
		try {
			if (!extensionsCoreService.existsExtension(extensionDefinition.getLocation())) {
				extensionsCoreService.createExtension(extensionDefinition.getLocation(), extensionDefinition.getModule(),
						extensionDefinition.getExtensionPoint(), extensionDefinition.getDescription());
				if (logger.isInfoEnabled()) {logger.info("Synchronized a new Extension [{}] for Extension Point [{}] from location: {}", extensionDefinition.getModule(),
						extensionDefinition.getExtensionPoint(), extensionDefinition.getLocation());}
				applyArtefactState(extensionDefinition, EXTENSION_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				ExtensionDefinition existing = extensionsCoreService.getExtension(extensionDefinition.getLocation());
				if (!extensionDefinition.equals(existing)) {
					extensionsCoreService.updateExtension(extensionDefinition.getLocation(), extensionDefinition.getModule(),
							extensionDefinition.getExtensionPoint(), extensionDefinition.getDescription());
					if (logger.isInfoEnabled()) {logger.info("Synchronized a modified Extension [{}] for Extension Point [{}] from location: {}", extensionDefinition.getModule(),
							extensionDefinition.getExtensionPoint(), extensionDefinition.getLocation());}
					applyArtefactState(extensionDefinition, EXTENSION_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			EXTENSIONS_SYNCHRONIZED.add(extensionDefinition.getLocation());
		} catch (ExtensionsException e) {
			applyArtefactState(extensionDefinition, EXTENSION_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			logProblem(e.getMessage(), ERROR_TYPE, extensionDefinition.getLocation(), EXTENSION_ARTEFACT.getId());
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
		if (logger.isTraceEnabled()) {logger.trace("Synchronizing Extension Points and Extensions from Registry...");}

		super.synchronizeRegistry();

		if (logger.isTraceEnabled()) {logger.trace("Done synchronizing Extension Points and Extensions from Registry.");}
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
		if (resourceName.endsWith(IExtensionsCoreService.FILE_EXTENSION_EXTENSIONPOINT)) {
			ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.parseExtensionPoint(resource.getContent());

			extensionPointDefinition.setLocation(getRegistryPath(resource));
			synchronizeExtensionPoint(extensionPointDefinition);
		}

		if (resourceName.endsWith(IExtensionsCoreService.FILE_EXTENSION_EXTENSION)) {
			ExtensionDefinition extensionDefinition = extensionsCoreService.parseExtension(resource.getContent());
			extensionDefinition.setLocation(getRegistryPath(resource));
			synchronizeExtension(extensionDefinition);
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
		if (logger.isTraceEnabled()) {logger.trace("Cleaning up Extension Points and Extensions...");}
		super.cleanup();

		try {
			List<ExtensionPointDefinition> extensionPointDefinitions = extensionsCoreService.getExtensionPoints();
			for (ExtensionPointDefinition extensionPointDefinition : extensionPointDefinitions) {
				if (!EXTENSION_POINTS_SYNCHRONIZED.contains(extensionPointDefinition.getLocation())) {
					extensionsCoreService.removeExtensionPoint(extensionPointDefinition.getLocation());
					if (logger.isWarnEnabled()) {logger.warn("Cleaned up Extension Point [{}] from location: {}", extensionPointDefinition.getName(),
							extensionPointDefinition.getLocation());}
				}
			}

			List<ExtensionDefinition> extensionDefinitions = extensionsCoreService.getExtensions();
			for (ExtensionDefinition extensionDefinition : extensionDefinitions) {
				if (!EXTENSIONS_SYNCHRONIZED.contains(extensionDefinition.getLocation())) {
					extensionsCoreService.removeExtension(extensionDefinition.getLocation());
					if (logger.isWarnEnabled()) {logger.warn("Cleaned up Extension for Module [{}] from location: {}", extensionDefinition.getModule(),
							extensionDefinition.getLocation());}
				}
			}
		} catch (ExtensionsException e) {
			throw new SynchronizationException(e);
		}

		if (logger.isTraceEnabled()) {logger.trace("Done cleaning up Extension Points and Extensions.");}
	}
	
	/** The Constant ERROR_TYPE. */
	private static final String ERROR_TYPE = "EXTENSIONS";
	
	/** The Constant MODULE. */
	private static final String MODULE = "dirigible-core-extensions";
	
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
			ProblemsFacade.save(location, errorType, "", "", errorMessage, "", artifactType, MODULE, ExtensionsSynchronizer.class.getName(), IProblemsConstants.PROGRAM_DEFAULT);
		} catch (ProblemsException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e.getMessage());}
		}
	}
}
