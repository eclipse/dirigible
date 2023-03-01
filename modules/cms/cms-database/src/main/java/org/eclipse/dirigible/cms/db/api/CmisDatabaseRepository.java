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

import org.eclipse.dirigible.cms.db.CmsDatabaseRepository;

/**
 * The Class CmisDatabaseRepository.
 */
public class CmisDatabaseRepository implements CmisRepository {

	/** The database repository. */
	private CmsDatabaseRepository databaseRepository;

	/**
	 * Instantiates a new cmis database repository.
	 *
	 * @param repository the repository
	 */
	public CmisDatabaseRepository(CmsDatabaseRepository repository) {
		super();
		this.databaseRepository = repository;
	}

	/**
	 * Gets the session.
	 *
	 * @return the session
	 */
	@Override
	public CmisSession getSession() {
		return new CmisSession(this);
	}

	/**
	 * Gets the internal object.
	 *
	 * @return the internal object
	 */
	@Override
	public Object getInternalObject() {
		return this.databaseRepository;
	}

}
