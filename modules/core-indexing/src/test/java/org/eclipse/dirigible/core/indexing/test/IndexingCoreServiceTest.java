package org.eclipse.dirigible.core.indexing.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.dirigible.core.indexing.api.IIndexingCoreService;
import org.eclipse.dirigible.core.indexing.api.IndexingException;
import org.eclipse.dirigible.core.indexing.service.IndexingCoreService;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Before;
import org.junit.Test;

public class IndexingCoreServiceTest extends AbstractGuiceTest {

	private static final String TEST_INDEX = "test_index";

	@Inject
	private IIndexingCoreService indexingCoreService;

	@Before
	public void setUp() throws Exception {
		this.indexingCoreService = getInjector().getInstance(IndexingCoreService.class);
	}

	@Test
	public void createListenerTest() throws IndexingException {

		Map<String, String> parameters = new HashMap<String, String>();
		indexingCoreService.add(TEST_INDEX, "/root/folder/subfolder/file1.txt", new Date().getTime(),
				"Apache LuceneTM is a high-performance, full-featured text search engine library written entirely in Java.".getBytes(), parameters);
		indexingCoreService.add(TEST_INDEX, "/root/folder/subfolder/file2.txt", new Date().getTime(),
				"It is a technology suitable for nearly any application that requires full-text search, especially cross-platform.".getBytes(),
				parameters);
		indexingCoreService.add(TEST_INDEX, "/root/folder/subfolder/file2.txt", new Date().getTime(),
				"Apache Lucene is an open source project available for free download. Please use the links on the right to access Lucene.".getBytes(),
				parameters);

		String[] matches = indexingCoreService.search(TEST_INDEX, "Lucene");
		assertNotNull(matches);
		assertEquals(1, matches.length);

		matches = indexingCoreService.search(TEST_INDEX, "Apache");
		assertNotNull(matches);
		assertEquals(2, matches.length);

		matches = indexingCoreService.search(TEST_INDEX, "apache");
		assertNotNull(matches);
		assertEquals(2, matches.length);

		matches = indexingCoreService.search(TEST_INDEX, "NoMatches");
		assertNotNull(matches);
		assertEquals(0, matches.length);
	}

}
