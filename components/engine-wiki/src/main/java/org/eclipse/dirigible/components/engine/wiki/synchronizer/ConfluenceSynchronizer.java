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
package org.eclipse.dirigible.components.engine.wiki.synchronizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.engine.wiki.domain.Confluence;
import org.eclipse.dirigible.components.engine.wiki.service.ConfluenceService;
import org.eclipse.dirigible.components.engine.wiki.service.WikiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class ConfluenceSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(70)
public class ConfluenceSynchronizer<A extends Artefact> implements Synchronizer<Confluence> {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ConfluenceSynchronizer.class);
	
	/**
     * The Constant FILE_EXTENSION_CONFLUENCE.
     */
    private static final String FILE_EXTENSION_CONFLUENCE = ".confluence";
	
	/** The confluence service. */
	private ConfluenceService confluenceService;
	
	/** The wiki service. */
	private WikiService wikiService;
	
	/** The synchronization callback. */
	private SynchronizerCallback callback;
	
	/**
	 * Instantiates a new confluence synchronizer.
	 *
	 * @param confluenceService the confluence service
	 */
	@Autowired
	public ConfluenceSynchronizer(ConfluenceService confluenceService, WikiService wikiService) {
		this.confluenceService = confluenceService;
		this.wikiService = wikiService;
	}
	
	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	@Override
	public ArtefactService<Confluence> getService() {
		return confluenceService;
	}
	
	public WikiService getWikiService() {
		return wikiService;
	}

	/**
	 * Checks if is accepted.
	 *
	 * @param file the file
	 * @param attrs the attrs
	 * @return true, if is accepted
	 */
	@Override
	public boolean isAccepted(Path file, BasicFileAttributes attrs) {
		return file.toString().endsWith(FILE_EXTENSION_CONFLUENCE);
	}
	
	/**
	 * Checks if is accepted.
	 *
	 * @param type the artefact
	 * @return true, if is accepted
	 */
	@Override
	public boolean isAccepted(String type) {
		return Confluence.ARTEFACT_TYPE.equals(type);
	}

	/**
	 * Load.
	 *
	 * @param location the location
	 * @param content the content
	 * @return the list
	 */
	@Override
	public List<Confluence> load(String location, byte[] content) {
		Confluence wiki = new Confluence();
		Configuration.configureObject(wiki);
		wiki.setLocation(location);
		wiki.setName(Paths.get(location).getFileName().toString());
		wiki.setType(Confluence.ARTEFACT_TYPE);
		wiki.updateKey();
		wiki.setContent(content);
		try {
			Confluence maybe = getService().findByKey(wiki.getKey());
			if (maybe != null) {
				wiki.setId(maybe.getId());
			}
			getService().save(wiki);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			if (logger.isErrorEnabled()) {logger.error("wiki: {}", wiki);}
			if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
		}
		return List.of(wiki);
	}
	
	/**
	 * Prepare.
	 *
	 * @param wrappers the wrappers
	 * @param depleter the depleter
	 */
	@Override
	public void prepare(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
	}
	
	/**
	 * Process.
	 *
	 * @param wrappers the wrappers
	 * @param depleter the depleter
	 */
	@Override
	public void process(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
		try {
			List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, ArtefactLifecycle.CREATED.toString());
			callback.registerErrors(this, results, ArtefactLifecycle.CREATED.toString(), ArtefactState.FAILED_CREATE_UPDATE);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
		}
	}
	
	/**
	 * Complete.
	 *
	 * @param wrapper the wrapper
	 * @param flow the flow
	 * @return true, if successful
	 */
	@Override
	public boolean complete(TopologyWrapper<Artefact> wrapper, String flow) {
		Confluence wiki = null;
		if (wrapper.getArtefact() instanceof Confluence) {
			wiki = (Confluence) wrapper.getArtefact();
		} else {
			throw new UnsupportedOperationException(String.format("Trying to process %s as BPMN", wrapper.getArtefact().getClass()));
		}
		
		wikiService.generateContent(wiki.getLocation(), new String(wiki.getContent(), StandardCharsets.UTF_8));
		
		callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE_UPDATE);
		return true;
	}

	/**
	 * Cleanup.
	 *
	 * @param confluence the confluence
	 */
	@Override
	public void cleanup(Confluence wiki) {
		try {
			getService().delete(wiki);
			
			wikiService.removeGenerated(wiki.getLocation());
			
			callback.registerState(this, wiki, ArtefactLifecycle.DELETED.toString(), ArtefactState.SUCCESSFUL_DELETE);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
			callback.registerState(this, wiki, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED_DELETE);
		}
	}
	
	/**
	 * Sets the callback.
	 *
	 * @param callback the new callback
	 */
	@Override
	public void setCallback(SynchronizerCallback callback) {
		this.callback = callback;
	}
	
	/**
	 * Gets the file extension.
	 *
	 * @return the file extension
	 */
	@Override
	public String getFileExtension() {
		return FILE_EXTENSION_CONFLUENCE;
	}

	/**
	 * Gets the artefact type.
	 *
	 * @return the artefact type
	 */
	@Override
	public String getArtefactType() {
		return Confluence.ARTEFACT_TYPE;
	}

}
