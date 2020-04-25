/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.cms.db.api;

import org.eclipse.dirigible.cms.db.CmsDatabaseRepository;

public class CmisDatabaseRepository implements CmisRepository {

	private CmsDatabaseRepository databaseRepository;

	public CmisDatabaseRepository(CmsDatabaseRepository repository) {
		super();
		this.databaseRepository = repository;
	}

	@Override
	public CmisSession getSession() {
		return new CmisSession(this);
	}

	@Override
	public Object getInternalObject() {
		return this.databaseRepository;
	}

}
