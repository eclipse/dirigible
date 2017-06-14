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

	public static final String SEPARATOR = ICommonConstants.SEPARATOR;

	/**
	 * This method creates a new empty collection at the specified path.
	 * <p>
	 * The returned value is an instance of <code>ICollection</code> which
	 * represents the newly created collection.
	 *
	 * @param path
	 *            the location
	 * @return the created {@link ICollection} instance
	 * @throws IOException
	 */
	public ICollection createCollection(String path) throws IOException;

	/**
	 * This method removes the collection with the specified path from the
	 * repository.
	 *
	 * @param path
	 *            the location
	 * @throws IOException
	 *             in case of an error
	 */
	public void removeCollection(String path) throws IOException;

	/**
	 * This method creates a new empty resource at the specified path.
	 * <p>
	 * The returned value is an instance of <code>IResource</code> that
	 * represents the newly created resource.
	 *
	 * @param path
	 *            the location
	 * @return the created {@link IResource} instance
	 * @throws IOException
	 */
	public IResource createResource(String path) throws IOException;

	/**
	 * This method creates a new resource at the specified path and fills it
	 * with the specified content.
	 * <p>
	 * The returned value is an instance of <code>IResource</code> that
	 * represents the newly created resource.
	 *
	 * @param path
	 *            the location
	 * @param content
	 *            the raw content
	 * @return the created {@link IResource}
	 * @throws IOException
	 *             in case of an error
	 */
	public IResource createResource(String path, byte[] content) throws IOException;

	/**
	 * This method creates a new empty resource at the specified path.
	 * <p>
	 * The returned value is an instance of <code>IResource</code> that
	 * represents the newly created resource.
	 *
	 * @param path
	 *            the location
	 * @param content
	 *            the raw content
	 * @param isBinary
	 *            whether is binary
	 * @param contentType
	 *            type of the content
	 * @return the created {@link IResource}
	 * @throws IOException
	 *             in case of an error
	 */
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType) throws IOException;

	/**
	 * This method creates a new empty, or override resource at the specified path.
	 * <p>
	 * The returned value is an instance of <code>IResource</code> that
	 * represents the newly created resource.
	 *
	 * @param path
	 *            the location
	 * @param content
	 *            the raw content
	 * @param isBinary
	 *            whether is binary
	 * @param contentType
	 *            type of the content
	 * @param override
	 *            whether to override existing if any
	 * @return the created {@link IResource}
	 * @throws IOException
	 *             in case of an error
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
	 *            the input stream
	 * @param relativeRoot
	 *            the relative root
	 * @throws IOException
	 *             in case of an error
	 */
	public void importZip(ZipInputStream zipInputStream, String relativeRoot) throws IOException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root. Overrides the previous content depending on the override parameter.
	 *
	 * @param zipInputStream
	 *            the input stream
	 * @param relativeRoot
	 *            the relative root
	 * @param override
	 *            whether to override existing if any
	 * @throws IOException
	 *             in case of an error
	 */
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override) throws IOException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root. Overrides the previous content depending on the override parameter.
	 * Excludes the name of the root folder, during the import, based on the excludeRootFolderName parameter.
	 *
	 * @param zipInputStream
	 *            the input stream
	 * @param relativeRoot
	 *            the relative root
	 * @param override
	 *            whether to override existing if any
	 * @param excludeRootFolderName
	 *            whether to exclude the root folder
	 * @throws IOException
	 *             in case of an error
	 */
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override, boolean excludeRootFolderName) throws IOException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root
	 *
	 * @param data
	 *            the Zip file as byte array
	 * @param relativeRoot
	 *            the relative root
	 * @throws IOException
	 *             in case of an error
	 */
	public void importZip(byte[] data, String relativeRoot) throws IOException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root. Overrides the previous content depending on the override parameter.
	 *
	 * @param data
	 *            the Zip file as byte array
	 * @param relativeRoot
	 *            the relative root
	 * @param override
	 *            whether to override existing if any
	 * @throws IOException
	 *             in case of an error
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
	 *            the relative root
	 * @param override
	 *            whether to override existing if any
	 * @param filter
	 *            the filter
	 * @param excludeRootFolderName
	 *            whether to exclude the root folder
	 * @throws IOException
	 *             in case of an error
	 */
	public void importZip(byte[] data, String relativeRoot, boolean override, boolean excludeRootFolderName, Map<String, String> filter)
			throws IOException;

	/**
	 * Search the given parameter in the names of the files and folders ( means
	 * *parameter)
	 *
	 * @param parameter
	 *            the expression
	 * @param caseInsensitive
	 *            whether to be case insensitive
	 * @return the list of matching {@link IEntity} instances
	 * @throws IOException
	 *             in case of an error
	 */
	public List<IEntity> searchName(String parameter, boolean caseInsensitive) throws IOException;

	/**
	 * Search the given parameter in the names of the files and folders ( means
	 * *parameter) under specified root folder (means *root)
	 *
	 * @param root
	 *            the beginning of the search
	 * @param parameter
	 *            the expression
	 * @param caseInsensitive
	 *            whether to be case insensitive
	 * @return the list of matching {@link IEntity} instances
	 * @throws IOException
	 *             in case of an error
	 */
	public List<IEntity> searchName(String root, String parameter, boolean caseInsensitive) throws IOException;

	/**
	 * Search the given given parameter in the names of the files and folders
	 * (means *parameter*)
	 *
	 * @param parameter
	 *            the expression
	 * @param caseInsensitive
	 *            whether to be case insensitive
	 * @return the list of matching {@link IEntity} instances
	 * @throws IOException
	 *             in case of an error
	 */
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) throws IOException;

	/**
	 * Search the given given parameter in the names of the files and folders as
	 * well as in the content of the text files
	 *
	 * @param parameter
	 *            the expression
	 * @param caseInsensitive
	 *            whether to be case insensitive
	 * @return he list of matching {@link IEntity} instances
	 * @throws IOException
	 *             in case of an error
	 */
	public List<IEntity> searchText(String parameter, boolean caseInsensitive) throws IOException;

	/**
	 * Clean-up the file versions older than a month For full fledged SCM
	 * system, use external e.g. Git
	 *
	 * @throws IOException
	 *             in case of an error
	 */
	public void cleanupOldVersions() throws IOException;

}
