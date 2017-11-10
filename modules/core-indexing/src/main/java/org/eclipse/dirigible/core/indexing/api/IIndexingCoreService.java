/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.indexing.api;

import java.util.Map;

import org.eclipse.dirigible.commons.api.service.ICoreService;

// TODO: Auto-generated Javadoc
/**
 * The Interface IIndexingCoreService.
 */
public interface IIndexingCoreService extends ICoreService {

	/**
	 * Adds the.
	 *
	 * @param index the index
	 * @param location the location
	 * @param contents the contents
	 * @param lastModified the last modified
	 * @param parameters the parameters
	 * @throws IndexingException the indexing exception
	 */
	public void add(String index, String location, byte[] contents, long lastModified, Map<String, String> parameters) throws IndexingException;

	/**
	 * Search.
	 *
	 * @param index the index
	 * @param term the term
	 * @return the string
	 * @throws IndexingException the indexing exception
	 */
	public String search(String index, String term) throws IndexingException;

	/**
	 * Before.
	 *
	 * @param index the index
	 * @param date the date
	 * @return the string
	 * @throws IndexingException the indexing exception
	 */
	public String before(String index, long date) throws IndexingException;

	/**
	 * After.
	 *
	 * @param index the index
	 * @param date the date
	 * @return the string
	 * @throws IndexingException the indexing exception
	 */
	public String after(String index, long date) throws IndexingException;

	/**
	 * Between.
	 *
	 * @param index the index
	 * @param lower the lower
	 * @param upper the upper
	 * @return the string
	 * @throws IndexingException the indexing exception
	 */
	public String between(String index, long lower, long upper) throws IndexingException;

}
