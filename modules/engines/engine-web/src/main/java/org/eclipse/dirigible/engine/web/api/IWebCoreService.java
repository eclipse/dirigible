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
package org.eclipse.dirigible.engine.web.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.engine.web.models.WebModel;

/**
 * The Web Core Service interface.
 */
public interface IWebCoreService extends ICoreService {

	/**  The project json file name. */
	public String FILE_PROJECT_JSON = "project.json";

	
	// project.json

	/**
	 * Creates the project.json.
	 *
	 * @param location            the location
	 * @param name            the name
	 * @param exposed the exposed
	 * @param hash            the hash
	 * @return the project.json model
	 * @throws WebCoreException             the web exception
	 */
	public WebModel createWeb(String location, String name, String exposed, String hash) throws WebCoreException;

	/**
	 * Gets the project.json.
	 *
	 * @param location
	 *            the location
	 * @return the project.json
	 * @throws WebCoreException
	 *             the web exception
	 */
	public WebModel getWeb(String location) throws WebCoreException;
	
	/**
	 * Getter for the Web by its name.
	 *
	 * @param name the name
	 * @return the model
	 * @throws WebCoreException the web core exception
	 */
	public WebModel getWebByName(String name) throws WebCoreException;

	/**
	 * Exists project.json.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws WebCoreException
	 *             the web exception
	 */
	public boolean existsWeb(String location) throws WebCoreException;

	/**
	 * Removes the project.json.
	 *
	 * @param location
	 *            the location
	 * @throws WebCoreException
	 *             the web exception
	 */
	public void removeWeb(String location) throws WebCoreException;

	/**
	 * Update project.json.
	 *
	 * @param location            the location
	 * @param name            the name
	 * @param exposed the exposed
	 * @param hash            the hash
	 * @throws WebCoreException             the web exception
	 */
	public void updateWeb(String location, String name, String exposed, String hash) throws WebCoreException;

	/**
	 * Gets the project.json files.
	 *
	 * @return the project.json files
	 * @throws WebCoreException
	 *             the web exception
	 */
	public List<WebModel> getWebs() throws WebCoreException;

	/**
	 * Parses the project.json.
	 *
	 * @param path the path
	 * @param json the content
	 * @return the project.json model
	 */
	public WebModel parseProject(String path, String json);

	/**
	 * Parses the project.json.
	 *
	 * @param path the path
	 * @param json the content
	 * @return the project.json model
	 */
	public WebModel parseWeb(String path, byte[] json);

	/**
	 * Serialize project.json.
	 *
	 * @param webModel
	 *            the web model
	 * @return the string
	 */
	public String serializeWeb(WebModel webModel);

}
