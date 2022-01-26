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
package org.eclipse.dirigible.cms.internal;

import java.io.IOException;

import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;

public class CmisObject {

	private CmisSession session;

	private IEntity internalEntity;

	private boolean typeCollection = false;

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

	protected String sanitize(String path) {
		return path.replace("\\", "");
	}

	public IEntity getInternalEntity() {
		return internalEntity;
	}

	/**
	 * Returns the ID of this CmisObject
	 *
	 * @return the Id
	 */
	public String getId() {
		return this.getInternalEntity().getPath();
	}

	/**
	 * Returns the Name of this CmisObject
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
	 * Returns the Type of this CmisObject
	 *
	 * @return the object type
	 */
	public ObjectType getType() {
		return this.isCollection() ? ObjectType.FOLDER : ObjectType.DOCUMENT;
	}

	protected boolean isCollection() {
		return typeCollection;
	}

	/**
	 * Delete this CmisObject
	 *
	 * @throws IOException IO Exception
	 */
	public void delete() throws IOException {
		this.getInternalEntity().delete();
	}

	/**
	 * Delete this CmisObject
	 *
	 * @param allVersions whether to delete all versions
	 * 
	 * @throws IOException IO Exception
	 */
	public void delete(boolean allVersions) throws IOException {
		delete();
	}

	/**
	 * Rename this CmisObject
	 * 
	 * @param newName the new name
	 *
	 * @throws IOException IO Exception
	 */
	public void rename(String newName) throws IOException {
		this.getInternalEntity().renameTo(newName);
	}

}
