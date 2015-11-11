/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.db;

import java.io.IOException;
import java.util.Date;

import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.db.dao.DBFileVersion;
import org.eclipse.dirigible.repository.db.dao.DBObject;

public class DBResourceVersion implements IResourceVersion {

	private final DBRepository repository;

	private final RepositoryPath path;

	private int version;

	private DBFileVersion fileVersion;

	public DBResourceVersion(DBRepository repository, RepositoryPath path, int version) {
		super();
		this.repository = repository;
		this.path = path;
		this.version = version;
		this.fileVersion = getRepository().getRepositoryDAO().getFileVersionByPath(getPath(), version);
	}

	public DBRepository getRepository() {
		return repository;
	}

	@Override
	public String getPath() {
		return this.path.toString();
	}

	protected DBObject getDBObject() throws IOException {
		return this.fileVersion;
	}

	@Override
	public int getVersion() {
		return this.version;
	}

	@Override
	public byte[] getContent() throws IOException {
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
		if (!(obj instanceof DBResourceVersion)) {
			return false;
		}
		final DBResourceVersion other = (DBResourceVersion) obj;
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
