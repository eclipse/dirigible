/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.cms.db;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.dirigible.repository.api.RepositoryPath;

/**
 * Internal representation of a Folder/Collection kind of object.
 */
public class CmsDatabaseFolder extends CmsDatabaseObject {

	/**
	 * Instantiates a new local folder.
	 *
	 * @param repository
	 *            the repository
	 */
	public CmsDatabaseFolder(CmsDatabaseRepository repository) {
		super(repository);
	}

	/**
	 * Delete tree.
	 *
	 * @throws CmsDatabaseRepositoryException
	 *             the local repository exception
	 */
	public void deleteTree() throws CmsDatabaseRepositoryException {
		try {
			getRepository().getRepositoryDao().removeFolderByPath(getPath());
		} catch (SQLException e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 * @throws CmsDatabaseRepositoryException
	 *             the local repository exception
	 */
	public List<CmsDatabaseObject> getChildren() throws CmsDatabaseRepositoryException {
		List<CmsDatabaseObject> result;
		try {
			result = getRepository().getRepositoryDao().getChildrenByFolder(getPath());
		} catch (SQLException e) {
			throw new CmsDatabaseRepositoryException(e);
		}
		return result;
	}

	/**
	 * Creates the folder.
	 *
	 * @param name
	 *            the name
	 * @throws CmsDatabaseRepositoryException
	 *             the local repository exception
	 */
	public void createFolder(String name) throws CmsDatabaseRepositoryException {
		try {
			getRepository().getRepositoryDao().createFolder(RepositoryPath.normalizePath(getPath(), name));
		} catch (SQLException e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Creates the file.
	 *
	 * @param name
	 *            the name
	 * @param content
	 *            the content
	 * @param isBinary
	 *            the is binary
	 * @param contentType
	 *            the content type
	 * @throws CmsDatabaseRepositoryException
	 *             the local repository exception
	 */
	public void createFile(String name, byte[] content, boolean isBinary, String contentType) throws CmsDatabaseRepositoryException {
		getRepository().getRepositoryDao().createFile(RepositoryPath.normalizePath(getPath(), name), content, isBinary, contentType);
	}

	/**
	 * Rename folder.
	 *
	 * @param newPath
	 *            the new path
	 * @throws CmsDatabaseRepositoryException
	 *             the local repository exception
	 */
	public void renameFolder(String newPath) throws CmsDatabaseRepositoryException {
		try {
			getRepository().getRepositoryDao().renameFolder(getPath(), newPath);
		} catch (SQLException e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Copy folder.
	 *
	 * @param newPath
	 *            the new path
	 * @throws CmsDatabaseRepositoryException
	 *             the local repository exception
	 */
	public void copyFolder(String newPath) throws CmsDatabaseRepositoryException {
		getRepository().getRepositoryDao().copyFolder(getPath(), newPath);
	}

}
