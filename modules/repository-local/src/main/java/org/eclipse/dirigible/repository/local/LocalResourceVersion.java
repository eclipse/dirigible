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

/**
 * The file system based implementation of {@link IResourceVersion} 
 */
public class LocalResourceVersion implements IResourceVersion {

	private final FileSystemRepository repository;

	private final RepositoryPath path;

	private int version;

	private LocalFileVersion fileVersion;

	public LocalResourceVersion(FileSystemRepository repository, RepositoryPath path, int version) throws RepositoryWriteException {
		super();
		this.repository = repository;
		this.path = path;
		this.version = version;
		this.fileVersion = getRepository().getRepositoryDao().getFileVersionByPath(getPath(), version);
	}

	public FileSystemRepository getRepository() {
		return repository;
	}

	@Override
	public String getPath() {
		return this.path.toString();
	}

	@Override
	public int getVersion() {
		return this.version;
	}

	@Override
	public byte[] getContent() throws RepositoryReadException {
		return this.fileVersion.getData();
	}

	@Override
	public boolean isBinary() {
		return this.fileVersion.isBinary();
	}

	@Override
	public String getContentType() {
		return this.fileVersion.getContentType();
	}

	@Override
	public String getCreatedBy() {
		return this.fileVersion.getCreatedBy();
	}

	@Override
	public Date getCreatedAt() {
		return this.fileVersion.getCreatedAt();
	}

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

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

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
