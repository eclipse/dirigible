/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.local;

import java.util.Date;

import org.eclipse.dirigible.repository.api.IEntityInformation;
import org.eclipse.dirigible.repository.api.RepositoryPath;

/**
 * The DB implementation of {@link IEntityInformation}
 */
public class LocalEntityInformation implements IEntityInformation {

	private RepositoryPath wrapperPath;

	private LocalObject master;

	private long size;

	public LocalEntityInformation(RepositoryPath wrapperPath, LocalObject master) {
		this.wrapperPath = wrapperPath;
		this.master = master;
	}

	@Override
	public String getName() {
		return this.wrapperPath.getLastSegment();
	}

	@Override
	public String getPath() {
		return this.wrapperPath.toString();
	}

	@Override
	public int getPermissions() {
		return this.master.getPermissions();
	}

	@Override
	public Long getSize() {
		return this.size;
	}

	@Override
	public String getCreatedBy() {
		return this.master.getCreatedBy();
	}

	@Override
	public Date getCreatedAt() {
		return this.master.getCreatedAt();
	}

	@Override
	public String getModifiedBy() {
		return this.master.getModifiedBy();
	}

	@Override
	public Date getModifiedAt() {
		return this.master.getModifiedAt();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof LocalEntityInformation)) {
			return false;
		}
		final LocalEntityInformation other = (LocalEntityInformation) obj;
		return getPath().equals(other.getPath());
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

}
