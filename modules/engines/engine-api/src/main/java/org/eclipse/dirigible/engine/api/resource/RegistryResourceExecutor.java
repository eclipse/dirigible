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
package org.eclipse.dirigible.engine.api.resource;

import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryException;

/**
 * The Class RegistryResourceExecutor.
 */
public class RegistryResourceExecutor extends AbstractResourceExecutor {
	
	/** The Constant ENGINE_TYPE. */
	public static final String ENGINE_TYPE = "registry";
	
	/** The Constant ENGINE_NAME. */
	public static final String ENGINE_NAME = "Registry Content Engine";

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IEngineExecutor#getType()
	 */
	@Override
	public String getType() {
		return ENGINE_TYPE;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IEngineExecutor#getName()
	 */
	@Override
	public String getName() {
		return ENGINE_NAME;
	}
	
	/**
	 * Gets the registry content.
	 *
	 * @param path the path
	 * @return the registry content
	 */
	public byte[] getRegistryContent(String path) {
		try {
			return this.getResourceContent(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
		} catch (RepositoryException e) {
			return null;
		}
	}

}
