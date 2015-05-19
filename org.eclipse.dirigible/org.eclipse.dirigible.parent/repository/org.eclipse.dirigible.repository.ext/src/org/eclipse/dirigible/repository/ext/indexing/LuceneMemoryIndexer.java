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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.dirigible.repository.logging.Logger;

public class LuceneMemoryIndexer <T> implements IIndex {
	
	private static final String FIELD_ID = "id"; //$NON-NLS-1$
	private static final String FIELD_CONTENT = "content"; //$NON-NLS-1$
	
	private static final Logger logger = Logger.getLogger(LuceneMemoryIndexer.class);
	
	private Directory directory = new RAMDirectory();
	
	private static Map<String, LuceneMemoryIndexer> indexes = Collections.synchronizedMap(new HashMap<String, LuceneMemoryIndexer>());
	
	private String indexName = null;
	
	private Date lastIndexed = new Date();
	
	public static LuceneMemoryIndexer getIndex(String indexName) {
		LuceneMemoryIndexer indexer = indexes.get(indexName);
		if (indexer == null) {
			indexer = new LuceneMemoryIndexer(indexName);
			indexes.put(indexName, indexer);
		}
		return indexer;
	}

	private LuceneMemoryIndexer(String indexName) {
		// no external instances
		this.indexName = indexName;
	}
	
	@Override
	public void clearIndex() throws EIndexingException {

		try {
			synchronized (directory) {
				
				logger.debug("entering: clearIndex() : " + indexName); //$NON-NLS-1$
				
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
				logger.debug("exiting: clearIndex() : " + indexName); //$NON-NLS-1$
			}
		} catch (Exception e) {
			throw new EIndexingException(e);
		}
	}

	@Override
	public List<Document> search(String term) throws EIndexingException {
		
		try {
			List<Document> docs = new ArrayList<Document>();
			
			try {
				synchronized (directory) {
					logger.debug("entering: search(String term) : " + indexName); //$NON-NLS-1$

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
						  docs.add(hitDoc);
						}
					} finally {
						if (isearcher != null) {
							isearcher.close();
						}
						if (ireader != null) {
							ireader.close();
						}
					}
					
					logger.debug("exiting: search(String term) : " + indexName); //$NON-NLS-1$
				}
			} catch (ParseException e) {
				logger.debug(e.getMessage());
			}
			return docs;
		} catch (Exception e) {
			throw new EIndexingException(e);
		}
	}

	@Override
	public Document createDocument(String id, String content) throws EIndexingException {
		Document doc = new Document();
		doc.add(new Field(FIELD_ID, id, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field(FIELD_CONTENT, content, Field.Store.YES, Field.Index.ANALYZED));
		return doc;
	}
	
	@Override
	public void indexDocument(Object document) throws EIndexingException {
		try {
			synchronized (directory) {
				
				logger.debug("entering: indexDocument(Document document) : " + indexName); //$NON-NLS-1$
				
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
				IndexWriterConfig config = null;
				IndexWriter iwriter = null;
				try {
					config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
					iwriter = new IndexWriter(directory, config);
					iwriter.addDocument((Document) document);
					iwriter.commit();				
					
					lastIndexed = new Date();
					
				} finally {
					if (iwriter != null) {
						iwriter.close();
					}
				}
				logger.debug("exiting: indexRepository(IRepository repository) : " + indexName); //$NON-NLS-1$
			}
		} catch (Exception e) {
			throw new EIndexingException(e);
		}
	}
	
	@Override
	public void deleteDocument(Object document) throws EIndexingException {
		try {
			synchronized (directory) {
				
				logger.debug("entering: indexDocument(Document document) : " + indexName); //$NON-NLS-1$
				
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
				IndexWriterConfig config = null;
				IndexWriter iwriter = null;
				try {
					config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
					iwriter = new IndexWriter(directory, config);
					Term term = new Term(FIELD_ID, ((Document) document).get(FIELD_ID));
					iwriter.deleteDocuments(term);
					iwriter.commit();				
					
					lastIndexed = new Date();
					
				} finally {
					if (iwriter != null) {
						iwriter.close();
					}
				}
				logger.debug("exiting: indexRepository(IRepository repository) : " + indexName); //$NON-NLS-1$
			}
		} catch (Exception e) {
			throw new EIndexingException(e);
		}
	}
	
	@Override
	public void updateDocument(Object document) throws EIndexingException {
		try {
			synchronized (directory) {
				
				logger.debug("entering: indexDocument(Document document) : " + indexName); //$NON-NLS-1$
				
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
				IndexWriterConfig config = null;
				IndexWriter iwriter = null;
				try {
					config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
					iwriter = new IndexWriter(directory, config);
					Term term = new Term(FIELD_ID, ((Document) document).get(FIELD_ID));
					iwriter.updateDocument(term, (Document) document);
					iwriter.commit();				
					
					lastIndexed = new Date();
					
				} finally {
					if (iwriter != null) {
						iwriter.close();
					}
				}
				logger.debug("exiting: indexRepository(IRepository repository) : " + indexName); //$NON-NLS-1$
			}
		} catch (Exception e) {
			throw new EIndexingException(e);
		}
	}
	
	public Date getLastIndexed() {
		return lastIndexed;
	}
	
}
