/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.cms.db.api;

import java.io.IOException;

import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;

/**
 * The Class CmisObject.
 */
public class CmisObject {

	/** The session. */
	private CmisSession session;

	/** The internal entity. */
	private IEntity internalEntity;

	/** The type collection. */
	private boolean typeCollection = false;

	/**
	 * Instantiates a new cmis object.
	 *
	 * @param session the session
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public CmisObject(CmisSession session, String path) throws IOException {
		super();
		this.session = session;
		path = sanitize(path);
		IRepository repository = ((IRepository) session.getCmisRepository().getInternalObject());
		if (repository.hasCollection(path)) {
			this.internalEntity = repository.getCollection(path);
			this.typeCollection = true;
		} else if (repository.hasResource(path)) {
			this.internalEntity = repository.getResource(path);
			this.typeCollection = false;
		}
	}

	/**
	 * Sanitize.
	 *
	 * @param path the path
	 * @return the string
	 */
	protected String sanitize(String path) {
		return path.replace("\\", "");
	}

	/**
	 * Gets the internal entity.
	 *
	 * @return the internal entity
	 */
	public IEntity getInternalEntity() {
		return internalEntity;
	}

	/**
	 * Returns the ID of this CmisObject.
	 *
	 * @return the Id
	 */
	public String getId() {
		return this.getInternalEntity().getPath();
	}

	/**
	 * Returns the Name of this CmisObject.
	 *
	 * @return the name
	 */
	public String getName() {
		if ("".equals(this.getInternalEntity().getName())) {
			return "root";
		}
		return this.getInternalEntity().getName();
	}

	/**
	 * Returns the Type of this CmisObject.
	 *
	 * @return the object type
	 */
	public ObjectType getType() {
		return this.isCollection() ? ObjectType.FOLDER : ObjectType.DOCUMENT;
	}

	/**
	 * Checks if is collection.
	 *
	 * @return true, if is collection
	 */
	protected boolean isCollection() {
		return typeCollection;
	}

	/**
	 * Delete this CmisObject.
	 *
	 * @throws IOException IO Exception
	 */
	public void delete() throws IOException {
		this.getInternalEntity().delete();
	}

	/**
	 * Delete this CmisObject.
	 *
	 * @param allVersions whether to delete all versions
	 * @throws IOException IO Exception
	 */
	public void delete(boolean allVersions) throws IOException {
		delete();
	}

	/**
	 * Rename this CmisObject.
	 *
	 * @param newName the new name
	 * @throws IOException IO Exception
	 */
	public void rename(String newName) throws IOException {
		this.getInternalEntity().renameTo(newName);
	}

}
