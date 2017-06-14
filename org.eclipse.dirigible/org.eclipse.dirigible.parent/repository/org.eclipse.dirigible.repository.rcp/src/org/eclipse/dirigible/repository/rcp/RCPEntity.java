/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.rcp;

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
public abstract class RCPEntity implements IEntity {

	private static final String THERE_IS_NO_ENTITY_AT_PATH_0 = Messages.getString("DBEntity.THERE_IS_NO_ENTITY_AT_PATH_0"); //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(RCPEntity.class);

	private final RCPRepository repository;

	private final RepositoryPath path;

	public RCPEntity(RCPRepository repository, RepositoryPath path) {
		super();
		this.repository = repository;
		this.path = path;
	}

	@Override
	public RCPRepository getRepository() {
		return this.repository;
	}

	/**
	 * Returns the path of this {@link IEntity} represented by an instance of
	 * {@link RepositoryPath}.
	 *
	 * @return Repository Path
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
		return new RCPCollection(repository, parentPath);
	}

	@Override
	public IEntityInformation getInformation() throws IOException {
		return new RCPEntityInformation(this.path, getRCPObjectSafe());
	}

	/**
	 * Returns the {@link RCPObject} that matches this entity's path. If there is
	 * no such object in the real repository, then <code>null</code> is
	 * returned.
	 *
	 * @return RCP Object
	 */
	protected RCPObject getRCPObject() {
		try {
			return this.repository.getRepositoryDAO().getObjectByPath(getPath());
		} catch (RCPBaseException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Returns the {@link RCPObject} that matches this entity's path. If there is
	 * no such object in the real repository, then an {@link IOException} is
	 * thrown.
	 *
	 * @return RCP Object
	 * @throws IOException
	 *             IO Exception
	 */
	protected RCPObject getRCPObjectSafe() throws IOException {
		final RCPObject result = getRCPObject();
		if (result == null) {
			throw new IOException(format(THERE_IS_NO_ENTITY_AT_PATH_0, this.path.toString()));
		}
		return result;
	}

	/**
	 * Creates all ancestors of the given {@link IEntity} inside the
	 * repository if they don't already exist.
	 *
	 * @throws IOException
	 *             IO Exception
	 */
	protected void createAncestorsIfMissing() throws IOException {
		final ICollection parent = getParent();
		if ((parent != null) && (!parent.exists())) {
			parent.create();
		}
	}

	/**
	 * Creates all ancestors of the given {@link IEntity} and itself too if
	 * they don't already exist.
	 *
	 * @throws IOException
	 *             IO Exception
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
		if (!(obj instanceof RCPEntity)) {
			return false;
		}
		final RCPEntity other = (RCPEntity) obj;
		return getPath().equals(other.getPath());
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

}
