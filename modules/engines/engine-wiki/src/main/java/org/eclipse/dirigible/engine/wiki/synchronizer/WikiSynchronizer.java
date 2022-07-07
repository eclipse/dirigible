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

	public static final String FILE_EXTENSION_HTML = ".html";

	private static final Logger logger = LoggerFactory.getLogger(WikiSynchronizer.class);

	private static final Map<String, WikiDefinition> WIKI_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, WikiDefinition>());
	
	private static final List<String> WIKI_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	private WikiCoreService wikiCoreService = new WikiCoreService();
	
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();

	private static final WikiSynchronizationArtefactType WIKI_ARTEFACT = new WikiSynchronizationArtefactType();
	
	private static final Map<String, WikiDefinition> WIKI_DEFINITIONS = new LinkedHashMap<String, WikiDefinition>();
	
	private WikiEngineExecutor wikiEngineExecutor = new WikiEngineExecutor();
	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (WikiSynchronizer.class) {
			if (beforeSynchronizing()) {
				logger.trace("Synchronizing Wiki files...");
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
						logger.debug("Synchronization has been disabled");
					}
				} catch (Exception e) {
					logger.error("Synchronizing process for Wiki files failed.", e);
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						logger.error("Synchronizing process for Wiki files failed in registering the state log.", e);
					}
				}
				logger.trace("Done synchronizing Wiki files.");
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
	 * @param path
	 *            the Wiki path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws IOException
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

	private void clearCache() {
		WIKI_SYNCHRONIZED.clear();
		WIKI_DEFINITIONS.clear();
	}

	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered Wiki files...");
		// Wiki
		for (WikiDefinition wikiDefinition : WIKI_PREDELIVERED.values()) {
			synchronizeWiki(wikiDefinition);
		}
		logger.trace("Done synchronizing predelivered Wiki files.");
	}

	private void synchronizeWiki(WikiDefinition wikiDefinition) throws SynchronizationException {
		try {
			if (!wikiCoreService.existsWiki(wikiDefinition.getLocation())) {
				wikiCoreService.createWiki(wikiDefinition.getLocation(), wikiDefinition.getHash());
				logger.info("Synchronized a new Wiki from location: {}", wikiDefinition.getLocation());
				WIKI_DEFINITIONS.put(wikiDefinition.getLocation(), wikiDefinition);
				applyArtefactState(wikiDefinition, WIKI_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				WikiDefinition existing = wikiCoreService.getWiki(wikiDefinition.getLocation());
				if (!wikiDefinition.equals(existing)) {
					wikiCoreService.updateWiki(wikiDefinition.getLocation(), wikiDefinition.getHash());
					logger.info("Synchronized a modified Wiki from location: {}", wikiDefinition.getLocation());
					applyArtefactState(wikiDefinition, WIKI_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
					WIKI_DEFINITIONS.put(wikiDefinition.getLocation(), wikiDefinition);
				}
			}
			WIKI_SYNCHRONIZED.add(wikiDefinition.getLocation());
		} catch (WikiException e) {
			applyArtefactState(wikiDefinition, WIKI_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			throw new SynchronizationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing Wiki from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing Wiki from Registry.");
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up Wiki files ...");
		super.cleanup();

		try {
			List<WikiDefinition> wikiDefinitions = wikiCoreService.getWikis();
			for (WikiDefinition wikiDefinition : wikiDefinitions) {
				if (!WIKI_SYNCHRONIZED.contains(wikiDefinition.getLocation())) {
					wikiCoreService.removeWiki(wikiDefinition.getLocation());
					logger.warn("Cleaned up Wiki from location: {}", wikiDefinition.getLocation());
				}
			}
		} catch (WikiException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up Wiki files.");
	}
	
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
				logger.error("Wiki file has been deleted" + path);
			}
		}
		
	}

}
