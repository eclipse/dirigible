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
package org.eclipse.dirigible.api.v3.platform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.FileSystemUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.local.LocalCollection;
import org.eclipse.dirigible.runtime.repository.json.Repository;
import org.eclipse.dirigible.runtime.repository.json.RepositoryJsonHelper;
import org.eclipse.dirigible.runtime.repository.processor.RepositoryProcessor;

/**
 * The Repository Facade
 */
public class RepositoryFacade {
	
	private static RepositoryProcessor repositoryProcessor = new RepositoryProcessor();
	
	/**
	 * Gets the resource.
	 *
	 * @param path
	 *            the path
	 * @return the resource
	 */
	public static IResource getResource(String path) {
		return repositoryProcessor.getResource(path);
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
	public static IResource createResource(String path, String content, String contentType) {
		return repositoryProcessor.createResource(path, content.getBytes(), contentType);
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
	public static IResource createResourceNative(String path, byte[] content, String contentType) {
		return repositoryProcessor.createResource(path, content, contentType);
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
	public static IResource updateResource(String path, String content) {
		return repositoryProcessor.updateResource(path, content.getBytes());
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
	public static IResource updateResourceNative(String path, byte[] content) {
		return repositoryProcessor.updateResource(path, content);
	}

	/**
	 * Deletes a resource.
	 *
	 * @param path
	 *            the path
	 */
	public static void deleteResource(String path) {
		repositoryProcessor.deleteResource(path);
	}

	/**
	 * Gets the collection.
	 *
	 * @param path
	 *            the path
	 * @return the collection
	 */
	public static ICollection getCollection(String path) {
		return repositoryProcessor.getCollection(path);
	}

	/**
	 * Render repository.
	 *
	 * @param collection
	 *            the collection
	 * @return the repository
	 */
	public static Repository renderRepository(ICollection collection) {
		return RepositoryJsonHelper.traverseRepository(collection, "", "");
	}

	/**
	 * Creates a new collection.
	 *
	 * @param path
	 *            the path
	 * @return the collection
	 */
	public static ICollection createCollection(String path) {
		return repositoryProcessor.createCollection(path);
	}

	/**
	 * Deletes a collection.
	 *
	 * @param path
	 *            the path
	 */
	public static void deleteCollection(String path) {
		repositoryProcessor.deleteCollection(path);
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
	public static String find(String path, String pattern) throws IOException, ScriptingException {
		return repositoryProcessor.find(path, pattern);
	}
}
