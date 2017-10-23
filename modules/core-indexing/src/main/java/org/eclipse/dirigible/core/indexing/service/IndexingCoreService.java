package org.eclipse.dirigible.core.indexing.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.indexing.api.IIndexingCoreService;
import org.eclipse.dirigible.core.indexing.api.IndexingException;

public class IndexingCoreService implements IIndexingCoreService {

	private static final String DIRIGIBLE_INDEXING_ROOT_FOLDER = "DIRIGIBLE_INDEXING_ROOT_FOLDER";
	private static final String DIRIGIBLE_INDEXING_MAX_RESULTS = "DIRIGIBLE_INDEXING_MAX_RESULTS";

	private static final String FIELD_CONTENTS = "contents";
	private static final String FIELD_MODIFIED = "modified";
	private static final String FIELD_PATH = "path";

	private static final String US = "_";
	private static final String BS = "\\";
	private static final String SLASH = "/";
	private static final String DOT = ".";

	private static String ROOT_FOLDER;
	private static int MAX_RESULTS;

	static {
		Configuration.load("/dirigible-indexing.properties");
		ROOT_FOLDER = Configuration.get(DIRIGIBLE_INDEXING_ROOT_FOLDER);
		MAX_RESULTS = Integer.parseInt(Configuration.get(DIRIGIBLE_INDEXING_MAX_RESULTS, "100"));
	}

	@Override
	public void add(String index, String location, long lastModified, byte[] contents, Map<String, String> parameters) throws IndexingException {
		String indexName = index;
		if (index != null) {
			indexName = flattenizeIndexName(indexName);
		} else {
			throw new IndexingException("Index name may not be null");
		}

		try {
			Directory dir = FSDirectory.open(Paths.get(ROOT_FOLDER + File.separator + indexName));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			IndexWriter writer = new IndexWriter(dir, iwc);
			try {
				Document doc = new Document();
				Field pathField = new StringField(FIELD_PATH, location, Field.Store.YES);
				doc.add(pathField);
				doc.add(new LongPoint(FIELD_MODIFIED, lastModified));
				for (String key : parameters.keySet()) {
					doc.add(new StringField(key, parameters.get(key), Field.Store.YES));
				}
				doc.add(new TextField(FIELD_CONTENTS,
						new BufferedReader(new InputStreamReader(new ByteArrayInputStream(contents), StandardCharsets.UTF_8))));
				writer.updateDocument(new Term(FIELD_PATH, location), doc);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			throw new IndexingException(e);
		}
	}

	@Override
	public String[] search(String index, String term) throws IndexingException {
		List<String> results = new ArrayList<String>();
		String indexName = index;
		if (index != null) {
			indexName = flattenizeIndexName(indexName);
		} else {
			throw new IndexingException("Index name may not be null");
		}
		try {
			Directory dir = FSDirectory.open(Paths.get(ROOT_FOLDER + File.separator + indexName));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();
			String field = FIELD_CONTENTS;
			QueryParser parser = new QueryParser(field, analyzer);
			Query query = parser.parse(term);
			TopDocs topDocs = searcher.search(query, MAX_RESULTS);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document document = searcher.doc(scoreDoc.doc);
				results.add(document.getField(FIELD_PATH).stringValue());
			}
			return results.toArray(new String[] {});
		} catch (IOException | ParseException e) {
			throw new IndexingException(e);
		}
	}

	private String flattenizeIndexName(String index) {
		String indexName = index;
		indexName = indexName.replace(DOT, US);
		indexName = indexName.replace(SLASH, US);
		indexName = indexName.replace(BS, US);
		return indexName;
	}

}
