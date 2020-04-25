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
package org.eclipse.dirigible.cms.internal;

import org.eclipse.dirigible.repository.api.IRepository;

public class CmisInternalRepository implements CmisRepository {

	private IRepository internalRepository;

	public CmisInternalRepository(IRepository internalRepository) {
		super();
		this.internalRepository = internalRepository;
	}

	@Override
	public CmisSession getSession() {
		return new CmisSession(this);
	}

	@Override
	public Object getInternalObject() {
		return this.internalRepository;
	}

}
