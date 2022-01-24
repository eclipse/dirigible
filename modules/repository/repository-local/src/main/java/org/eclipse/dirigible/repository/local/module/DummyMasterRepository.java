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
package org.eclipse.dirigible.repository.local.module;

import java.util.List;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryInitializationException;
import org.eclipse.dirigible.repository.api.RepositoryReadException;

public class DummyMasterRepository implements IMasterRepository {

	@Override
	public void initialize() throws RepositoryInitializationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ICollection getRoot() {
		return null;
	}

	@Override
	public ICollection getCollection(String path) {
		return null;
	}

	@Override
	public boolean hasCollection(String path) throws RepositoryReadException {
		return false;
	}

	@Override
	public IResource getResource(String path) {
		return null;
	}

	@Override
	public boolean hasResource(String path) throws RepositoryReadException {
		return false;
	}

	@Override
	public List<String> getAllResourcePaths() throws RepositoryReadException {
		return null;
	}

}
