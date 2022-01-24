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
package org.eclipse.dirigible.runtime.repository.processor;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.dirigible.api.v3.utils.UrlFacade;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.repository.json.Repository;
import org.eclipse.dirigible.runtime.repository.json.RepositoryJsonHelper;

/**
 * The Repository Processor.
 */
public class RepositoryProcessor {

	private static final String REPOSITORY_SERVICE_PREFIX = "core/repository";

	private IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

	/**
	 * Gets the resource.
	 *
	 * @param path
	 *            the path
	 * @return the resource
	 */
	public IResource getResource(String path) {
		return repository.getResource(path);
	}

	/**
	 * Creates the resource.
	 *
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @param contentType
	 *            the content type
	 * @return the i resource
	 */
	public IResource createResource(String path, byte[] content, String contentType) {
		return repository.createResource(path, content, false, contentType);
	}

	/**
	 * Update resource.
	 *
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @return the i resource
	 */
	public IResource updateResource(String path, byte[] content) {
		IResource resource = repository.getResource(path);
		resource.setContent(content);
		return resource;
	}

	/**
	 * Deletes a resource.
	 *
	 * @param path
	 *            the path
	 */
	public void deleteResource(String path) {
		repository.removeResource(path);
	}

	/**
	 * Gets the collection.
	 *
	 * @param path
	 *            the path
	 * @return the collection
	 */
	public ICollection getCollection(String path) {
		return repository.getCollection(path);
	}

	/**
	 * Render repository.
	 *
	 * @param collection
	 *            the collection
	 * @return the repository
	 */
	public Repository renderRepository(ICollection collection) {
		return RepositoryJsonHelper.traverseRepository(collection, "", "");
	}

	/**
	 * Creates a new collection.
	 *
	 * @param path
	 *            the path
	 * @return the collection
	 */
	public ICollection createCollection(String path) {
		return repository.createCollection(path);
	}

	/**
	 * Deletes a collection.
	 *
	 * @param path
	 *            the path
	 */
	public void deleteCollection(String path) {
		repository.removeCollection(path);
	}

	/**
	 * Gets the uri.
	 *
	 * @param path
	 *            the path
	 * @return the uri
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	public URI getURI(String path) throws URISyntaxException {
		StringBuilder relativePath = new StringBuilder(REPOSITORY_SERVICE_PREFIX).append(IRepositoryStructure.SEPARATOR);
		if (path != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(path);
		}
		return new URI(UrlFacade.escape(relativePath.toString()));
	}

}
