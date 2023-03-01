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
package org.eclipse.dirigible.engine.wiki.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.engine.wiki.definition.WikiDefinition;

/**
 * The Interface IWebsocketsCoreService.
 */
public interface IWikiCoreService extends ICoreService {

	/** The Constant FILE_EXTENSION_MD. */
	public static final String FILE_EXTENSION_MD = ".md";
	
	/** The Constant FILE_EXTENSION_MARKDOWN. */
	public static final String FILE_EXTENSION_MARKDOWN = ".markdown";
	
	/** The Constant FILE_EXTENSION_CONFLUENCE. */
	public static final String FILE_EXTENSION_CONFLUENCE = ".confluence";
	


	
	// Wiki definition

	/**
	 * Creates the Wiki definition.
	 *
	 * @param location
	 *            the location
	 * @param hash
	 *            the hash
	 * @return the WikiDefinition definition
	 * @throws WikiException
	 *             the Wiki exception
	 */
	public WikiDefinition createWiki(String location, String hash) throws WikiException;

	/**
	 * Gets the Wiki.
	 *
	 * @param location
	 *            the location
	 * @return the definition
	 * @throws WikiException
	 *             the Wiki exception
	 */
	public WikiDefinition getWiki(String location) throws WikiException;

	/**
	 * Exists Wiki.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws WikiException
	 *             the Wiki exception
	 */
	public boolean existsWiki(String location) throws WikiException;

	/**
	 * Removes the Wiki.
	 *
	 * @param location
	 *            the location
	 * @throws WikiException
	 *             the Wiki exception
	 */
	public void removeWiki(String location) throws WikiException;

	/**
	 * Update Wiki.
	 *
	 * @param location
	 *            the location
	 * @param hash
	 *            the hash
	 * @throws WikiException
	 *             the Wiki exception
	 */
	public void updateWiki(String location, String hash) throws WikiException;

	/**
	 * Gets the Wikis.
	 *
	 * @return the Wiki
	 * @throws WikiException
	 *             the Wiki exception
	 */
	public List<WikiDefinition> getWikis() throws WikiException;

}
