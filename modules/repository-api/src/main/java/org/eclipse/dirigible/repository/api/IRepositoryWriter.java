/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * This interface represents a Repository. It allows for querying, modifying and
 * navigating through collections and resources.
 */
public interface IRepositoryWriter {

	/**
	 * This method creates a new empty collection at the specified path.
	 * <p>
	 * The returned value is an instance of <code>ICollection</code> which
	 * represents the newly created collection.
	 */
	public ICollection createCollection(String path) throws RepositoryWriteException;

	/**
	 * This method removes the collection with the specified path from the
	 * repository.
	 */
	public void removeCollection(String path) throws RepositoryWriteException;

	/**
	 * This method creates a new empty resource at the specified path.
	 * <p>
	 * The returned value is an instance of <code>IResource</code> that
	 * represents the newly created resource.
	 */
	public IResource createResource(String path) throws RepositoryWriteException;

	/**
	 * This method creates a new resource at the specified path and fills it
	 * with the specified content.
	 * <p>
	 * The returned value is an instance of <code>IResource</code> that
	 * represents the newly created resource.
	 */
	public IResource createResource(String path, byte[] content) throws RepositoryWriteException;

	/**
	 * This method creates a new empty resource at the specified path.
	 * <p>
	 * The returned value is an instance of <code>IResource</code> that
	 * represents the newly created resource.
	 */
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType) throws RepositoryWriteException;

	/**
	 * This method creates a new empty, or override resource at the specified path.
	 * <p>
	 * The returned value is an instance of <code>IResource</code> that
	 * represents the newly created resource.
	 */
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType, boolean override) throws RepositoryWriteException;

	/**
	 * This method removes the resource at the specified path from the
	 * repository.
	 */
	public void removeResource(String path) throws RepositoryWriteException;

	/**
	 * Disposes of this repository.
	 * <p>
	 * Calling this method allows for the repository to release all allocated
	 * resources.
	 * <p>
	 * Calling this method more than once will be a no-op.
	 */
	public void dispose();

	/**
	 * Clean-up the file versions older than a month For full fledged SCM
	 * system, use external e.g. Git
	 *
	 * @throws IOException
	 */
	public void cleanupOldVersions() throws IOException;

}
