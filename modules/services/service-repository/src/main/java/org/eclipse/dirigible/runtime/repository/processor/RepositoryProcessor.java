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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.api.v3.utils.UrlFacade;
import org.eclipse.dirigible.commons.api.helpers.FileSystemUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.local.LocalCollection;
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

	/**
	 * Find all the files matching the pattern
	 *
	 * @param path the root path
	 * @param pattern the glob pattern
	 * @return the list of file names
	 * @throws IOException in case of an error
	 * @throws ScriptingException in case of an error
	 */
	public String find(String path, String pattern) throws IOException, ScriptingException {
		ICollection collection = getCollection(path);
		if (collection.exists() && collection instanceof LocalCollection) {
			List<String> list = FileSystemUtils.find(((LocalCollection) collection).getFolder().getPath(), pattern);
			int repositoryRootLength = ((LocalCollection) collection.getRepository().getRoot()).getFolder().getPath().length();
			List<String> prepared = new ArrayList<String>();
			list.forEach(item -> {
				String truncated = item.substring(repositoryRootLength);
				if (!IRepository.SEPARATOR.equals(File.separator)) {
					truncated = truncated.replace(File.separator, IRepository.SEPARATOR);
				}
				prepared.add(truncated);
			});

			return GsonHelper.GSON.toJson(prepared);
		}
		return "[]";
	}
}
