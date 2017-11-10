/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.repository.processor;

import javax.inject.Inject;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.repository.json.Repository;
import org.eclipse.dirigible.runtime.repository.json.RepositoryJsonHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class RepositoryProcessor.
 */
public class RepositoryProcessor {

	/** The repository. */
	@Inject
	private IRepository repository;

	/**
	 * Gets the resource.
	 *
	 * @param path the path
	 * @return the resource
	 */
	public IResource getResource(String path) {
		return repository.getResource(path);
	}

	/**
	 * Creates the resource.
	 *
	 * @param path the path
	 * @param content the content
	 * @param contentType the content type
	 * @return the i resource
	 */
	public IResource createResource(String path, byte[] content, String contentType) {
		return repository.createResource(path, content, false, contentType);
	}

	/**
	 * Update resource.
	 *
	 * @param path the path
	 * @param content the content
	 * @return the i resource
	 */
	public IResource updateResource(String path, byte[] content) {
		IResource resource = repository.getResource(path);
		resource.setContent(content);
		return resource;
	}

	/**
	 * Delete resource.
	 *
	 * @param path the path
	 */
	public void deleteResource(String path) {
		repository.removeResource(path);
	}

	/**
	 * Gets the collection.
	 *
	 * @param path the path
	 * @return the collection
	 */
	public ICollection getCollection(String path) {
		return repository.getCollection(path);
	}

	/**
	 * Render repository.
	 *
	 * @param collection the collection
	 * @return the repository
	 */
	public Repository renderRepository(ICollection collection) {
		return RepositoryJsonHelper.traverseRepository(collection, "", "");
	}

}
