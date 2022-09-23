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
package org.eclipse.dirigible.engine.wiki.synchronizer;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.eclipse.dirigible.engine.wiki.api.IWikiCoreService;
import org.eclipse.dirigible.engine.wiki.api.WikiException;
import org.eclipse.dirigible.engine.wiki.artefacts.WikiSynchronizationArtefactType;
import org.eclipse.dirigible.engine.wiki.definition.WikiDefinition;
import org.eclipse.dirigible.engine.wiki.processor.WikiEngineExecutor;
import org.eclipse.dirigible.engine.wiki.service.WikiCoreService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class WikiSynchronizer.
 */
public class WikiSynchronizer extends AbstractSynchronizer {

	/** The Constant FILE_EXTENSION_HTML. */
	public static final String FILE_EXTENSION_HTML = ".html";

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(WikiSynchronizer.class);

	/** The Constant WIKI_PREDELIVERED. */
	private static final Map<String, WikiDefinition> WIKI_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, WikiDefinition>());
	
	/** The Constant WIKI_SYNCHRONIZED. */
	private static final List<String> WIKI_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	/** The wiki core service. */
	private WikiCoreService wikiCoreService = new WikiCoreService();
	
	/** The synchronizer name. */
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();

	/** The Constant WIKI_ARTEFACT. */
	private static final WikiSynchronizationArtefactType WIKI_ARTEFACT = new WikiSynchronizationArtefactType();
	
	/** The Constant WIKI_DEFINITIONS. */
	private static final Map<String, WikiDefinition> WIKI_DEFINITIONS = new LinkedHashMap<String, WikiDefinition>();
	
	/** The wiki engine executor. */
	private WikiEngineExecutor wikiEngineExecutor = new WikiEngineExecutor();
	

	/**
	 * Synchronize.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (WikiSynchronizer.class) {
			if (beforeSynchronizing()) {
				if (logger.isTraceEnabled()) {logger.trace("Synchronizing Wiki files...");}
				try {
					if (isSynchronizationEnabled()) {
						startSynchronization(SYNCHRONIZER_NAME);
						clearCache();
						synchronizePredelivered();
						synchronizeRegistry();
						processWikis();
						int immutableCount = WIKI_PREDELIVERED.size();
						int mutableCount = WIKI_SYNCHRONIZED.size();
						cleanup();
						clearCache();
						successfulSynchronization(SYNCHRONIZER_NAME, format("Immutable: {0}, Mutable: {1}", immutableCount, mutableCount));
					} else {
						if (logger.isDebugEnabled()) {logger.debug("Synchronization has been disabled");}
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {logger.error("Synchronizing process for Wiki files failed.", e);}
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						if (logger.isErrorEnabled()) {logger.error("Synchronizing process for Wiki files failed in registering the state log.", e);}
					}
				}
				if (logger.isTraceEnabled()) {logger.trace("Done synchronizing Wiki files.");}
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		WikiSynchronizer synchronizer = new WikiSynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
	}

	/**
	 * Register pre-delivered Wiki.
	 *
	 * @param path            the Wiki path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredWiki(String path) throws IOException {
		InputStream in = WikiSynchronizer.class.getResourceAsStream("/META-INF/dirigible" + path);
		try {
			String content = IOUtils.toString(in, StandardCharsets.UTF_8);
			WikiDefinition wikiDefinition = new WikiDefinition();
			wikiDefinition.setLocation(path);
			wikiDefinition.setHash(DigestUtils.md5Hex(content));
			WIKI_PREDELIVERED.put(path, wikiDefinition);
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
		WIKI_SYNCHRONIZED.clear();
		WIKI_DEFINITIONS.clear();
	}

	/**
	 * Synchronize predelivered.
	 *
	 * @throws SynchronizationException the synchronization exception
	 */
	private void synchronizePredelivered() throws SynchronizationException {
		if (logger.isTraceEnabled()) {logger.trace("Synchronizing predelivered Wiki files...");}
		// Wiki
		for (WikiDefinition wikiDefinition : WIKI_PREDELIVERED.values()) {
			synchronizeWiki(wikiDefinition);
		}
		if (logger.isTraceEnabled()) {logger.trace("Done synchronizing predelivered Wiki files.");}
	}

	/**
	 * Synchronize wiki.
	 *
	 * @param wikiDefinition the wiki definition
	 * @throws SynchronizationException the synchronization exception
	 */
	private void synchronizeWiki(WikiDefinition wikiDefinition) throws SynchronizationException {
		try {
			if (!wikiCoreService.existsWiki(wikiDefinition.getLocation())) {
				wikiCoreService.createWiki(wikiDefinition.getLocation(), wikiDefinition.getHash());
				if (logger.isInfoEnabled()) {logger.info("Synchronized a new Wiki from location: {}", wikiDefinition.getLocation());}
				WIKI_DEFINITIONS.put(wikiDefinition.getLocation(), wikiDefinition);
				applyArtefactState(wikiDefinition, WIKI_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				WikiDefinition existing = wikiCoreService.getWiki(wikiDefinition.getLocation());
				if (!wikiDefinition.equals(existing)) {
					wikiCoreService.updateWiki(wikiDefinition.getLocation(), wikiDefinition.getHash());
					if (logger.isInfoEnabled()) {logger.info("Synchronized a modified Wiki from location: {}", wikiDefinition.getLocation());}
					applyArtefactState(wikiDefinition, WIKI_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
					WIKI_DEFINITIONS.put(wikiDefinition.getLocation(), wikiDefinition);
				}
			}
			WIKI_SYNCHRONIZED.add(wikiDefinition.getLocation());
		} catch (WikiException e) {
			applyArtefactState(wikiDefinition, WIKI_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			logProblem(e.getMessage(), ERROR_TYPE, wikiDefinition.getLocation(), WIKI_ARTEFACT.getId());
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
		if (logger.isTraceEnabled()) {logger.trace("Synchronizing Wiki from Registry...");}

		super.synchronizeRegistry();

		if (logger.isTraceEnabled()) {logger.trace("Done synchronizing Wiki from Registry.");}
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
			if (resourceName.endsWith(IWikiCoreService.FILE_EXTENSION_MARKDOWN)
					|| resourceName.endsWith(IWikiCoreService.FILE_EXTENSION_MD)
					|| resourceName.endsWith(IWikiCoreService.FILE_EXTENSION_CONFLUENCE)) {
				String path = getRegistryPath(resource);
				
				WikiDefinition wikiDefinition = new WikiDefinition();
				wikiDefinition.setLocation(path);
				wikiDefinition.setHash(DigestUtils.md5Hex(resource.getContent()));
				synchronizeWiki(wikiDefinition);
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
		if (logger.isTraceEnabled()) {logger.trace("Cleaning up Wiki files ...");}
		super.cleanup();

		try {
			List<WikiDefinition> wikiDefinitions = wikiCoreService.getWikis();
			for (WikiDefinition wikiDefinition : wikiDefinitions) {
				if (!WIKI_SYNCHRONIZED.contains(wikiDefinition.getLocation())) {
					wikiCoreService.removeWiki(wikiDefinition.getLocation());
					if (logger.isWarnEnabled()) {logger.warn("Cleaned up Wiki from location: {}", wikiDefinition.getLocation());}
				}
			}
		} catch (WikiException e) {
			throw new SynchronizationException(e);
		}

		if (logger.isTraceEnabled()) {logger.trace("Done cleaning up Wiki files.");}
	}
	
	/**
	 * Process wikis.
	 */
	private void processWikis() {
		for (String location : WIKI_DEFINITIONS.keySet()) {
			String path = location;
			if (location.endsWith(IWikiCoreService.FILE_EXTENSION_MARKDOWN)) {
				path = path.substring(0, path.length() - IWikiCoreService.FILE_EXTENSION_MARKDOWN.length());
			} else if (location.endsWith(IWikiCoreService.FILE_EXTENSION_MD)) {
				path = path.substring(0, path.length() - IWikiCoreService.FILE_EXTENSION_MD.length());
			} else if (location.endsWith(IWikiCoreService.FILE_EXTENSION_CONFLUENCE)) {
				path = path.substring(0, path.length() - IWikiCoreService.FILE_EXTENSION_CONFLUENCE.length());
			}
			IRepository repository = getRepository();
			String registryPath = IRepositoryStructure.PATH_REGISTRY_PUBLIC + location;
			if (repository.hasResource(registryPath)) {
				IResource resource = repository.getResource(registryPath);
				String content = new String(resource.getContent(), StandardCharsets.UTF_8);
				String rendered = wikiEngineExecutor.renderContent(location, content);
				path = IRepositoryStructure.PATH_REGISTRY_PUBLIC + path + FILE_EXTENSION_HTML;
				repository.createResource(path, rendered.getBytes());
			} else {
				if (logger.isErrorEnabled()) {logger.error("Wiki file has been deleted" + path);}
			}
		}
	}
	
	/** The Constant ERROR_TYPE. */
	private static final String ERROR_TYPE = "WIKI";
	
	/** The Constant MODULE. */
	private static final String MODULE = "dirigible-engine-wiki";
	
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
			ProblemsFacade.save(location, errorType, "", "", errorMessage, "", artifactType, MODULE, WikiSynchronizer.class.getName(), IProblemsConstants.PROGRAM_DEFAULT);
		} catch (ProblemsException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e.getMessage());}
		}
	}

}
