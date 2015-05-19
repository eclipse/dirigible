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

package org.eclipse.dirigible.repository.ext.indexing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.logging.Logger;

public class RepositoryMemoryIndexer {
	
	private static final String FIELD_PATH = "path"; //$NON-NLS-1$
	private static final String FIELD_NAME = "name"; //$NON-NLS-1$
	private static final String FIELD_CONTENT = "content"; //$NON-NLS-1$
	
	private static final Logger logger = Logger.getLogger(RepositoryMemoryIndexer.class);
	
	private static Directory directory = new RAMDirectory();
	private static List<String> indexedResources = new ArrayList<String>();
	private static Date lastIndexed = new Date();

	private RepositoryMemoryIndexer() {
		// no external instances
	}
	
	public static void indexRepository(IRepository repository) 
			throws IOException {

		try {
			synchronized (directory) {
				
				logger.debug("entering: indexRepository(IRepository repository)"); //$NON-NLS-1$
				
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
				IndexWriterConfig config = null;
				IndexWriter iwriter = null;
				try {
					config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
					iwriter = new IndexWriter(directory, config);
					
					ICollection collection = repository.getRoot();
					indexCollection(iwriter, collection);
					
					lastIndexed = new Date();
					
				} finally {
					if (iwriter != null) {
						iwriter.close();
					}
				}
				logger.debug("exiting: indexRepository(IRepository repository)"); //$NON-NLS-1$
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	public static void clearIndex()
			throws IOException {

		try {
			synchronized (directory) {
				
				logger.debug("entering: clearIndex()"); //$NON-NLS-1$
				
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
				IndexWriterConfig config = null;
				IndexWriter iwriter = null;
				try {
					config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
					iwriter = new IndexWriter(directory, config);
					iwriter.deleteAll();
				} finally {
					if (iwriter != null) {
						iwriter.close();
					}
				}
				logger.debug("exiting: clearIndex()"); //$NON-NLS-1$
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public static List<String> search(String term) 
			throws IOException {
		
		List<String> docs = new ArrayList<String>();
		
		try {
			synchronized (directory) {
				
				logger.debug("entering: search(String term)"); //$NON-NLS-1$
				
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
				// Now search the index:
				IndexSearcher isearcher = null;
				IndexReader ireader = null;
				 
				try {
					ireader = IndexReader.open(directory);
					isearcher = new IndexSearcher(ireader);
					// Parse a simple query that searches for "text":
					QueryParser parser = new QueryParser(Version.LUCENE_35, FIELD_CONTENT, analyzer);
					Query query = parser.parse(term);
					ScoreDoc[] hits = isearcher.search(query, null, 100).scoreDocs;
					// Iterate through the results:
					for (int i = 0; i < hits.length; i++) {
					  Document hitDoc = isearcher.doc(hits[i].doc);
					  docs.add(hitDoc.get(FIELD_PATH));
					}
				} finally {
					if (isearcher != null) {
						isearcher.close();
					}
					if (ireader != null) {
						ireader.close();
					}
				}
				
				logger.debug("exiting: search(String term)"); //$NON-NLS-1$
				
			}
		} catch (ParseException e) {
			logger.debug(e.getMessage());
		}
		return docs;
	}

	private static void indexCollection(IndexWriter iwriter, ICollection collection) throws IOException {
		
		logger.debug("entering: indexCollection(IndexWriter iwriter, ICollection collection)"); //$NON-NLS-1$
		
		List<IResource> resources = collection.getResources();
		for (Iterator<IResource> iterator = resources.iterator(); iterator.hasNext();) {
			IResource resource = iterator.next();
			indexResource(iwriter, resource);
		}
		List<ICollection> collections = collection.getCollections();
		for (Iterator<ICollection> iterator = collections.iterator(); iterator.hasNext();) {
			ICollection child = iterator.next();
			indexCollection(iwriter, child);
		}
		logger.debug("exiting: indexCollection(IndexWriter iwriter, ICollection collection)"); //$NON-NLS-1$
	}

	private static void indexResource(IndexWriter iwriter, IResource resource)
			throws CorruptIndexException, IOException {
		
		logger.debug("entering: indexResource(IndexWriter iwriter, IResource resource)"); //$NON-NLS-1$
		
		if (!resource.isBinary()) {
			if (!indexedResources.contains(resource.getPath())) {
				logger.debug("Indexing resource: " + resource.getPath()); //$NON-NLS-1$
				Document doc = createDocument(resource);
				iwriter.addDocument(doc);
				iwriter.commit();
				logger.debug("Resource: " + resource.getPath() + " indexed successfully"); //$NON-NLS-1$ //$NON-NLS-2$
				indexedResources.add(resource.getPath());
			} else {
				if (lastIndexed.before(resource.getInformation().getModifiedAt())) {
					logger.debug("Updating index for resource: " + resource.getPath()); //$NON-NLS-1$
					Document doc = createDocument(resource);
					Term term = new Term(FIELD_PATH, resource.getPath());
					iwriter.updateDocument(term, doc);
					iwriter.commit();
					logger.debug("For resource: " + resource.getPath() + " index updated successfully"); //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					logger.debug("Skip indexing for unmodified resource: " + resource.getPath()); //$NON-NLS-1$
				}
			}
		} else {
			logger.debug("Skip indexing for binary resource: " + resource.getPath()); //$NON-NLS-1$
		}
		
		logger.debug("exiting: indexResource(IndexWriter iwriter, IResource resource)"); //$NON-NLS-1$
	}

	private static Document createDocument(IResource resource)
			throws UnsupportedEncodingException, IOException {
		Document doc = new Document();
		String text = new String(resource.getContent(), "UTF-8"); //$NON-NLS-1$
		doc.add(new Field(FIELD_CONTENT, text, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field(FIELD_NAME, resource.getName(), Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field(FIELD_PATH, resource.getPath(), Field.Store.YES, Field.Index.ANALYZED));
		return doc;
	}
	
}
