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

/**
 * This interface represents a READ ONLY Repository. It allows for navigating through collections and resources.
 */
public interface IReadOnlyRepository {

	/**
	 * Returns an instance of <code>ICollection</code> which represents the root
	 * collection of the repository.
	 * <p>
	 * This method does not throw any exceptions for convenience but is not
	 * guaranteed to return a valid collection.
	 *
	 * @return an {@link ICollection} instance
	 */
	public ICollection getRoot();

	/**
	 * Returns an <code>ICollection</code> instance representing the resource at
	 * the specified path.
	 * <p>
	 * The collection may not exist at the specified path.
	 *
	 * @param path
	 * @return an {@link ICollection} instance
	 */
	public ICollection getCollection(String path);

	/**
	 * Returns whether a collection with the specified path exists in the
	 * repository.
	 *
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean hasCollection(String path) throws IOException;

	/**
	 * Returns an instance of <code>IResource</code> which represents the
	 * resource located at the specified path.
	 * <p>
	 * The resource may not exist at the specified path.
	 *
	 * @param path
	 *            the location
	 * @return an {@link IResource} instance
	 */
	public IResource getResource(String path);

	/**
	 * Returns whether a resource with the specified path exists in the
	 * repository.
	 *
	 * @param path
	 *            the location
	 * @return whether the {@link IRepository} contains an {@link IResource} by this path
	 * @throws IOException
	 */
	public boolean hasResource(String path) throws IOException;

	/**
	 * Export all the content under the given path(s) with the target repository
	 * instance Include the last segment of the relative roots during the
	 * archiving
	 *
	 * @param relativeRoots
	 *            the relative roots
	 * @return the {@link IRepository} content
	 * @throws IOException
	 */
	public byte[] exportZip(List<String> relativeRoots) throws IOException;

	/**
	 * Export all the content under the given path with the target repository
	 * instance Include or NOT the last segment of the relative root during the
	 * archiving
	 *
	 * @param relativeRoot
	 *            single root
	 * @param inclusive
	 *            whether to include the last segment of the root or to pack its
	 *            content directly in the archive
	 * @return the {@link IRepository} content
	 * @throws IOException
	 */
	public byte[] exportZip(String relativeRoot, boolean inclusive) throws IOException;

	/**
	 * Retrieve all the kept versions of a given resource
	 *
	 * @param path
	 *            the location
	 * @return the list of {@link IResourceVersion} instances
	 * @throws IOException
	 */
	public List<IResourceVersion> getResourceVersions(String path) throws IOException;

	/**
	 * Retrieve a particular version of a given resource
	 *
	 * @param path
	 *            the location
	 * @param version
	 *            the exact version
	 * @return the {@link IResourceVersion}
	 * @throws IOException
	 */
	public IResourceVersion getResourceVersion(String path, int version) throws IOException;

	/**
	 * Getter for the user has created this instance of a repository object
	 *
	 * @return the user name
	 */
	public String getUser();

}
