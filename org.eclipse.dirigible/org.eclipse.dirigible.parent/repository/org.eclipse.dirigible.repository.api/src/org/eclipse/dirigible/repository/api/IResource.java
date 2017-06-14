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
 * The <code>IResource</code> interface represents a resource located in the
 * repository.
 */
public interface IResource extends IEntity {

	public static final String CONTENT_TYPE_DEFAULT = "text/plain"; //$NON-NLS-1$

	/**
	 * Returns the content of the resource as a byte array.
	 *
	 * @return the raw content
	 * @throws IOException
	 *             in case of an error
	 */
	public byte[] getContent() throws IOException;

	/**
	 * Sets this resource's content.
	 *
	 * @param content
	 *            the raw content
	 * @throws IOException
	 *             in case of an error
	 */
	public void setContent(byte[] content) throws IOException;

	/**
	 * Sets this resource's content.
	 *
	 * @param content
	 *            the raw content
	 * @param isBinary
	 *            whether it is binary
	 * @param contentType
	 *            the content type
	 * @throws IOException
	 *             in case of an error
	 */
	public void setContent(byte[] content, boolean isBinary, String contentType) throws IOException;

	/**
	 * Getter for binary flag
	 *
	 * @return whether it is binary
	 */
	public boolean isBinary();

	/**
	 * Getter for the content type
	 *
	 * @return the content type
	 */
	public String getContentType();

	/**
	 * Retrieve all the kept versions of a given resource
	 *
	 * @return the list of {@link IResourceVersion} instances
	 * @throws IOException
	 *             in case of an error
	 */
	public List<IResourceVersion> getResourceVersions() throws IOException;

	/**
	 * Retrieve a particular version of a given resource
	 *
	 * @param version
	 *            the exact version
	 * @return the list of {@link IResourceVersion} instances
	 * @throws IOException
	 *             in case of an error
	 */
	public IResourceVersion getResourceVersion(int version) throws IOException;

}
