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
package org.eclipse.dirigible.core.scheduler.api;

import org.eclipse.dirigible.commons.api.artefacts.IArtefactDefinition;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.scheduler.service.SynchronizerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.SynchronizerStateDefinition;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static java.text.MessageFormat.format;

/**
 * The AbstractSynchronizer.
 */
public abstract class AbstractSynchronizer implements ISynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(AbstractSynchronizer.class);

	private IRepository repository = null;
	
	private SynchronizerCoreService synchronizerCoreService = new SynchronizerCoreService();
	
	private final AtomicLong lastSynchronized = new AtomicLong(0);
	
	private final AtomicBoolean forcedSynchronization = new AtomicBoolean(false);
	
	
	
	@Override
	public boolean beforeSynchronizing() {
		if (forcedSynchronization.get() || getLastSynchronized() < getRepository().getLastModified()) {
			return true;
		}
		return false;
	}
	
	@Override
	public void afterSynchronizing() {
		setLastSynchronized(System.currentTimeMillis());
	}
	
	@Override
	public void setForcedSynchronization(boolean forced) {
		forcedSynchronization.set(forced);
	}
	
	public long getLastSynchronized() {
		return lastSynchronized.get();
	}
	
	protected void setLastSynchronized(long time) {
		lastSynchronized.set(time);
	}

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	protected synchronized IRepository getRepository() {
		if (repository == null) {
			repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		}
		return repository;
	}

	/**
	 * Synchronize registry.
	 *
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	protected void synchronizeRegistry() throws SynchronizationException {
		ICollection collection = getRepository().getCollection(IRepositoryStructure.PATH_REGISTRY_PUBLIC);
		if (collection.exists()) {
			synchronizeCollection(collection);
		}
	}

	/**
	 * Synchronize collection.
	 *
	 * @param collection
	 *            the collection
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	protected void synchronizeCollection(ICollection collection) throws SynchronizationException {
		List<IResource> resources = collection.getResources();
		for (IResource resource : resources) {
			try {
				synchronizeResource(resource);
			} catch (Exception e) {
				logger.error(format("Resource [{0}] skipped due to an error: {1}", resource.getPath(), e.getMessage()), e);
			}
		}
		List<ICollection> collections = collection.getCollections();
		for (ICollection childCollection : collections) {
			synchronizeCollection(childCollection);
		}
	}

	/**
	 * Gets the registry path.
	 *
	 * @param resource
	 *            the resource
	 * @return the registry path
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	protected String getRegistryPath(IResource resource) throws SynchronizationException {
		String resourcePath = resource.getPath();
		if (resourcePath.startsWith(IRepositoryStructure.PATH_REGISTRY_PUBLIC)) {
			return resourcePath.substring(IRepositoryStructure.PATH_REGISTRY_PUBLIC.length());
		}
		return resourcePath;
	}

	/**
	 * Synchronize resource.
	 *
	 * @param resource
	 *            the resource
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	protected abstract void synchronizeResource(IResource resource) throws SynchronizationException;

	/**
	 * Cleanup.
	 *
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	protected void cleanup() throws SynchronizationException {
		try {
			synchronizerCoreService.deleteOldSynchronizerStateLogs();
		} catch (SchedulerException e) {
			logger.error(format("Error during cleaning up the state log from: [{0}]. Skipped due to an error: {1}", this.getClass().getCanonicalName(), e.getMessage()), e);
		}
	}
	
	protected void registerSynchronizer(String name) throws SchedulerException {
		SynchronizerStateDefinition synchronizerStateDefinition = synchronizerCoreService.getSynchronizerState(name);
		if (synchronizerStateDefinition == null) {
			synchronizerCoreService.createSynchronizerState(name, ISynchronizerCoreService.STATE_INITIAL, "", 0, 0, 0, 0);
		}
	}
	
	protected void startSynchronization(String name) throws SchedulerException {
		SynchronizerStateDefinition synchronizerStateDefinition = synchronizerCoreService.getSynchronizerState(name);
		long currentTimeMillis = System.currentTimeMillis();
		if (synchronizerStateDefinition == null) {
			synchronizerCoreService.createSynchronizerState(name, ISynchronizerCoreService.STATE_IN_PROGRESS, "", 
					currentTimeMillis, 0, currentTimeMillis, 0);
		} else {
			synchronizerStateDefinition.setState(ISynchronizerCoreService.STATE_IN_PROGRESS);
			synchronizerStateDefinition.setLastTimeTriggered(currentTimeMillis);
			if (synchronizerStateDefinition.getFirstTimeTriggered() == 0) {
				synchronizerStateDefinition.setFirstTimeTriggered(currentTimeMillis);
			}
			synchronizerCoreService.updateSynchronizerState(synchronizerStateDefinition);
		}
	}
	
	protected void successfulSynchronization(String name, String message) throws SchedulerException {
		SynchronizerStateDefinition synchronizerStateDefinition = synchronizerCoreService.getSynchronizerState(name);
		long currentTimeMillis = System.currentTimeMillis();
		if (synchronizerStateDefinition == null) {
			throw new SchedulerException(format("Invalid state - finishing successful synchronization for: {0}, which has not been initialized yet.", this.getClass().getCanonicalName()));
		} else {
			if (synchronizerStateDefinition.getState() != ISynchronizerCoreService.STATE_IN_PROGRESS) {
				throw new SchedulerException(format("Invalid state - finishing successful synchronization for: {0}, which has not been 'in progress'.", this.getClass().getCanonicalName()));
			}
			synchronizerStateDefinition.setState(ISynchronizerCoreService.STATE_SUCCESSFUL);
			synchronizerStateDefinition.setMessage(message);
			synchronizerStateDefinition.setLastTimeFinished(currentTimeMillis);
			if (synchronizerStateDefinition.getFirstTimeFinished() == 0) {
				synchronizerStateDefinition.setFirstTimeFinished(currentTimeMillis);
			}
			synchronizerCoreService.updateSynchronizerState(synchronizerStateDefinition);
		}
	}
	
	protected void failedSynchronization(String name, String message) throws SchedulerException {
		SynchronizerStateDefinition synchronizerStateDefinition = synchronizerCoreService.getSynchronizerState(name);
		long currentTimeMillis = System.currentTimeMillis();
		if (synchronizerStateDefinition == null) {
			logger.error(format("Invalid state - finishing failed synchronization for: {0}, which has not been initialized yet.", this.getClass().getCanonicalName()));
			synchronizerCoreService.createSynchronizerState(name, ISynchronizerCoreService.STATE_FAILED, message, 
					0, 0, 0, 0);
		} else {
			if (synchronizerStateDefinition.getState() != ISynchronizerCoreService.STATE_IN_PROGRESS) {
				logger.error(format("Invalid state - finishing failed synchronization for: {0}, which has not been 'in progress'.", this.getClass().getCanonicalName()));
			}
			synchronizerStateDefinition.setState(ISynchronizerCoreService.STATE_FAILED);
			synchronizerStateDefinition.setMessage(message);
//			synchronizerStateDefinition.setLastTimeFinished(currentTimeMillis);
//			if (synchronizerStateDefinition.getFirstTimeFinished() != 0) {
//				synchronizerStateDefinition.setFirstTimeFinished(currentTimeMillis);
//			}
			synchronizerCoreService.updateSynchronizerState(synchronizerStateDefinition);
		}
	}
	
	protected boolean isSynchronizerSuccessful(String name) throws SchedulerException {
		boolean ignoreDependencies = Boolean.parseBoolean(Configuration.get(ISynchronizer.DIRIGIBLE_SYNCHRONIZER_IGNORE_DEPENDENCIES, "false"));
		if (ignoreDependencies) {
			logger.warn(format("Dependencies skiped for: {0}, due to configuration.", this.getClass().getCanonicalName()));
			return true;
		}
		SynchronizerStateDefinition synchronizerStateDefinition = synchronizerCoreService.getSynchronizerState(name);
//		return synchronizerStateDefinition != null && synchronizerStateDefinition.getState() == ISynchronizerCoreService.STATE_SUCCESSFUL;
		return synchronizerStateDefinition != null 
				&& synchronizerStateDefinition.getFirstTimeTriggered() != 0 && synchronizerStateDefinition.getFirstTimeFinished() != 0
				&& synchronizerStateDefinition.getState() != ISynchronizerCoreService.STATE_FAILED;
	}
	
	@Override
	public boolean isSynchronizationEnabled() {
		return SynchronizerCoreService.isSynchronizationEnabled();
	}

	/**
	 * Apply the state
	 * 
	 * @param artefact the artefact
	 * @param type the type
	 * @param state the state
	 */
	public void applyArtefactState(IArtefactDefinition artefact, ISynchronizerArtefactType type, ISynchronizerArtefactType.ArtefactState state) {
		applyArtefactState(artefact, type, state, null);
	}

	/**
	 * Apply the state
	 * 
	 * @param artefact the artefact
	 * @param type the type
	 * @param state the state
	 * @param message the message
	 */
	public void applyArtefactState(IArtefactDefinition artefact, ISynchronizerArtefactType type, ISynchronizerArtefactType.ArtefactState state, String message) {
		if (artefact != null && type != null && state != null) {
			String artefactName = artefact.getArtefactName();
			String artefactLocation = artefact.getArtefactLocation();
			String artefactType = type.getId();
			String artefactState = state.getValue();
			String artefactStateMessage = type.getStateMessage(state, message);
			checkSynchronizerStateArtefactCurrentState(artefactName, artefactLocation, artefactType, artefactState, artefactStateMessage);
		} else {
			logger.error("Can't apply artefact state to \"null\" artefact object.");
		}
	}

	private void checkSynchronizerStateArtefactCurrentState(String artefactName, String artefactLocation, String artefactType, String artefactState, String artefactStateMessage) {
		try {
			if (synchronizerCoreService.existsSynchronizerStateArtefact(artefactName, artefactLocation)) {
				synchronizerCoreService.updateSynchronizerStateArtefact(artefactName, artefactLocation, artefactType, artefactState, artefactStateMessage);
			} else {
				synchronizerCoreService.createSynchronizerStateArtefact(artefactName, artefactLocation, artefactType, artefactState, artefactStateMessage);
			}
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void applyArtefactState(String artefactName, String artefactLocation, AbstractSynchronizationArtefactType type, ISynchronizerArtefactType.ArtefactState state, String message) {
		if (artefactName != null && artefactLocation != null && type != null && state != null) {
			String artefactType = type.getId();
			String artefactState = state.getValue();
			String artefactStateMessage = type.getStateMessage(state, message);
			checkSynchronizerStateArtefactCurrentState(artefactName, artefactLocation, artefactType, artefactState, artefactStateMessage);
		} else {
			logger.error("Can't apply artefact state to \"null\" artefact object.");
		}
	}
	
	public void removeArtefactState(String name, String location) throws SchedulerException {
		synchronizerCoreService.removeSynchronizerStateArtefact(name, location);
	}

}
