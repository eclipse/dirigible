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

import static java.text.MessageFormat.format;

import java.io.IOException;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IEntityInformation;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * The DB implementation of {@link IEntity}
 */
public abstract class LocalEntity implements IEntity {

	private static final String THERE_IS_NO_ENTITY_AT_PATH_0 = "There is no entity at path ''{0}''."; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(LocalEntity.class);

	private final FileSystemRepository repository;

	private final RepositoryPath path;

	public LocalEntity(FileSystemRepository repository, RepositoryPath path) {
		super();
		this.repository = repository;
		this.path = path;
	}

	@Override
	public FileSystemRepository getRepository() {
		return this.repository;
	}

	/**
	 * Returns the path of this {@link Entity} represented by an instance of
	 * {@link RepositoryPath}.
	 */
	protected RepositoryPath getRepositoryPath() {
		return this.path;
	}

	@Override
	public String getName() {
		return this.path.getLastSegment();
	}

	@Override
	public String getPath() {
		return this.path.toString();
	}

	@Override
	public ICollection getParent() {
		final RepositoryPath parentPath = this.path.getParentPath();
		if (parentPath == null) {
			return null;
		}
		return new LocalCollection(repository, parentPath);
	}

	@Override
	public IEntityInformation getInformation() throws IOException {
		return new LocalEntityInformation(this.path, getLocalObjectSafe());
	}

	/**
	 * Returns the {@link LocalObject} that matches this entity's path. If there is
	 * no such object in the real repository, then <code>null</code> is
	 * returned.
	 */
	protected LocalObject getLocalObject() throws IOException {
		try {
			return this.repository.getRepositoryDAO().getObjectByPath(getPath());
		} catch (LocalBaseException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Returns the {@link LocalObject} that matches this entity's path. If there is
	 * no such object in the real repository, then an {@link IOException} is
	 * thrown.
	 */
	protected LocalObject getLocalObjectSafe() throws IOException {
		final LocalObject result = getLocalObject();
		if (result == null) {
			throw new IOException(format(THERE_IS_NO_ENTITY_AT_PATH_0, this.path.toString()));
		}
		return result;
	}

	/**
	 * Creates all ancestors of the given {@link CMISEntity} inside the
	 * repository if they don't already exist.
	 */
	protected void createAncestorsIfMissing() throws IOException {
		final ICollection parent = getParent();
		if ((parent != null) && (!parent.exists())) {
			parent.create();
		}
	}

	/**
	 * Creates all ancestors of the given {@link CMISEntity} and itself too if
	 * they don't already exist.
	 */
	protected void createAncestorsAndSelfIfMissing() throws IOException {
		createAncestorsIfMissing();
		if (!exists()) {
			create();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof LocalEntity)) {
			return false;
		}
		final LocalEntity other = (LocalEntity) obj;
		return getPath().equals(other.getPath());
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

}
