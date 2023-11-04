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
package org.eclipse.dirigible.components.engine.cms.internal.repository;

import java.io.IOException;

/**
 * The Class CmisSession.
 */
public class CmisSession {

	/** The cmis repository. */
	private CmisRepository cmisRepository;

	/**
	 * Instantiates a new cmis session.
	 *
	 * @param cmisRepository the cmis repository
	 */
	public CmisSession(CmisRepository cmisRepository) {
		super();
		this.cmisRepository = cmisRepository;
	}

	/**
	 * Gets the cmis repository.
	 *
	 * @return the cmis repository
	 */
	public CmisRepository getCmisRepository() {
		return cmisRepository;
	}

	/**
	 * Returns the information about the CMIS repository.
	 *
	 * @return Repository Info
	 */
	public RepositoryInfo getRepositoryInfo() {
		return new RepositoryInfo(this);
	}

	/**
	 * Returns the ObjectFactory utility.
	 *
	 * @return Object Factory
	 */
	public ObjectFactory getObjectFactory() {
		return new ObjectFactory(this);
	}

	/**
	 * Returns the root folder of this repository.
	 *
	 * @return Folder
	 * @throws IOException IO Exception
	 */
	public Folder getRootFolder() throws IOException {
		return new Folder(this);
	}

	/**
	 * Returns a CMIS Object by name.
	 *
	 * @param id the Id
	 * @return CMIS Object
	 * @throws IOException IO Exception
	 */
	public CmisObject getObject(String id) throws IOException {
		CmisObject cmisObject = new CmisObject(this, id);
		if (!cmisObject.getInternalEntity().exists()) {
			throw new IOException(String.format("Object with id: %s does not exist", id));
		}
		if (CmisConstants.OBJECT_TYPE_FOLDER.equals(cmisObject.getType().getId())) {
			return new Folder(this, id);
		} else if (CmisConstants.OBJECT_TYPE_DOCUMENT.equals(cmisObject.getType().getId())) {
			return new Document(this, id);
		}
		return cmisObject;
	}

	/**
	 * Returns a CMIS Object by path.
	 *
	 * @param path the path
	 * @return CMIS Object
	 * @throws IOException IO Exception
	 */
	public CmisObject getObjectByPath(String path) throws IOException {
		return getObject(path);
	}

}
