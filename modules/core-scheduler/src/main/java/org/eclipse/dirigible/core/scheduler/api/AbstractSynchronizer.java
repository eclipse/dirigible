/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
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

public abstract class AbstractSynchronizer implements ISynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(AbstractSynchronizer.class);

	@Inject
	private IRepository repository;

	protected IRepository getRepository() {
		return repository;
	}

	protected void synchronizeRegistry() throws SynchronizationException {
		ICollection collection = getRepository().getCollection(IRepositoryStructure.PATH_REGISTRY_PUBLIC);
		if (collection.exists()) {
			synchronizeCollection(collection);
		}
	}

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

	protected String getRegistryPath(IResource resource) throws SynchronizationException {
		String resourcePath = resource.getPath();
		if (resourcePath.startsWith(IRepositoryStructure.PATH_REGISTRY_PUBLIC)) {
			return resourcePath.substring(IRepositoryStructure.PATH_REGISTRY_PUBLIC.length());
		}
		return resourcePath;
	}

	protected abstract void synchronizeResource(IResource resource) throws SynchronizationException;

	protected abstract void cleanup() throws SynchronizationException;

}
