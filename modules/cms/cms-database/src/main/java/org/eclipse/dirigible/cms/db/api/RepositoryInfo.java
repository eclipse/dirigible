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
package org.eclipse.dirigible.cms.db.api;

public class RepositoryInfo {

	private CmisSession session;

	public RepositoryInfo(CmisSession session) {
		super();
		this.session = session;
	}

	/**
	 * Returns the ID of the CMIS repository
	 *
	 * @return the Id
	 */
	public String getId() {
		return this.session.getCmisRepository().getInternalObject().getClass().getCanonicalName();
	}

	/**
	 * Returns the Name of the CMIS repository
	 *
	 * @return the Name
	 */
	public String getName() {
		return this.session.getCmisRepository().getInternalObject().getClass().getCanonicalName();
	}

}
