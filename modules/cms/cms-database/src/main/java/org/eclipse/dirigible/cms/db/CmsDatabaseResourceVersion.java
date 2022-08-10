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

import java.util.Date;

import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;

/**
 * The file system based implementation of {@link IResourceVersion}.
 */
public class CmsDatabaseResourceVersion implements IResourceVersion {

	/** The repository. */
	private final CmsDatabaseRepository repository;

	/** The path. */
	private final RepositoryPath path;

	/** The version. */
	private int version;

	/** The file version. */
	private CmsDatabaseFileVersion fileVersion;

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
	public CmsDatabaseResourceVersion(CmsDatabaseRepository repository, RepositoryPath path, int version) throws RepositoryWriteException {
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
	public CmsDatabaseRepository getRepository() {
		return repository;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getPath()
	 */
	@Override
	public String getPath() {
		return this.path.toString();
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getVersion()
	 */
	@Override
	public int getVersion() {
		return this.version;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 * @throws RepositoryReadException the repository read exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getContent()
	 */
	@Override
	public byte[] getContent() throws RepositoryReadException {
		return this.fileVersion.getData();
	}

	/**
	 * Checks if is binary.
	 *
	 * @return true, if is binary
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#isBinary()
	 */
	@Override
	public boolean isBinary() {
		return this.fileVersion.isBinary();
	}

	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getContentType()
	 */
	@Override
	public String getContentType() {
		return this.fileVersion.getContentType();
	}

	/**
	 * Gets the created by.
	 *
	 * @return the created by
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getCreatedBy()
	 */
	@Override
	public String getCreatedBy() {
		return this.fileVersion.getCreatedBy();
	}

	/**
	 * Gets the created at.
	 *
	 * @return the created at
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getCreatedAt()
	 */
	@Override
	public Date getCreatedAt() {
		return this.fileVersion.getCreatedAt();
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
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
		if (!(obj instanceof CmsDatabaseResourceVersion)) {
			return false;
		}
		final CmsDatabaseResourceVersion other = (CmsDatabaseResourceVersion) obj;
		return getPath().equals(other.getPath());
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	/**
	 * Compare to.
	 *
	 * @param o the o
	 * @return the int
	 */
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
