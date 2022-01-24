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

import org.eclipse.dirigible.repository.api.IEntityInformation;
import org.eclipse.dirigible.repository.api.RepositoryPath;

/**
 * The file system based implementation of {@link IEntityInformation}.
 */
public class DatabaseEntityInformation implements IEntityInformation {

	private RepositoryPath wrapperPath;

	private DatabaseObject master;

	private long size;

	/**
	 * Instantiates a new local entity information.
	 *
	 * @param wrapperPath
	 *            the wrapper path
	 * @param master
	 *            the master
	 */
	public DatabaseEntityInformation(RepositoryPath wrapperPath, DatabaseObject master) {
		this.wrapperPath = wrapperPath;
		this.master = master;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntityInformation#getName()
	 */
	@Override
	public String getName() {
		return this.wrapperPath.getLastSegment();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntityInformation#getPath()
	 */
	@Override
	public String getPath() {
		return this.wrapperPath.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntityInformation#getPermissions()
	 */
	@Override
	public int getPermissions() {
		return this.master.getPermissions();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntityInformation#getSize()
	 */
	@Override
	public Long getSize() {
		return this.size;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntityInformation#getCreatedBy()
	 */
	@Override
	public String getCreatedBy() {
		return this.master.getCreatedBy();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntityInformation#getCreatedAt()
	 */
	@Override
	public Date getCreatedAt() {
		return this.master.getCreatedAt();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntityInformation#getModifiedBy()
	 */
	@Override
	public String getModifiedBy() {
		return this.master.getModifiedBy();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntityInformation#getModifiedAt()
	 */
	@Override
	public Date getModifiedAt() {
		return this.master.getModifiedAt();
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
		if (!(obj instanceof DatabaseEntityInformation)) {
			return false;
		}
		final DatabaseEntityInformation other = (DatabaseEntityInformation) obj;
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

}
