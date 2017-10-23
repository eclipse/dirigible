package org.eclipse.dirigible.core.indexing.api;

import java.util.Map;

import org.eclipse.dirigible.commons.api.service.ICoreService;

public interface IIndexingCoreService extends ICoreService {

	public void add(String index, String location, long lastModified, byte[] contents, Map<String, String> parameters) throws IndexingException;

	public String[] search(String index, String query) throws IndexingException;
}
