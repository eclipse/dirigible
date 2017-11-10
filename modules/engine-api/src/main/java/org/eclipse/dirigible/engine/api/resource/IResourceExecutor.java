/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.api.resource;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryException;

// TODO: Auto-generated Javadoc
/**
 * The Interface IResourceExecutor.
 */
public interface IResourceExecutor {
	
	/**
	 * Gets the resource content.
	 *
	 * @param root the root
	 * @param module the module
	 * @return the resource content
	 * @throws RepositoryException the repository exception
	 */
	public byte[] getResourceContent(String root, String module) throws RepositoryException;
	
	/**
	 * Gets the resource content.
	 *
	 * @param root the root
	 * @param module the module
	 * @param extension the extension
	 * @return the resource content
	 * @throws RepositoryException the repository exception
	 */
	public byte[] getResourceContent(String root, String module, String extension) throws RepositoryException;
	
	/**
	 * Gets the collection.
	 *
	 * @param root the root
	 * @param module the module
	 * @return the collection
	 * @throws RepositoryException the repository exception
	 */
	public ICollection getCollection(String root, String module) throws RepositoryException;

	/**
	 * Gets the resource.
	 *
	 * @param root the root
	 * @param module the module
	 * @return the resource
	 * @throws RepositoryException the repository exception
	 */
	public IResource getResource(String root, String module) throws RepositoryException;
	
	/**
	 * Gets the resource.
	 *
	 * @param root the root
	 * @param module the module
	 * @param extension the extension
	 * @return the resource
	 * @throws RepositoryException the repository exception
	 */
	public IResource getResource(String root, String module, String extension) throws RepositoryException;
	
	/**
	 * Exist resource.
	 *
	 * @param root the root
	 * @param module the module
	 * @return true, if successful
	 * @throws RepositoryException the repository exception
	 */
	public boolean existResource(String root, String module) throws RepositoryException;
	
	/**
	 * Exist resource.
	 *
	 * @param root the root
	 * @param module the module
	 * @param extension the extension
	 * @return true, if successful
	 * @throws RepositoryException the repository exception
	 */
	public boolean existResource(String root, String module, String extension) throws RepositoryException;
	
	/**
	 * Creates the resource path.
	 *
	 * @param root the root
	 * @param module the module
	 * @return the string
	 */
	public String createResourcePath(String root, String module);
	
	/**
	 * Creates the resource path.
	 *
	 * @param root the root
	 * @param module the module
	 * @param extension the extension
	 * @return the string
	 */
	public String createResourcePath(String root, String module, String extension);

}
