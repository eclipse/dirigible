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
package org.eclipse.dirigible.core.publisher.synchronizer;

import org.eclipse.dirigible.commons.config.ResourcesCache;
import org.eclipse.dirigible.core.publisher.api.IPublisherHandler;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;
import org.eclipse.dirigible.core.publisher.service.PublisherCoreService;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.core.scheduler.service.SynchronizerCoreService;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;

/**
 * The PublisherSynchronizer takes the requests for publish and perform the needed actions on the artifacts assigned.
 */
public class PublisherSynchronizer extends AbstractSynchronizer {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(PublisherSynchronizer.class);

	/** The publish core service. */
	private PublisherCoreService publishCoreService = new PublisherCoreService();
	
	/** The synchronizer core service. */
	private SynchronizerCoreService synchronizerCoreService = new SynchronizerCoreService();

	/** The resource locations. */
	private Map<String, String> resourceLocations = new HashMap<String, String>();
	
	/** The unpublish locations. */
	private List<String> unpublishLocations = new ArrayList<String>();

	/** The current workspace. */
	private String currentWorkspace = null;

	/** The current registry. */
	private String currentRegistry = null;

	/** The current request time. */
	private Timestamp currentRequestTime = new Timestamp(0);

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		PublisherSynchronizer publisherSynchronizer = new PublisherSynchronizer();
		publisherSynchronizer.synchronize();
	}

	/**
	 * Synchronize.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (PublisherSynchronizer.class) {
			if (logger.isTraceEnabled()) {logger.trace("Publishing...");}
			try {
				List<PublishRequestDefinition> publishRequestDefinitions = getPendingPublishedRequests();

				if (publishRequestDefinitions.isEmpty()) {
					if (logger.isTraceEnabled()) {logger.trace("Nothing to publish.");}
					return;
				}

				enumerateResourcesForPublish(publishRequestDefinitions);

				synchronizeRegistry();

				ResourcesCache.clear();

				removeProcessedRequests(publishRequestDefinitions);

				cleanup();
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {logger.error("Publishing failed.", e);}
			}
			if (logger.isTraceEnabled()) {logger.trace("Done publishing.");}
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
				if (logger.isErrorEnabled()) {logger.error("Publishing error: Unknown command; " + publishRequestDefinition.getCommand());}
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
	public void synchronizeRegistry() throws SynchronizationException {
		if (logger.isTraceEnabled()) {logger.trace("Synchronizing published artefacts in Registry...");}

		publishResources();
		
		unpublishResources();

		if (logger.isTraceEnabled()) {logger.trace("Done synchronizing published artefacts in Registry.");}
	}

	/**
	 * Publish resources.
	 *
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void publishResources() throws SynchronizationException {
		if (!resourceLocations.isEmpty()) {
			try {
				synchronizerCoreService.disableSynchronization();
				
				synchronizerCoreService.initializeSynchronizersStates();

				ServiceLoader<IPublisherHandler> publisherHandlers = ServiceLoader.load(IPublisherHandler.class);
				
				for (Map.Entry<String, String> entry : resourceLocations.entrySet()) {
					for (IPublisherHandler next : publisherHandlers) {
						next.beforePublish(entry.getKey());
					}
		
					try {
						// publish
						publishResource(entry);
					} catch (SynchronizationException e) {
						if (logger.isErrorEnabled()) {logger.error("Failed to publish: " + entry.getKey(), e);}
					}

					for (IPublisherHandler next : publisherHandlers) {
						next.afterPublish(entry.getKey(), entry.getValue());
					}
				}
			} catch (SchedulerException e) {
				throw new SynchronizationException(e);
			} finally {
				try {
					synchronizerCoreService.enableSynchronization();
				} catch (SchedulerException e) {
					throw new SynchronizationException(e);
				}
			}
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
		if (!unpublishLocations.isEmpty()) {
			try {
				synchronizerCoreService.disableSynchronization();
				
				synchronizerCoreService.initializeSynchronizersStates();

				ServiceLoader<IPublisherHandler> publisherHandlers = ServiceLoader.load(IPublisherHandler.class);

				for (String entry : unpublishLocations) {
					for (IPublisherHandler next : publisherHandlers) {
						next.beforeUnpublish(entry);
					}
		
					try {
						// unpublish
						unpublishResource(entry);
					} catch (SynchronizationException e) {
						if (logger.isErrorEnabled()) {logger.error("Failed to unpublish: " + entry, e);}
					}

					for (IPublisherHandler next : publisherHandlers) {
						next.afterUnpublish(entry);
					}
				}
			} catch (SchedulerException e) {
				throw new SynchronizationException(e);
			} finally {
				try {
					synchronizerCoreService.enableSynchronization();
				} catch (SchedulerException e) {
					throw new SynchronizationException(e);
				}
			}
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
	
	/**
	 * Synchronize collection.
	 *
	 * @param collection the collection
	 * @throws SynchronizationException the synchronization exception
	 */
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
		String sourceLocation = resource.getPath();
		String path = sourceLocation.substring(currentWorkspace.length());
		String targetLocation = new RepositoryPath(currentRegistry, path).toString();
		resourceLocations.put(sourceLocation, targetLocation);
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
	public void cleanup() throws SynchronizationException {
		resourceLocations.clear();
		unpublishLocations.clear();
	}
}
