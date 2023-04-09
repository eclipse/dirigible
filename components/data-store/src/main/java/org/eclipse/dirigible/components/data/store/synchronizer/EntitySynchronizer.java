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
package org.eclipse.dirigible.components.data.store.synchronizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.data.store.DataStore;
import org.eclipse.dirigible.components.data.store.domain.Entity;
import org.eclipse.dirigible.components.data.store.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class BpmnSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(290)
public class EntitySynchronizer<A extends Artefact> implements Synchronizer<Entity> {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(EntitySynchronizer.class);
	
	/** The Constant FILE_EXTENSION_BPMN. */
	public static final String FILE_EXTENSION_ENTITY = ".hbm.xml";
	
	/** The entity service. */
	private EntityService entityService;
	
	/** The object store. */
	private DataStore dataStore;
	
	/** The synchronization callback. */
	private SynchronizerCallback callback;
	
	/**
	 * Instantiates a new entity synchronizer.
	 *
	 * @param entityService the entity service
	 * @param dataStore the data store
	 */
	@Autowired
	public EntitySynchronizer(EntityService entityService, DataStore dataStore) {
		this.entityService = entityService;
		this.dataStore = dataStore;
	}
	
	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	@Override
	public ArtefactService<Entity> getService() {
		return entityService;
	}
	
	/**
	 * Gets the data store.
	 *
	 * @return the data store
	 */
	public DataStore getDataStore() {
		return dataStore;
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
		return file.toString().endsWith(getFileExtension());
	}
	
	/**
	 * Checks if is accepted.
	 *
	 * @param type the artefact
	 * @return true, if is accepted
	 */
	@Override
	public boolean isAccepted(String type) {
		return Entity.ARTEFACT_TYPE.equals(type);
	}

	/**
	 * Load.
	 *
	 * @param location the location
	 * @param content the content
	 * @return the list
	 */
	@Override
	public List<Entity> load(String location, byte[] content) {
		Entity entity = new Entity();
		entity.setLocation(location);
		entity.setName(Paths.get(location).getFileName().toString());
		entity.setType(Entity.ARTEFACT_TYPE);
		entity.updateKey();
		entity.setContent(content);
		try {
			Entity maybe = getService().findByKey(entity.getKey());
			if (maybe != null) {
				entity.setId(maybe.getId());
			}
			getService().save(entity);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			if (logger.isErrorEnabled()) {logger.error("entity: {}", entity);}
			if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
		}
		return List.of(entity);
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
			
			dataStore.initialize();
			
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
		
		Entity entity = null;
		if (wrapper.getArtefact() instanceof Entity) {
			entity = (Entity) wrapper.getArtefact();
			dataStore.addMapping(entity.getKey(), prepareContent(entity));
			callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE_UPDATE, "");
			return true;
		}
		throw new UnsupportedOperationException(String.format("Trying to process %s as Entity", wrapper.getArtefact().getClass()));
	}

	/**
	 * Prepare content.
	 *
	 * @param entity the entity
	 * @return the string
	 */
	public String prepareContent(Entity entity) {
		return new String(entity.getContent(), StandardCharsets.UTF_8);
	}

	/**
	 * Cleanup.
	 *
	 * @param entity the entity
	 */
	@Override
	public void cleanup(Entity entity) {
		try {
			getService().delete(entity);
			dataStore.removeMapping(entity.getKey());
			dataStore.initialize();
			callback.registerState(this, entity, ArtefactLifecycle.DELETED.toString(), ArtefactState.SUCCESSFUL_DELETE, "");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
			callback.registerState(this, entity, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED_DELETE, e.getMessage());
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
	 * Gets the file entity.
	 *
	 * @return the file entity
	 */
	@Override
	public String getFileExtension() {
		return FILE_EXTENSION_ENTITY;
	}

	/**
	 * Gets the artefact type.
	 *
	 * @return the artefact type
	 */
	@Override
	public String getArtefactType() {
		return Entity.ARTEFACT_TYPE;
	}

}
