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
package org.eclipse.dirigible.engine.web.synchronizer;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.api.v3.problems.IProblemsConstants;
import org.eclipse.dirigible.api.v3.problems.ProblemsFacade;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType.ArtefactState;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.engine.web.api.IWebCoreService;
import org.eclipse.dirigible.engine.web.api.WebCoreException;
import org.eclipse.dirigible.engine.web.artefacts.WebSynchronizationArtefactType;
import org.eclipse.dirigible.engine.web.models.WebModel;
import org.eclipse.dirigible.engine.web.processor.WebExposureManager;
import org.eclipse.dirigible.engine.web.service.WebCoreService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class WebSynchronizer.
 */
public class WebSynchronizer extends AbstractSynchronizer {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(WebSynchronizer.class);

	/** The Constant WEB_PREDELIVERED. */
	private static final Map<String, WebModel> WEB_PREDELIVERED = Collections.synchronizedMap(new HashMap<String, WebModel>());

	/** The Constant WEB_SYNCHRONIZED. */
	private static final List<String> WEB_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	/** The web core service. */
	private WebCoreService webCoreService = new WebCoreService();

	/** The synchronizer name. */
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();

	/** The Constant WEB_ARTEFACT. */
	private static final WebSynchronizationArtefactType WEB_ARTEFACT = new WebSynchronizationArtefactType();

