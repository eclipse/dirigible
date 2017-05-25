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
public interface IRepository extends IReadOnlyRepository {

	public static final String SEPARATOR = IRepositoryConstants.SEPARATOR;
	
	public static final String DIRIGIBLE_REPOSITORY_PROVIDER = "DIRIGIBLE_REPOSITORY_PROVIDER"; //$NON-NLS-1$
	public static final String DIRIGIBLE_REPOSITORY_PROVIDER_LOCAL = "local"; //$NON-NLS-1$

	/**
	 * This method creates a new empty collection at the specified path.
	 * <p>
	 * The returned value is an instance of <code>ICollection</code> which
	 * represents the newly created collection.
	 */
	public ICollection createCollection(String path) throws IOException;

	/**
	 * This method removes the collection with the specified path from the
	 * repository.
	 */
	public void removeCollection(String path) throws IOException;

	/**
	 * This method creates a new empty resource at the specified path.
	 * <p>
	 * The returned value is an instance of <code>IResource</code> that
	 * represents the newly created resource.
	 */
	public IResource createResource(String path) throws IOException;

	/**
	 * This method creates a new resource at the specified path and fills it
	 * with the specified content.
	 * <p>
	 * The returned value is an instance of <code>IResource</code> that
	 * represents the newly created resource.
	 */
	public IResource createResource(String path, byte[] content) throws IOException;

	/**
	 * This method creates a new empty resource at the specified path.
	 * <p>
	 * The returned value is an instance of <code>IResource</code> that
	 * represents the newly created resource.
	 */
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType) throws IOException;

	/**
	 * This method creates a new empty, or override resource at the specified path.
	 * <p>
	 * The returned value is an instance of <code>IResource</code> that
	 * represents the newly created resource.
	 */
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType, boolean override) throws IOException;

	/**
	 * This method removes the resource at the specified path from the
	 * repository.
	 */
	public void removeResource(String path) throws IOException;

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
	 * Imports content from zip file to the repository, based on the relative
	 * root
	 *
	 * @param zipInputStream
	 * @param relativeRoot
	 * @throws IOException
	 */
	public void importZip(ZipInputStream zipInputStream, String relativeRoot) throws IOException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root. Overrides the previous content depending on the override parameter.
	 *
	 * @param zipInputStream
	 * @param relativeRoot
	 * @param override
	 * @throws IOException
	 */
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override) throws IOException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root. Overrides the previous content depending on the override parameter.
	 * Excludes the name of the root folder, during the import, based on the excludeRootFolderName parameter.
	 *
	 * @param zipInputStream
	 * @param relativeRoot
	 * @param override
	 * @param excludeRootFolderName
	 * @throws IOException
	 */
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override, boolean excludeRootFolderName) throws IOException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root
	 *
	 * @param data
	 *            the Zip file as byte array
	 * @param relativeRoot
	 * @throws IOException
	 */
	public void importZip(byte[] data, String relativeRoot) throws IOException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root. Overrides the previous content depending on the override parameter.
	 *
	 * @param data
	 *            the Zip file as byte array
	 * @param relativeRoot
	 * @param override
	 * @throws IOException
	 */
	public void importZip(byte[] data, String relativeRoot, boolean override) throws IOException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root. Overrides the previous content depending on the override parameter.
	 * Excludes the name of the root folder, during the import, based on the excludeRootFolderName parameter.
	 *
	 * @param data
	 *            the Zip file as byte array
	 * @param relativeRoot
	 * @param override
	 * @param filter
	 * @param excludeRootFolderName
	 * @throws IOException
	 */
	public void importZip(byte[] data, String relativeRoot, boolean override, boolean excludeRootFolderName, Map<String, String> filter)
			throws IOException;

	/**
	 * Search the given parameter in the names of the files and folders ( means
	 * *parameter)
	 *
	 * @param parameter
	 * @param caseInsensitive
	 * @return
	 * @throws IOException
	 */
	public List<IEntity> searchName(String parameter, boolean caseInsensitive) throws IOException;

	/**
	 * Search the given parameter in the names of the files and folders ( means
	 * *parameter) under specified root folder (means *root)
	 *
	 * @param root
	 * @param parameter
	 * @param caseInsensitive
	 * @return
	 * @throws IOException
	 */
	public List<IEntity> searchName(String root, String parameter, boolean caseInsensitive) throws IOException;

	/**
	 * Search the given given parameter in the names of the files and folders
	 * (means *parameter*)
	 *
	 * @param parameter
	 * @param caseInsensitive
	 * @return
	 * @throws IOException
	 */
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) throws IOException;

	/**
	 * Search the given given parameter in the names of the files and folders as
	 * well as in the content of the text files
	 *
	 * @param parameters
	 * @param caseInsensitive
	 * @return
	 * @throws IOException
	 */
	public List<IEntity> searchText(String parameter, boolean caseInsensitive) throws IOException;

	/**
	 * Clean-up the file versions older than a month For full fledged SCM
	 * system, use external e.g. Git
	 *
	 * @throws IOException
	 */
	public void cleanupOldVersions() throws IOException;

}
