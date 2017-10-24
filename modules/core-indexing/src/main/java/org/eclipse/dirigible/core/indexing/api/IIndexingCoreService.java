package org.eclipse.dirigible.core.indexing.api;

import java.util.Map;

import org.eclipse.dirigible.commons.api.service.ICoreService;

public interface IIndexingCoreService extends ICoreService {

	public void add(String index, String location, byte[] contents, long lastModified, Map<String, String> parameters) throws IndexingException;

	public String search(String index, String term) throws IndexingException;

	public String before(String index, long date) throws IndexingException;

	public String after(String index, long date) throws IndexingException;

	public String between(String index, long lower, long upper) throws IndexingException;

}
