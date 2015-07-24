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

package test.org.eclipse.dirigible.repository.ext;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.lucene.document.Document;
import org.junit.Test;
import org.eclipse.dirigible.repository.ext.indexing.LuceneMemoryIndexer;

public class CustomMemoryIndexerTest {

	@Test
	public void testIndexer() {
		try {
			// create indexer
			LuceneMemoryIndexer customMemoryIndexer = LuceneMemoryIndexer.getIndex("TestIndex");
			
			assertNotNull(customMemoryIndexer);
			
			// create document
			Document document = customMemoryIndexer.createDocument("001", "Test Content");
			// index it
			customMemoryIndexer.indexDocument(document);
			// search it
			List<Document> hitDocs = customMemoryIndexer.search("Test");
			
			assertEquals(1, hitDocs.size());
			assertEquals("001", hitDocs.get(0).get("id"));
			
			// clear index
			customMemoryIndexer.clearIndex();
			// search again
			hitDocs = customMemoryIndexer.search("Test");
			// nothing found
			assertEquals(0, hitDocs.size());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


	@Test
	public void testDeleteDocument() {
		try {
			// create indexer
			LuceneMemoryIndexer customMemoryIndexer = LuceneMemoryIndexer.getIndex("TestIndex");
			
			assertNotNull(customMemoryIndexer);
			
			// create document
			Document document = customMemoryIndexer.createDocument("001", "Test Content");
			// index it
			customMemoryIndexer.indexDocument(document);
			// search it
			List<Document> hitDocs = customMemoryIndexer.search("Test");
			
			assertEquals(1, hitDocs.size());
			assertEquals("001", hitDocs.get(0).get("id"));
			
			document = customMemoryIndexer.createDocument("001", "Test Content");
			// clear index
			customMemoryIndexer.deleteDocument(document);
			// search again
			hitDocs = customMemoryIndexer.search("Test");
			// nothing found
			assertEquals(0, hitDocs.size());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateDocument() {
		try {
			// create indexer
			LuceneMemoryIndexer customMemoryIndexer = LuceneMemoryIndexer.getIndex("TestIndex");
			
			assertNotNull(customMemoryIndexer);
			
			// create document
			Document document = customMemoryIndexer.createDocument("001", "Test Content");
			// index it
			customMemoryIndexer.indexDocument(document);
			// search it
			List<Document> hitDocs = customMemoryIndexer.search("Test");
			
			assertEquals(1, hitDocs.size());
			assertEquals("001", hitDocs.get(0).get("id"));
			assertEquals("Test Content", hitDocs.get(0).get("content"));
			
			document = customMemoryIndexer.createDocument("001", "Test Content 1");
			// clear index
			customMemoryIndexer.updateDocument(document);
			// search again
			hitDocs = customMemoryIndexer.search("Test");
			// nothing found
			assertEquals(1, hitDocs.size());
			assertEquals("001", hitDocs.get(0).get("id"));
			assertEquals("Test Content 1", hitDocs.get(0).get("content"));
			
			customMemoryIndexer.clearIndex();
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
