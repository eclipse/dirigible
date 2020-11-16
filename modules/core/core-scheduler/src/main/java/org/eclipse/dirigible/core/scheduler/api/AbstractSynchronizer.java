/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.scheduler.api;

import static java.text.MessageFormat.format;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AbstractSynchronizer.
 */
public abstract class AbstractSynchronizer implements ISynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(AbstractSynchronizer.class);

	@Inject
	private IRepository repository;

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	protected IRepository getRepository() {
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
	protected abstract void cleanup() throws SynchronizationException;

}
