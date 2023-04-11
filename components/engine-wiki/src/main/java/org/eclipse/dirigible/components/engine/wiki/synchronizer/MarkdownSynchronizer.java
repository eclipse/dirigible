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
package org.eclipse.dirigible.components.engine.wiki.synchronizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.engine.wiki.domain.Markdown;
import org.eclipse.dirigible.components.engine.wiki.service.MarkdownService;
import org.eclipse.dirigible.components.engine.wiki.service.WikiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class MarkdownSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(70)
public class MarkdownSynchronizer<A extends Artefact> implements Synchronizer<Markdown> {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(MarkdownSynchronizer.class);
	
	/**
     * The Constant FILE_EXTENSION_MARKDOWN.
     */
    private static final String FILE_EXTENSION_MARKDOWN = ".md";
    
	/** The markdown service. */
	private MarkdownService markdownService;
	
	/** The wiki service. */
	private WikiService wikiService;
	
	/** The synchronization callback. */
	private SynchronizerCallback callback;
	
	/**
	 * Instantiates a new markdown synchronizer.
	 *
	 * @param markdownService the markdown service
	 * @param wikiService the wiki service
	 */
	@Autowired
	public MarkdownSynchronizer(MarkdownService markdownService, WikiService wikiService) {
		this.markdownService = markdownService;
		this.wikiService = wikiService;
	}
	
	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	@Override
	public ArtefactService<Markdown> getService() {
		return markdownService;
	}
	
	/**
	 * Gets the wiki service.
	 *
	 * @return the wiki service
	 */
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
		return file.toString().endsWith(FILE_EXTENSION_MARKDOWN);
	}
	
	/**
	 * Checks if is accepted.
	 *
	 * @param type the artefact
	 * @return true, if is accepted
	 */
	@Override
	public boolean isAccepted(String type) {
		return Markdown.ARTEFACT_TYPE.equals(type);
	}

	/**
	 * Load.
	 *
	 * @param location the location
	 * @param content the content
	 * @return the list
	 */
	@Override
	public List<Markdown> parse(String location, byte[] content) {
		Markdown wiki = new Markdown();
		Configuration.configureObject(wiki);
		wiki.setLocation(location);
		wiki.setName(Paths.get(location).getFileName().toString());
		wiki.setType(Markdown.ARTEFACT_TYPE);
		wiki.updateKey();
		wiki.setContent(content);
		try {
			Markdown maybe = getService().findByKey(wiki.getKey());
			if (maybe != null) {
				wiki.setId(maybe.getId());
			}
			wiki = getService().save(wiki);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			if (logger.isErrorEnabled()) {logger.error("wiki: {}", wiki);}
			if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
		}
		return List.of(wiki);
	}
	
	/**
	 * Retrieve.
	 *
	 * @param location the location
	 * @return the list
	 */
	@Override
	public List<Markdown> retrieve(String location) {
		return getService().getAll();
	}
	
	/**
	 * Sets the status.
	 *
	 * @param artefact the artefact
	 * @param lifecycle the lifecycle
	 * @param error the error
	 */
	@Override
	public void setStatus(Artefact artefact, ArtefactLifecycle lifecycle, String error) {
		artefact.setLifecycle(lifecycle);
		artefact.setError(error);
		getService().save((Markdown) artefact);
	}
	
	/**
	 * Complete.
	 *
	 * @param wrapper the wrapper
	 * @param flow the flow
	 * @return true, if successful
	 */
	@Override
	public boolean complete(TopologyWrapper<Artefact> wrapper, ArtefactPhase flow) {
		Markdown wiki = null;
		if (wrapper.getArtefact() instanceof Markdown) {
			wiki = (Markdown) wrapper.getArtefact();
		} else {
			throw new UnsupportedOperationException(String.format("Trying to process %s as Markdown", wrapper.getArtefact().getClass()));
		}
		
		switch (flow) {
		case CREATE:
			if (wiki.getLifecycle().equals(ArtefactLifecycle.NEW)) {
				wikiService.generateContent(wiki.getLocation(), new String(wiki.getContent(), StandardCharsets.UTF_8));
				callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
			}
			break;
		case UPDATE:
			if (wiki.getLifecycle().equals(ArtefactLifecycle.MODIFIED)) {
				wikiService.generateContent(wiki.getLocation(), new String(wiki.getContent(), StandardCharsets.UTF_8));
				callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED, "");
			}
			break;
		case DELETE:
		case START:
		case STOP:
		}
		
		return true;
	}

	/**
	 * Cleanup.
	 *
	 * @param wiki the wiki
	 */
	@Override
	public void cleanup(Markdown wiki) {
		try {
			wikiService.removeGenerated(wiki.getLocation());
			getService().delete(wiki);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
			callback.registerState(this, wiki, ArtefactLifecycle.DELETED, e.getMessage());
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
		return FILE_EXTENSION_MARKDOWN;
	}

	/**
	 * Gets the artefact type.
	 *
	 * @return the artefact type
	 */
	@Override
	public String getArtefactType() {
		return Markdown.ARTEFACT_TYPE;
	}

}
