/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.api;

// TODO: Auto-generated Javadoc
/**
 * This interface represents a READ ONLY Repository. It allows for navigating through collections and resources.
 */
public interface IRepositoryReader {
	
	/**
	 * Performs initialization tasks.
	 *
	 * @throws RepositoryInitializationException in case of an error
	 */
	public void initialize() throws RepositoryInitializationException;

	/**
	 * Returns an instance of <code>ICollection</code> which represents the root
	 * collection of the repository.
	 * <p>
	 * This method does not throw any exceptions for convenience but is not
	 * guaranteed to return a valid collection.
	 * @return an {@link ICollection} instance
	 */
	public ICollection getRoot();

	/**
	 * Returns an <code>ICollection</code> instance representing the resource at
	 * the specified path.
	 * <p>
	 * The collection may not exist at the specified path.
	 * @param path the {@link ICollection} location
	 * @return an {@link ICollection} instance
	 */
	public ICollection getCollection(String path);

	/**
	 * Returns whether a collection with the specified path exists in the
	 * repository.
	 * @param path the {@link ICollection} location
	 * @return whether the {@link ICollection} exists
	 * @throws RepositoryReadException in case the check cannot be performed
	 */
	public boolean hasCollection(String path) throws RepositoryReadException;

	/**
	 * Returns an instance of <code>IResource</code> which represents the
	 * resource located at the specified path.
	 * <p>
	 * The resource may not exist at the specified path.
	 * @param path the location of the {@link IResource}
	 * @return an {@link IResource} instance
	 */
	public IResource getResource(String path);

	/**
	 * Returns whether a resource with the specified path exists in the
	 * repository.
	 * @param path the {@link IResource} location
	 * @return whether the {@link IResource} exists
	 * @throws RepositoryReadException in case the check cannot be performed
	 */
	public boolean hasResource(String path) throws RepositoryReadException;

}
