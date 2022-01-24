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

import static java.text.MessageFormat.format;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IEntityInformation;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The file system based implementation of {@link IEntity}.
 */
public abstract class CmsDatabaseEntity implements IEntity {

	private static final Logger logger = LoggerFactory.getLogger(CmsDatabaseEntity.class);

	private transient final CmsDatabaseRepository repository;

	private final RepositoryPath path;

	/**
	 * Instantiates a new local entity.
	 *
	 * @param repository
	 *            the repository
	 * @param path
	 *            the path
	 */
	public CmsDatabaseEntity(CmsDatabaseRepository repository, RepositoryPath path) {
		super();
		this.repository = repository;
		this.path = path;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getRepository()
	 */
	@Override
	public CmsDatabaseRepository getRepository() {
		return this.repository;
	}

	/**
	 * Returns the path of this {@link IEntity} represented by an instance of
	 * {@link RepositoryPath}.
	 *
	 * @return the repository path location
	 */
	protected RepositoryPath getRepositoryPath() {
		return this.path;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getName()
	 */
	@Override
	public String getName() {
		return this.path.getLastSegment();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getPath()
	 */
	@Override
	public String getPath() {
		return this.path.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getParent()
	 */
	@Override
	public ICollection getParent() {
		final RepositoryPath parentPath = this.path.getParentPath();
		if (parentPath == null) {
			return null;
		}
		return new CmsDatabaseCollection(repository, parentPath);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getInformation()
	 */
	@Override
	public IEntityInformation getInformation() throws RepositoryReadException {
		return new CmsDatabaseEntityInformation(this.path, getLocalObjectSafe());
	}

	/**
	 * Returns the {@link CmsDatabaseObject} that matches this entity's path. If there is
	 * no such object in the real repository, then <code>null</code> is
	 * returned.
	 *
	 * @return the local object
	 * @throws RepositoryReadException
	 *             the repository read exception
	 */
	protected CmsDatabaseObject getLocalObject() throws RepositoryReadException {
		try {
			return this.repository.getRepositoryDao().getObjectByPath(getPath());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Returns the {@link CmsDatabaseObject} that matches this entity's path. If there is
	 * no such object in the real repository, then an {@link RepositoryNotFoundException} is
	 * thrown.
	 *
	 * @return the {@link CmsDatabaseObject} that matches this entity's path
	 * @throws RepositoryNotFoundException
	 *             If there is no such object in the real repository
	 */
	protected CmsDatabaseObject getLocalObjectSafe() throws RepositoryNotFoundException {
		final CmsDatabaseObject result = getLocalObject();
		if (result == null) {
			throw new RepositoryNotFoundException(format("There is no entity at path ''{0}''.", this.path.toString()));
		}
		return result;
	}

	/**
	 * Creates all ancestors of the given {@link IEntity} inside the
	 * repository if they don't already exist.
	 *
	 * @throws RepositoryWriteException
	 *             the repository write exception
	 */
	protected void createAncestorsIfMissing() throws RepositoryWriteException {
		final ICollection parent = getParent();
		if ((parent != null) && (!parent.exists())) {
			parent.create();
		}
	}

	/**
	 * Creates all ancestors of the given {@link IEntity} and itself too if
	 * they don't already exist.
	 *
	 * @throws RepositoryWriteException
	 *             the repository write exception
	 */
	protected void createAncestorsAndSelfIfMissing() throws RepositoryWriteException {
		createAncestorsIfMissing();
		if (!exists()) {
			create();
		}
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
		if (!(obj instanceof CmsDatabaseEntity)) {
			return false;
		}
		final CmsDatabaseEntity other = (CmsDatabaseEntity) obj;
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
