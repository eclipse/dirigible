/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.repository.ext.indexing.RepositoryMemoryIndexer;
import org.eclipse.dirigible.runtime.utils.DataSourceUtils;

public class SearchIndexerTest {

	private static IRepository repository;

	@Before
	public void setUp() {
		DataSource dataSource = DataSourceUtils.createLocal();
		try {
			repository = new DBRepository(dataSource, "guest", false); //$NON-NLS-1$
			
			repository.createResource(
					"/db/dirigible/file1.txt", "Apache LuceneTM is a high-performance, full-featured text search engine library written entirely in Java." //$NON-NLS-1$ //$NON-NLS-2$
							.getBytes(), false, "text/plain");
			
			repository.createResource(
					"/db/dirigible/file2.txt", "It is a technology suitable for nearly any application that requires full-text search, especially cross-platform." //$NON-NLS-1$ //$NON-NLS-2$
							.getBytes(), false, "text/plain");
			
			repository.createResource(
					"/db/dirigible/sub/file3.txt", "Apache Lucene is an open source project available for free download. Please use the links on the right to access Lucene." //$NON-NLS-1$ //$NON-NLS-2$
							.getBytes(), false, "text/plain");
			
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSearch() {

		try {
			
			
			RepositoryMemoryIndexer.indexRepository(repository);
			
			List<String> matches = RepositoryMemoryIndexer.search("Lucene");
			assertNotNull(matches);
			assertEquals(1, matches.size());
			
			matches = RepositoryMemoryIndexer.search("Apache");
			assertNotNull(matches);
			assertEquals(2, matches.size());
			
			matches = RepositoryMemoryIndexer.search("apache");
			assertNotNull(matches);
			assertEquals(2, matches.size());
			
			matches = RepositoryMemoryIndexer.search("Eclipse");
			assertNotNull(matches);
			assertEquals(0, matches.size());

		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

}
