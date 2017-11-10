/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.local;

import java.io.IOException;
import java.util.Date;

import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;

// TODO: Auto-generated Javadoc
/**
 * The file system based implementation of {@link IResourceVersion}.
 */
public class LocalResourceVersion implements IResourceVersion {

	/** The repository. */
	private final FileSystemRepository repository;

	/** The path. */
	private final RepositoryPath path;

	/** The version. */
	private int version;

	/** The file version. */
	private LocalFileVersion fileVersion;

	/**
	 * Instantiates a new local resource version.
	 *
	 * @param repository the repository
	 * @param path the path
	 * @param version the version
	 * @throws RepositoryWriteException the repository write exception
	 */
	public LocalResourceVersion(FileSystemRepository repository, RepositoryPath path, int version) throws RepositoryWriteException {
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
	public FileSystemRepository getRepository() {
		return repository;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getPath()
	 */
	@Override
	public String getPath() {
		return this.path.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getVersion()
	 */
	@Override
	public int getVersion() {
		return this.version;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getContent()
	 */
	@Override
	public byte[] getContent() throws RepositoryReadException {
		return this.fileVersion.getData();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#isBinary()
	 */
	@Override
	public boolean isBinary() {
		return this.fileVersion.isBinary();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getContentType()
	 */
	@Override
	public String getContentType() {
		return this.fileVersion.getContentType();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getCreatedBy()
	 */
	@Override
	public String getCreatedBy() {
		return this.fileVersion.getCreatedBy();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResourceVersion#getCreatedAt()
	 */
	@Override
	public Date getCreatedAt() {
		return this.fileVersion.getCreatedAt();
	}

	/* (non-Javadoc)
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
		if (!(obj instanceof LocalResourceVersion)) {
			return false;
		}
		final LocalResourceVersion other = (LocalResourceVersion) obj;
		return getPath().equals(other.getPath());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	/* (non-Javadoc)
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
