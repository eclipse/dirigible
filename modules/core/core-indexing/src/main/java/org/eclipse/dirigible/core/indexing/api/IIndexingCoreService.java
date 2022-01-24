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
package org.eclipse.dirigible.core.indexing.api;

import java.util.Map;

import org.eclipse.dirigible.commons.api.service.ICoreService;

/**
 * The Interface IIndexingCoreService.
 */
public interface IIndexingCoreService extends ICoreService {

	/**
	 * Adds an index.
	 *
	 * @param index
	 *            the index
	 * @param location
	 *            the location
	 * @param contents
	 *            the contents
	 * @param lastModified
	 *            the last modified
	 * @param parameters
	 *            the parameters
	 * @throws IndexingException
	 *             the indexing exception
	 */
	public void add(String index, String location, byte[] contents, long lastModified, Map<String, String> parameters) throws IndexingException;

	/**
	 * Search an index by term.
	 *
	 * @param index
	 *            the index
	 * @param term
	 *            the term
	 * @return the values as JSON
	 * @throws IndexingException
	 *             the indexing exception
	 */
	public String search(String index, String term) throws IndexingException;

	/**
	 * Search an index by date before.
	 *
	 * @param index
	 *            the index
	 * @param date
	 *            the date
	 * @return the values as JSON
	 * @throws IndexingException
	 *             the indexing exception
	 */
	public String before(String index, long date) throws IndexingException;

	/**
	 * Search an index by date after.
	 *
	 * @param index
	 *            the index
	 * @param date
	 *            the date
	 * @return the values as JSON
	 * @throws IndexingException
	 *             the indexing exception
	 */
	public String after(String index, long date) throws IndexingException;

	/**
	 * Search an index by date between.
	 *
	 * @param index
	 *            the index
	 * @param lower
	 *            the lower
	 * @param upper
	 *            the upper
	 * @return the values as JSON
	 * @throws IndexingException
	 *             the indexing exception
	 */
	public String between(String index, long lower, long upper) throws IndexingException;

}
