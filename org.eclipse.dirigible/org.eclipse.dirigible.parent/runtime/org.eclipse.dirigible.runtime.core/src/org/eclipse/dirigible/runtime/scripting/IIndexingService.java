package org.eclipse.dirigible.runtime.scripting;

import org.eclipse.dirigible.repository.ext.indexing.IIndex;

public interface IIndexingService <T> {
	
	public IIndex<T> getIndex(String indexName);

}