	/**
	 * Synchronize.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (WebSynchronizer.class) {
			if (beforeSynchronizing()) {
				if (logger.isTraceEnabled()) {logger.trace("Synchronizing Web...");}
				try {
					if (isSynchronizationEnabled()) {
						startSynchronization(SYNCHRONIZER_NAME);
						clearCache();
						synchronizePredelivered();
						synchronizeRegistry();
						updateWebExposures();
						int immutableCount = WEB_PREDELIVERED.size();
						int mutableCount = WEB_SYNCHRONIZED.size();
						cleanup();
						clearCache();
						successfulSynchronization(SYNCHRONIZER_NAME, format("Immutable: {0}, Mutable: {1}", immutableCount, mutableCount));
					} else {
						if (logger.isDebugEnabled()) {logger.debug("Synchronization has been disabled");}
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {logger.error("Synchronizing process for Web failed.", e);}
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						if (logger.isErrorEnabled()) {logger.error("Synchronizing process for Web files failed in registering the state log.", e);}
					}
				}
				if (logger.isTraceEnabled()) {logger.trace("Done synchronizing Webs.");}
				afterSynchronizing();
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		WebSynchronizer synchronizer = new WebSynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
	}

	/**
	 * Register predelivered web.
	 *
	 * @param webPath
	 *            the web path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredProject(String webPath) throws IOException {
		InputStream in = WebSynchronizer.class.getResourceAsStream("/META-INF/dirigible" + webPath);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			WebModel webModel = webCoreService.parseProject(webPath, json);
			WEB_PREDELIVERED.put(webPath, webModel);
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
		if (logger.isTraceEnabled()) {logger.trace("Synchronizing Webs from Registry...");}

		super.synchronizeRegistry();

		if (logger.isTraceEnabled()) {logger.trace("Done synchronizing Webs from Registry.");}
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
		if (resourceName.equals(IWebCoreService.FILE_PROJECT_JSON)) {
			String path = getRegistryPath(resource);
			WebModel webModel = webCoreService.parseWeb(path, resource.getContent());
			synchronizeWeb(webModel);
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
		if (logger.isTraceEnabled()) {logger.trace("Cleaning up Webs...");}
		super.cleanup();

		try {
			List<WebModel> webModels = webCoreService.getWebs();
			for (WebModel webModel : webModels) {
				if (!WEB_SYNCHRONIZED.contains(webModel.getGuid())) {
					webCoreService.removeWeb(webModel.getGuid());
					if (logger.isWarnEnabled()) {logger.warn("Cleaned up Web [{}]", webModel.getGuid());}
				}
			}
		} catch (WebCoreException e) {
			throw new SynchronizationException(e);
		}

		if (logger.isTraceEnabled()) {logger.trace("Done cleaning up Webs.");}
	}

	/**
	 * Update web exposures.
	 *
	 * @throws SchedulerException the scheduler exception
	 */
	private void updateWebExposures() throws SchedulerException {
		if (logger.isTraceEnabled()) {logger.trace("Start Web Registering...");}

		for (String webName : WEB_SYNCHRONIZED) {
			if (!WebExposureManager.existExposableProject(webName)) {
				WebModel webModel = null;
				try {
					webModel = webCoreService.getWebByName(webName);
					if (webModel.getExposes() != null) {
						WebExposureManager.registerExposableProject(webName, webModel.getExposes());
						applyArtefactState(webModel, WEB_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
					} else {
						if (logger.isTraceEnabled()) {logger.trace(webName + " skipped due to lack of exposures");}
					}
				} catch (WebCoreException e) {
					if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
					applyArtefactState(webModel, WEB_ARTEFACT, ArtefactState.FAILED_CREATE, e.getMessage());
				}
			}
		}

		Set<String> registerdProjects = WebExposureManager.listRegisteredProjects();
		for (String registeredProject : registerdProjects) {
			WebModel webModel = null;
			try {
				if (!WEB_SYNCHRONIZED.contains(registeredProject)) {
					webModel = new WebModel();
					webModel.setLocation(IRepository.SEPARATOR + registeredProject + IRepository.SEPARATOR + "project.json");
					webModel.setGuid(registeredProject);
					webCoreService.removeWeb(webModel.getLocation());
					WebExposureManager.unregisterProject(registeredProject);
					applyArtefactState(webModel, WEB_ARTEFACT, ArtefactState.SUCCESSFUL_DELETE);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
				applyArtefactState(webModel, WEB_ARTEFACT, ArtefactState.FAILED_DELETE, e.getMessage());
			}
		}

		if (logger.isTraceEnabled()) {logger.trace("Registered Projects: " + registerdProjects.size());}
		if (logger.isTraceEnabled()) {logger.trace("Done registering Projects.");}
	}

	/**
	 * Clear cache.
	 */
	private void clearCache() {
		WEB_SYNCHRONIZED.clear();
	}

	/**
	 * Synchronize predelivered.
	 *
	 * @throws SynchronizationException the synchronization exception
	 */
	private void synchronizePredelivered() throws SynchronizationException {
		if (logger.isTraceEnabled()) {logger.trace("Synchronizing predelivered Webs...");}
		// Webs
		for (WebModel webModel : WEB_PREDELIVERED.values()) {
			synchronizeWeb(webModel);
		}
		if (logger.isTraceEnabled()) {logger.trace("Done synchronizing predelivered Jobs.");}
	}

	/**
	 * Synchronize web.
	 *
	 * @param webModel the web model
	 * @throws SynchronizationException the synchronization exception
	 */
	private void synchronizeWeb(WebModel webModel) throws SynchronizationException {
		try {
			if (!webCoreService.existsWeb(webModel.getLocation())) {
				webCoreService.createWeb(webModel.getLocation(), webModel.getGuid(), webModel.getExposed(), webModel.getHash());
				if (logger.isInfoEnabled()) {logger.info("Synchronized a new Web [{}]", webModel.getLocation());}
				applyArtefactState(webModel, WEB_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				WebModel existing = webCoreService.getWeb(webModel.getLocation());
				if (!webModel.equals(existing)) {
					webCoreService.updateWeb(webModel.getLocation(), webModel.getGuid(), webModel.getExposed(), webModel.getHash());
					if (logger.isInfoEnabled()) {logger.info("Synchronized a modified Web [{}]", webModel.getLocation());}
					applyArtefactState(webModel, WEB_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			WEB_SYNCHRONIZED.add(webModel.getGuid());
		} catch (WebCoreException e) {
			logProblem(e.getMessage(), ERROR_TYPE, webModel.getLocation(), WEB_ARTEFACT.getId());
			throw new SynchronizationException(e);
		}
	}
	
	/** The Constant ERROR_TYPE. */
	private static final String ERROR_TYPE = "WEB";
	
	/** The Constant MODULE. */
	private static final String MODULE = "dirigible-core-security";
	
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
			ProblemsFacade.save(location, errorType, "", "", errorMessage, "", artifactType, MODULE, WebSynchronizer.class.getName(), IProblemsConstants.PROGRAM_DEFAULT);
		} catch (ProblemsException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e.getMessage());}
		}
	}

}
