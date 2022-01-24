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
package org.eclipse.dirigible.api.v3.indexing;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.core.indexing.api.IIndexingCoreService;
import org.eclipse.dirigible.core.indexing.api.IndexingException;
import org.eclipse.dirigible.core.indexing.service.IndexingCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class IndexingFacade.
 */
public class IndexingFacade implements IScriptingFacade {

	private static final Logger logger = LoggerFactory.getLogger(IndexingFacade.class);

	private static final IIndexingCoreService indexingCoreService = new IndexingCoreService();

	/**
	 * Adds an index.
	 *
	 * @param index the index
	 * @param location the location
	 * @param contents the contents
	 * @param lastModified the last modified
	 * @param parameters the parameters
	 * @throws IndexingException the indexing exception
	 */
	public static final void add(String index, String location, String contents, String lastModified, String parameters) throws IndexingException {
		Map map = GsonHelper.GSON.fromJson(parameters, Map.class);
		indexingCoreService.add(index, location, contents.getBytes(StandardCharsets.UTF_8), Long.parseLong(lastModified), map);
	}

	/**
	 * Search an index by term.
	 *
	 * @param index the index
	 * @param term the term
	 * @return the values as JSON
	 * @throws IndexingException the indexing exception
	 */
	public static final String search(String index, String term) throws IndexingException {
		return indexingCoreService.search(index, term);
	}

	/**
	 * Search an index by date before.
	 *
	 * @param index the index
	 * @param date the date
	 * @return the values as JSON
	 * @throws IndexingException the indexing exception
	 */
	public static final String before(String index, String date) throws IndexingException {
		return indexingCoreService.before(index, Long.parseLong(date));
	}

	/**
	 * Search an index by date after.
	 *
	 * @param index the index
	 * @param date the date
	 * @return the values as JSON
	 * @throws IndexingException the indexing exception
	 */
	public static final String after(String index, String date) throws IndexingException {
		return indexingCoreService.after(index, Long.parseLong(date));
	}

	/**
	 * Search an index by date between.
	 *
	 * @param index the index
	 * @param lower the lower
	 * @param upper the upper
	 * @return the values as JSON
	 * @throws IndexingException the indexing exception
	 */
	public static final String between(String index, String lower, String upper) throws IndexingException {
		return indexingCoreService.between(index, Long.parseLong(lower), Long.parseLong(upper));
	}

}