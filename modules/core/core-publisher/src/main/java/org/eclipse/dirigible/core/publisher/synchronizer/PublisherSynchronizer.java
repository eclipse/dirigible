/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.publisher.synchronizer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.config.ResourcesCache;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;
import org.eclipse.dirigible.core.publisher.service.PublisherCoreService;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PublisherSynchronizer takes the requests for publish and perform the needed actions on the artifacts assigned.
 */
@Singleton
public class PublisherSynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(PublisherSynchronizer.class);

	@Inject
	private PublisherCoreService publishCoreService;

	private Map<String, String> resourceLocations = new HashMap<String, String>();
	private List<String> unpublishLocations = new ArrayList<String>();

	private String currentWorkspace = null;

	private String currentRegistry = null;

	private Timestamp currentRequestTime = new Timestamp(0);

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		PublisherSynchronizer publisherSynchronizer = StaticInjector.getInjector().getInstance(PublisherSynchronizer.class);
		publisherSynchronizer.synchronize();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (PublisherSynchronizer.class) {
			logger.trace("Publishing...");
			try {
				List<PublishRequestDefinition> publishRequestDefinitions = getPendingPublishedRequests();

				if (publishRequestDefinitions.isEmpty()) {
					logger.trace("Nothing to publish.");
					return;
				}

				enumerateResourcesForPublish(publishRequestDefinitions);

				synchronizeRegistry();

				ResourcesCache.clear();

				removeProcessedRequests(publishRequestDefinitions);

				cleanup();
			} catch (Exception e) {
				logger.error("Publishing failed.", e);
			}
			logger.trace("Done publishing.");
		}
	}

	/**
	 * Enumerate resources for publish.
	 *
	 * @param publishRequestDefinitions
	 *            the publish request definitions
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void enumerateResourcesForPublish(List<PublishRequestDefinition> publishRequestDefinitions) throws SynchronizationException {
		cleanup();
		
		for (PublishRequestDefinition publishRequestDefinition : publishRequestDefinitions) {
			currentWorkspace = publishRequestDefinition.getWorkspace();
			String path = publishRequestDefinition.getPath();
			currentRegistry = (publishRequestDefinition.getRegistry() != null ? publishRequestDefinition.getRegistry()
					: IRepositoryStructure.PATH_REGISTRY_PUBLIC);
			currentRequestTime = (publishRequestDefinition.getCreatedAt().after(currentRequestTime) ? publishRequestDefinition.getCreatedAt()
					: currentRequestTime);
			
			if (publishRequestDefinition.getCommand().equals(PublishRequestDefinition.COMMAND_PUBLISH)) {
				// Publish
				String sourceLocation = new RepositoryPath(currentWorkspace, path).toString();
				ICollection collection = getRepository().getCollection(sourceLocation);
				if (collection.exists()) {
					synchronizeCollection(collection);
				} else {
					IResource resource = getRepository().getResource(sourceLocation);
					if (resource.exists()) {
						synchronizeResource(resource);
					}
				}
			} else if (publishRequestDefinition.getCommand().equals(PublishRequestDefinition.COMMAND_UNPUBLISH)) {
				// Unpublish
				String targetLocation = new RepositoryPath(currentRegistry, path).toString();
				unpublishLocations.add(targetLocation);
			} else {
				logger.error("Publishing error: Unknown command; " + publishRequestDefinition.getCommand());
			}
		}
	}

	/**
	 * Gets the pending published requests.
	 *
	 * @return the pending published requests
	 * @throws PublisherException
	 *             the publisher exception
	 */
	private List<PublishRequestDefinition> getPendingPublishedRequests() throws PublisherException {
		Timestamp timestamp = publishCoreService.getLatestPublishLog();
		List<PublishRequestDefinition> publishRequestDefinitions = publishCoreService.getPublishRequestsAfter(timestamp);
		return publishRequestDefinitions;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	public void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing published artefacts in Registry...");

		publishResources();
		
		unpublishResources();

		logger.trace("Done synchronizing published artefacts in Registry.");
	}

	/**
	 * Publish resources.
	 *
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void publishResources() throws SynchronizationException {
		for (Map.Entry<String, String> entry : resourceLocations.entrySet()) {

			// pre publish handlers

			try {
				// publish
				publishResource(entry);
			} catch (SynchronizationException e) {
				logger.error("Failed to publish: " + entry.getKey(), e);
			}

			// post publish handlers
		}
	}

	/**
	 * Publish resource.
	 *
	 * @param entry
	 *            the entry
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void publishResource(Map.Entry<String, String> entry) throws SynchronizationException {
		String sourceLocation = entry.getKey();
		String targetLocation = entry.getValue();
		
		ICollection sourceCollection = getRepository().getCollection(sourceLocation);
		if (sourceCollection.exists()) {
			// publish collection
			ICollection targetCollection = getRepository().getCollection(targetLocation);
			sourceCollection.copyTo(targetCollection.getPath());
			try {
				publishCoreService.createPublishLog(sourceCollection.getPath(), targetCollection.getPath());
			} catch (PublisherException e) {
				throw new SynchronizationException(e);
			}
		} else {
			// publish a single resource
			IResource sourceResource = getRepository().getResource(sourceLocation);
			IResource targetResource = getRepository().getResource(targetLocation);
			if (targetResource.exists()) {
				java.util.Date lastModified = targetResource.getInformation().getModifiedAt();
				if ((lastModified == null) || (currentRequestTime.getTime() > lastModified.getTime())) {
					targetResource.setContent(sourceResource.getContent());
				}
			} else {
				getRepository().createResource(targetLocation, sourceResource.getContent());
			}
			try {
				publishCoreService.createPublishLog(sourceResource.getPath(), targetResource.getPath());
			} catch (PublisherException e) {
				throw new SynchronizationException(e);
			}
		}
	}
	
	/**
	 * Unpublish resources.
	 *
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void unpublishResources() throws SynchronizationException {
		for (String entry : unpublishLocations) {

			// pre unpublish handlers

			try {
				// unpublish
				unpublishResource(entry);
			} catch (SynchronizationException e) {
				logger.error("Failed to unpublish: " + entry, e);
			}

			// post unpublish handlers
		}
	}
	
	/**
	 * Publish resource.
	 *
	 * @param entry
	 *            the entry
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void unpublishResource(String entry) throws SynchronizationException {
		String targetLocation = entry;
		
		ICollection targetCollection = getRepository().getCollection(targetLocation);
		if (targetCollection.exists()) {
			// unpublish collection
			targetCollection.delete();
			try {
				publishCoreService.createPublishLog(PublishRequestDefinition.COMMAND_UNPUBLISH, targetCollection.getPath());
			} catch (PublisherException e) {
				throw new SynchronizationException(e);
			}
		} else {
			// unpublish a single resource
			IResource targetResource = getRepository().getResource(targetLocation);
			if (targetResource.exists()) {
				targetResource.delete();
			}
			try {
				publishCoreService.createPublishLog(PublishRequestDefinition.COMMAND_UNPUBLISH, targetResource.getPath());
			} catch (PublisherException e) {
				throw new SynchronizationException(e);
			}
		}
	}

	/**
	 * Removes the processed requests.
	 *
	 * @param publishRequestDefinitions
	 *            the publish request definitions
	 * @throws PublisherException
	 *             the publisher exception
	 */
	private void removeProcessedRequests(List<PublishRequestDefinition> publishRequestDefinitions) throws PublisherException {
		for (PublishRequestDefinition publishRequestDefinition : publishRequestDefinitions) {
			publishCoreService.removePublishRequest(publishRequestDefinition.getId());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeCollection(org.eclipse.dirigible.
	 * repository.api.IResource)
	 */
	@Override
	protected void synchronizeCollection(ICollection collection) throws SynchronizationException {
		String sourceLocation = collection.getPath();
		String path = sourceLocation.substring(currentWorkspace.length());
		String targetLocation = new RepositoryPath(currentRegistry, path).toString();
		resourceLocations.put(sourceLocation, targetLocation);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.
	 * repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String sourceLocation = resource.getPath();
		String path = sourceLocation.substring(currentWorkspace.length());
		String targetLocation = new RepositoryPath(currentRegistry, path).toString();
		resourceLocations.put(sourceLocation, targetLocation);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	public void cleanup() throws SynchronizationException {
		resourceLocations.clear();
		unpublishLocations.clear();
	}
}
