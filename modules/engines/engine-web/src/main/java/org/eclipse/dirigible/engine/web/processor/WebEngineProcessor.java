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
package org.eclipse.dirigible.engine.web.processor;

import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;

/**
 * Processing the incoming requests for the raw web content.
 * It supports only GET requests
 */
public class WebEngineProcessor {

	private WebEngineExecutor webEngineExecutor = new WebEngineExecutor();

	/**
	 * Exist resource.
	 *
	 * @param path
	 *            the requested resource location
	 * @return if the {@link IResource}
	 */
	public boolean existResource(String path) {
		return webEngineExecutor.existResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
	}

	/**
	 * Gets the resource.
	 *
	 * @param path
	 *            the requested resource location
	 * @return the {@link IResource} instance
	 */
	public IResource getResource(String path) {
		if (WebExposureManager.isPathExposed(path)) {
			return webEngineExecutor.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
		}
		return null;
	}

	/**
	 * Gets the resource content.
	 *
	 * @param path
	 *            the requested resource location
	 * @return the {@link IResource} content as a byte array
	 */
	public byte[] getResourceContent(String path) {
		if (WebExposureManager.isPathExposed(path)) {
			return webEngineExecutor.getResourceContent(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
		}
		return null;
	}

}
