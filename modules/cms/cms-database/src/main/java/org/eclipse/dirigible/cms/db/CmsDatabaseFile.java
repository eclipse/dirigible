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

/**
 * Internal representation of a File/Resource kind of object.
 */
public class CmsDatabaseFile extends CmsDatabaseObject {

	/** The binary. */
	private boolean binary = false;

	/** The content type. */
	private String contentType;

	/**
	 * Instantiates a new local file.
	 *
	 * @param repository
	 *            the repository
	 * @param isBinary
	 *            the is binary
	 * @param contentType
	 *            the content type
	 */
	public CmsDatabaseFile(CmsDatabaseRepository repository, boolean isBinary, String contentType) {
		super(repository);
		this.binary = isBinary;
		this.contentType = contentType;
	}

	/**
	 * Delete.
	 *
	 * @throws CmsDatabaseRepositoryException
	 *             the local repository exception
	 */
	public void delete() throws CmsDatabaseRepositoryException {
		try {
			getRepository().getRepositoryDao().removeFileByPath(getPath());
		} catch (SQLException e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Rename.
	 *
	 * @param newPath
	 *            the new path
	 * @throws CmsDatabaseRepositoryException
	 *             the local repository exception
	 */
	public void rename(String newPath) throws CmsDatabaseRepositoryException {
		try {
			getRepository().getRepositoryDao().renameFile(getPath(), newPath);
		} catch (SQLException e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Copy to.
	 *
	 * @param newPath
	 *            the new path
	 * @throws CmsDatabaseRepositoryException
	 *             the local repository exception
	 */
	public void copyTo(String newPath) throws CmsDatabaseRepositoryException {
		try {
			getRepository().getRepositoryDao().copyFile(getPath(), newPath);
		} catch (SQLException e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 * @throws CmsDatabaseRepositoryException
	 *             the local repository exception
	 */
	public byte[] getData() throws CmsDatabaseRepositoryException {
		return getRepository().getRepositoryDao().getFileContent(this);
	}

	/**
	 * Sets the data.
	 *
	 * @param content
	 *            the new data
	 * @throws CmsDatabaseRepositoryException
	 *             the local repository exception
	 */
	public void setData(byte[] content) throws CmsDatabaseRepositoryException {
		try {
			getRepository().getRepositoryDao().setFileContent(this, content);
		} catch (SQLException e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Checks if is binary.
	 *
	 * @return true, if is binary
	 */
	public boolean isBinary() {
		return binary;
	}

	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	public String getContentType() {
		return contentType;
	}
}
