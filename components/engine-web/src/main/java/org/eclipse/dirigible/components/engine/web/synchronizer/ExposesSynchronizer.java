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
package org.eclipse.dirigible.components.engine.web.synchronizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.engine.web.domain.Expose;
import org.eclipse.dirigible.components.engine.web.exposure.ExposeManager;
import org.eclipse.dirigible.components.engine.web.service.ExposeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class ExposesSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(70)
public class ExposesSynchronizer<A extends Artefact> implements Synchronizer<Expose> {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ExposesSynchronizer.class);
	
	/** The Constant FILE_NAME. */
	public static final String FILE_NAME = "project.json";
	
	/** The expose service. */
	private ExposeService exposeService;
	
	/** The synchronization callback. */
	private SynchronizerCallback callback;
	
	/**
	 * Instantiates a new exposes synchronizer.
	 *
	 * @param exposeService the expose service
	 */
	@Autowired
	public ExposesSynchronizer(ExposeService exposeService) {
		this.exposeService = exposeService;
	}
	
	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	@Override
	public ArtefactService<Expose> getService() {
		return exposeService;
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
		return file.toString().endsWith(FILE_NAME);
	}
	
	/**
	 * Checks if is accepted.
	 *
	 * @param type the artefact
	 * @return true, if is accepted
	 */
	@Override
	public boolean isAccepted(String type) {
		return Expose.ARTEFACT_TYPE.equals(type);
	}

	/**
	 * Load.
	 *
	 * @param location the location
	 * @param content the content
	 * @return the list
	 */
	@Override
	public List<Expose> load(String location, byte[] content) {
		Expose expose = JsonHelper.fromJson(new String(content, StandardCharsets.UTF_8), Expose.class);
		Configuration.configureObject(expose);
		expose.setLocation(location);
		expose.setName(expose.getGuid());
		expose.setType(Expose.ARTEFACT_TYPE);
		expose.updateKey();
		try {
			Expose maybe = getService().findByKey(expose.getKey());
			if (maybe != null) {
				expose.setId(maybe.getId());
			}
			getService().save(expose);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			if (logger.isErrorEnabled()) {logger.error("expose: {}", expose);}
			if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
		}
		return List.of(expose);
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
		Expose expose = null;
		if (wrapper.getArtefact() instanceof Expose) {
			expose = (Expose) wrapper.getArtefact();
		} else {
			throw new UnsupportedOperationException(String.format("Trying to process %s as Expose", wrapper.getArtefact().getClass()));
		}
		
		if (!ExposeManager.existExposableProject(expose.getName())) {
			try {
				if (expose.getExposes() != null) {
					ExposeManager.registerExposableProject(expose.getName(), expose.getExposes());
					callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE);
				} else {
					if (logger.isTraceEnabled()) {logger.trace(expose.getName() + " skipped due to lack of exposures");}
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
				callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.FAILED_CREATE);
			}
		}
		return true;
	}

	/**
	 * Cleanup.
	 *
	 * @param expose the expose
	 */
	@Override
	public void cleanup(Expose expose) {
		try {
			ExposeManager.unregisterProject(expose.getName());
			getService().delete(expose);
			callback.registerState(this, expose, ArtefactLifecycle.DELETED.toString(), ArtefactState.SUCCESSFUL_DELETE);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
			callback.registerState(this, expose, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED_DELETE);
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
		return FILE_NAME;
	}

	/**
	 * Gets the artefact type.
	 *
	 * @return the artefact type
	 */
	@Override
	public String getArtefactType() {
		return Expose.ARTEFACT_TYPE;
	}

}
