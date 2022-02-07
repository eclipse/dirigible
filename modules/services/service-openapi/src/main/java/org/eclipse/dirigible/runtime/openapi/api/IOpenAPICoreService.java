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
package org.eclipse.dirigible.runtime.openapi.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.runtime.openapi.definition.OpenAPIDefinition;

/**
 * The Interface IWebsocketsCoreService.
 */
public interface IOpenAPICoreService extends ICoreService {

	/** The Constant FILE_EXTENSION_OPENAPI. */
	public static final String FILE_EXTENSION_OPENAPI = ".openapi";


	
	// OpenAPI

	/**
	 * Creates the OpenAPI.
	 *
	 * @param location
	 *            the location
	 * @param hash
	 *            the hash
	 * @return the OpenAPI definition
	 * @throws OpenAPIException
	 *             the OpenAPI exception
	 */
	public OpenAPIDefinition createOpenAPI(String location, String hash) throws OpenAPIException;

	/**
	 * Gets the OpenAPI.
	 *
	 * @param location
	 *            the location
	 * @return the definition
	 * @throws OpenAPIException
	 *             the OpenAPI exception
	 */
	public OpenAPIDefinition getOpenAPI(String location) throws OpenAPIException;

	/**
	 * Exists OpenAPI.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws OpenAPIException
	 *             the OpenAPI exception
	 */
	public boolean existsOpenAPI(String location) throws OpenAPIException;

	/**
	 * Removes the OpenAPI.
	 *
	 * @param location
	 *            the location
	 * @throws OpenAPIException
	 *             the OpenAPI exception
	 */
	public void removeOpenAPI(String location) throws OpenAPIException;

	/**
	 * Update OpenAPI.
	 *
	 * @param location
	 *            the location
	 * @param hash
	 *            the hash
	 * @throws OpenAPIException
	 *             the OpenAPI exception
	 */
	public void updateOpenAPI(String location, String hash) throws OpenAPIException;

	/**
	 * Gets the OpenAPIs.
	 *
	 * @return the OpenAPI
	 * @throws OpenAPIException
	 *             the OpenAPI exception
	 */
	public List<OpenAPIDefinition> getOpenAPIs() throws OpenAPIException;

}
