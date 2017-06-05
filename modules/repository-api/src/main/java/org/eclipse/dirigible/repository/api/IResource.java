/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.api;

import java.io.IOException;
import java.util.List;

/**
 * The <code>IResource</code> interface represents a resource located in the
 * repository.
 * 
 */
public interface IResource extends IEntity {

	/**
	 * The default content type - text/plain
	 */
	public static final String CONTENT_TYPE_DEFAULT = "text/plain"; //$NON-NLS-1$

	/**
	 * Returns the content of the resource as a byte array.
	 * @return the raw content
	 * @throws RepositoryReadException in case the content cannot be retreived
	 */
	public byte[] getContent() throws RepositoryReadException;

	/**
	 * Sets this resource's content.
	 * 
	 * @param content the raw content
	 * @throws RepositoryWriteException 
	 */
	public void setContent(byte[] content) throws RepositoryWriteException;

	/**
	 * Sets this resource's content.
	 * 
	 * @param content the raw content
	 * @param isBinary whether it is binary
	 * @param contentType the type of the content
	 * @throws RepositoryWriteException in case the content of the {@link IResource} cannot be retreived
	 */
	public void setContent(byte[] content, boolean isBinary, String contentType)
			throws RepositoryWriteException;

	/**
	 * Getter for binary flag
	 * 
	 * @return whether it is binary
	 */
	public boolean isBinary();

	/**
	 * Getter for the content type
	 * 
	 * @return the type of the content
	 */
	public String getContentType();

	/**
	 * Retrieve all the kept versions of a given resource
	 * 
	 * @return the list of the {@link IResourceVersion} for this {link IResource}
	 * @throws RepositoryVersioningException
	 */
	public List<IResourceVersion> getResourceVersions() throws RepositoryVersioningException;

	/**
	 * Retrieve a particular version of a given resource
	 * 
	 * @param version the exact version
	 * @return the {@link IResourceVersion} for this {link IResource} for this version
	 * @throws RepositoryVersioningException
	 */
	public IResourceVersion getResourceVersion(int version) throws RepositoryVersioningException;

}
