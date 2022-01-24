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
package org.eclipse.dirigible.repository.db;

import java.util.Date;

import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;

/**
 * The file system based implementation of {@link IResourceVersion}.
 */
public class DatabaseResourceVersion implements IResourceVersion {

	private final DatabaseRepository repository;

	private final RepositoryPath path;

	private int version;

	private DatabaseFileVersion fileVersion;

	/**
	 * Instantiates a new local resource version.
	 *
	 * @param repository
	 *            the repository
	 * @param path
	 *            the path
	 * @param version
	 *            the version
	 * @throws RepositoryWriteException
	 *             the repository write exception
	 */
	public DatabaseResourceVersion(DatabaseRepository repository, RepositoryPath path, int version) throws RepositoryWriteException {
		super();
		this.repository = repository;
		this.path = path;
		this.version = version;
		this.fileVersion = getRepository().getRepositoryDao().getFileVersionByPath(getPath(), version);
	}

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	public DatabaseRepository getRepository() {
		return repository;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getPath()
	 */
	@Override
	public String getPath() {
		return this.path.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getVersion()
	 */
	@Override
	public int getVersion() {
		return this.version;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getContent()
	 */
	@Override
	public byte[] getContent() throws RepositoryReadException {
		return this.fileVersion.getData();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#isBinary()
	 */
	@Override
	public boolean isBinary() {
		return this.fileVersion.isBinary();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getContentType()
	 */
	@Override
	public String getContentType() {
		return this.fileVersion.getContentType();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getCreatedBy()
	 */
	@Override
	public String getCreatedBy() {
		return this.fileVersion.getCreatedBy();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getCreatedAt()
	 */
	@Override
	public Date getCreatedAt() {
		return this.fileVersion.getCreatedAt();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DatabaseResourceVersion)) {
			return false;
		}
		final DatabaseResourceVersion other = (DatabaseResourceVersion) obj;
		return getPath().equals(other.getPath());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(IResourceVersion o) {
		try {
			int x = this.getVersion();
			int y = o.getVersion();
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		} catch (NumberFormatException e) {
			return 0;
		}

	}

}
