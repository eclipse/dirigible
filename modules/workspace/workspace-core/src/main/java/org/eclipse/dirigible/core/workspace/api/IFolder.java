/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.workspace.api;

import java.util.List;

import org.eclipse.dirigible.repository.api.ICollection;

/**
 * The Workspace's Folder interface.
 */
public interface IFolder extends ICollection {

	/**
	 * Gets the internal.
	 *
	 * @return the internal
	 */
	public ICollection getInternal();

	/**
	 * Creates the folder.
	 *
	 * @param path
	 *            the path
	 * @return the i folder
	 */
	public IFolder createFolder(String path);

	/**
	 * Exists folder.
	 *
	 * @param path
	 *            the path
	 * @return true, if successful
	 */
	public boolean existsFolder(String path);

	/**
	 * Gets the folder.
	 *
	 * @param path
	 *            the path
	 * @return the folder
	 */
	public IFolder getFolder(String path);

	/**
	 * Gets the folders.
	 *
	 * @return the folders
	 */
	public List<IFolder> getFolders();

	/**
	 * Delete folder.
	 *
	 * @param path
	 *            the path
	 */
	public void deleteFolder(String path);

	/**
	 * Creates the file.
	 *
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @return the i file
	 */
	public IFile createFile(String path, byte[] content);

	/**
	 * Creates the file.
	 *
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @param isBinary
	 *            the is binary
	 * @param contentType
	 *            the content type
	 * @return the i file
	 */
	public IFile createFile(String path, byte[] content, boolean isBinary, String contentType);

	/**
	 * Gets the file.
	 *
	 * @param path
	 *            the path
	 * @return the file
	 */
	public IFile getFile(String path);

	/**
	 * Exists file.
	 *
	 * @param path
	 *            the path
	 * @return true, if successful
	 */
	public boolean existsFile(String path);

	/**
	 * Gets the files.
	 *
	 * @return the files
	 */
	public List<IFile> getFiles();

	/**
	 * Delete file.
	 *
	 * @param path
	 *            the path
	 */
	public void deleteFile(String path);

	/**
	 * Free-text search.
	 *
	 * @param term            the term
	 * @return the list of files
	 */
	public List<IFile> search(String term);
	
	/**
	 * Find by file name pattern.
	 *
	 * @param pattern            the pattern
	 * @return the list of files
	 */
	public List<IFile> find(String pattern);
}
