/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.registry.processor;

import javax.inject.Inject;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.repository.json.Registry;
import org.eclipse.dirigible.runtime.repository.json.RepositoryJsonHelper;

/**
 * Processing the Registry Service incoming requests.
 */
public class RegistryProcessor {

	private static final String REGISTRY = "/registry";

	@Inject
	private IRepository repository;

	/**
	 * Gets the resource.
	 *
	 * @param path
	 *            the path
	 * @return the resource
	 */
	public IResource getResource(String path) {
		StringBuilder registryPath = generateRegistryPath(path);
		return repository.getResource(registryPath.toString());
	}

	/**
	 * Gets the collection.
	 *
	 * @param path
	 *            the path
	 * @return the collection
	 */
	public ICollection getCollection(String path) {
		StringBuilder registryPath = generateRegistryPath(path);
		return repository.getCollection(registryPath.toString());
	}

	/**
	 * Render registry.
	 *
	 * @param collection
	 *            the collection
	 * @return the registry
	 */
	public Registry renderRegistry(ICollection collection) {
		return RepositoryJsonHelper.traverseRegistry(collection, IRepositoryStructure.PATH_REGISTRY_PUBLIC, REGISTRY);
	}

	/**
	 * Generate registry path.
	 *
	 * @param path
	 *            the path
	 * @return the string builder
	 */
	private StringBuilder generateRegistryPath(String path) {
		StringBuilder registryPath = new StringBuilder(IRepositoryStructure.PATH_REGISTRY_PUBLIC).append(IRepositoryStructure.SEPARATOR).append(path);
		return registryPath;
	}

}
