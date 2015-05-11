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
import java.util.Date;

public interface IResourceVersion {

	/**
	 * Returns the path of this resource version
	 * <p>
	 * The result may not be <code>null</code>.
	 * <p>
	 * Example: /db/users/test.txt <br>
	 * Example: /db/articles
	 */
	public String getPath();

	/**
	 * Returns the version number
	 * 
	 * @return
	 */
	public int getVersion();

	/**
	 * Returns the content of the resource version as a byte array.
	 */
	public byte[] getContent() throws IOException;

	/**
	 * Getter for binary flag
	 * 
	 * @return
	 */
	public boolean isBinary();

	/**
	 * Getter for the content type
	 * 
	 * @return
	 */
	public String getContentType();

	/**
	 * The creator of the entity
	 * 
	 * @return
	 */
	public String getCreatedBy();

	/**
	 * Timestamp of the creation of the entity
	 * 
	 * @return
	 */
	public Date getCreatedAt();

}
