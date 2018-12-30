/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.repository.api;

import java.util.List;

/**
 * The interface containing the search related methods of the repository .
 */
public interface IRepositorySearch {

	/**
	 * Search the given parameter in the names of the files and folders ( means
	 * *parameter).
	 *
	 * @param parameter
	 *            the search text
	 * @param caseInsensitive
	 *            whether to be case insensitive
	 * @return a list of {@link IEntity} instances
	 * @throws RepositorySearchException
	 *             in case the search fails
	 */
	public List<IEntity> searchName(String parameter, boolean caseInsensitive) throws RepositorySearchException;

	/**
	 * Search the given parameter in the names of the files and folders ( means
	 * *parameter) under specified root folder (means *root).
	 *
	 * @param root
	 *            the root location to start the search from
	 * @param parameter
	 *            the search text
	 * @param caseInsensitive
	 *            whether to be case insensitive
	 * @return a list of {@link IEntity} instances
	 * @throws RepositorySearchException
	 *             in case the search fails
	 */
	public List<IEntity> searchName(String root, String parameter, boolean caseInsensitive) throws RepositorySearchException;

	/**
	 * Search the given given parameter in the names of the files and folders
	 * (means *parameter*).
	 *
	 * @param parameter
	 *            the search text
	 * @param caseInsensitive
	 *            whether to be case insensitive
	 * @return a list of {@link IEntity} instances
	 * @throws RepositorySearchException
	 *             in case the search fails
	 */
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) throws RepositorySearchException;

	/**
	 * Search the given given parameter in the names of the files and folders as
	 * well as in the content of the text files.
	 *
	 * @param term
	 *            the search text
	 * @return a list of {@link IEntity} instances
	 * @throws RepositorySearchException
	 *             in case the search fails
	 */
	public List<IEntity> searchText(String term) throws RepositorySearchException;

	/**
	 * Re-index the content
	 *
	 * @throws RepositorySearchException
	 */
	public void searchRefresh() throws RepositorySearchException;

}
