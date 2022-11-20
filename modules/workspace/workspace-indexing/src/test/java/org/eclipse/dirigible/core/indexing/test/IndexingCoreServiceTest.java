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
package org.eclipse.dirigible.core.indexing.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.indexing.api.IIndexingCoreService;
import org.eclipse.dirigible.core.indexing.api.IndexingException;
import org.eclipse.dirigible.core.indexing.service.IndexingCoreService;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class IndexingCoreServiceTest.
 */
public class IndexingCoreServiceTest extends AbstractDirigibleTest {

	/** The Constant TEST_INDEX. */
	private static final String TEST_INDEX = "test_index";

	/** The indexing core service. */
	private IIndexingCoreService indexingCoreService = new IndexingCoreService();

	/**
	 * Setup.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.indexingCoreService = new IndexingCoreService();
	}

	/**
	 * Search test.
	 *
	 * @throws IndexingException
	 *             the indexing exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void searchTest() throws IndexingException {

		Map<String, String> parameters = new HashMap<String, String>();
		indexingCoreService.add(TEST_INDEX, "/root/folder/subfolder/file1.txt",
				"Apache LuceneTM is a high-performance, full-featured text search engine library written entirely in Java.".getBytes(),
				new Date().getTime(), parameters);
		indexingCoreService.add(TEST_INDEX, "/root/folder/subfolder/file2.txt",
				"It is a technology suitable for nearly any application that requires full-text search, especially cross-platform.".getBytes(),
				new Date().getTime(), parameters);
		indexingCoreService.add(TEST_INDEX, "/root/folder/subfolder/file3.txt",
				"Apache Lucene is an open source project available for free download. Please use the links on the right to access Lucene.".getBytes(),
				new Date().getTime(), parameters);

		List matches = GsonHelper.fromJson(indexingCoreService.search(TEST_INDEX, "Lucene"), List.class);
		assertNotNull(matches);
		assertEquals(1, matches.size());

		matches = GsonHelper.fromJson(indexingCoreService.search(TEST_INDEX, "Apache"), List.class);
		assertNotNull(matches);
		assertEquals(2, matches.size());

		matches = GsonHelper.fromJson(indexingCoreService.search(TEST_INDEX, "apache"), List.class);
		assertNotNull(matches);
		assertEquals(2, matches.size());

		matches = GsonHelper.fromJson(indexingCoreService.search(TEST_INDEX, "NoMatches"), List.class);
		assertNotNull(matches);
		assertEquals(0, matches.size());
	}

	/**
	 * Between test.
	 *
	 * @throws IndexingException
	 *             the indexing exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void betweenTest() throws IndexingException {

		Map<String, String> parameters = new HashMap<String, String>();
		indexingCoreService.add(TEST_INDEX, "/root/folder/subfolder/file4.txt",
				"Apache LuceneTM is a high-performance, full-featured text search engine library written entirely in Java.".getBytes(),
				new Date(123).getTime(), parameters);
		indexingCoreService.add(TEST_INDEX, "/root/folder/subfolder/file5.txt",
				"It is a technology suitable for nearly any application that requires full-text search, especially cross-platform.".getBytes(),
				new Date(456).getTime(), parameters);
		indexingCoreService.add(TEST_INDEX, "/root/folder/subfolder/file6.txt",
				"Apache Lucene is an open source project available for free download. Please use the links on the right to access Lucene.".getBytes(),
				new Date(789).getTime(), parameters);

		List matches = GsonHelper.fromJson(indexingCoreService.between(TEST_INDEX, new Date(124).getTime(), new Date(689).getTime()),
				List.class);
		assertNotNull(matches);
		assertEquals(1, matches.size());
	}

}
