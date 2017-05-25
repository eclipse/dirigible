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
	 * Performs initialization tasks
	 * 
	 * @throws IOException
	 */
	public void initialize() throws RepositoryInitializationException;

	/**
	 * Returns an instance of <code>ICollection</code> which represents the root
	 * collection of the repository.
	 * <p>
	 * This method does not throw any exceptions for convenience but is not
	 * guaranteed to return a valid collection. One should check this by using
	 * the {@link ICollection#isValid()} method.
	 */
	public ICollection getRoot();

	/**
	 * Returns an <code>ICollection</code> instance representing the resource at
	 * the specified path.
	 * <p>
	 * The collection may not exist at the specified path.
	 */
	public ICollection getCollection(String path);

	/**
	 * Returns whether a collection with the specified path exists in the
	 * repository.
	 */
	public boolean hasCollection(String path) throws IOException;

	/**
	 * Returns an instance of <code>IResource</code> which represents the
	 * resource located at the specified path.
	 * <p>
	 * The resource may not exist at the specified path.
	 */
	public IResource getResource(String path);

	/**
	 * Returns whether a resource with the specified path exists in the
	 * repository.
	 */
	public boolean hasResource(String path) throws IOException;

	/**
	 * Export all the content under the given path(s) with the target repository
	 * instance Include the last segment of the relative roots during the
	 * archiving
	 *
	 * @param relativeRoot
	 * @return
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
	 * @return
	 * @throws IOException
	 */
	public byte[] exportZip(String relativeRoot, boolean inclusive) throws IOException;

	/**
	 * Retrieve all the kept versions of a given resource
	 *
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public List<IResourceVersion> getResourceVersions(String path) throws IOException;

	/**
	 * Retrieve a particular version of a given resource
	 *
	 * @param path
	 * @param version
	 * @return
	 * @throws IOException
	 */
	public IResourceVersion getResourceVersion(String path, int version) throws IOException;

}
