package org.eclipse.dirigible.repository.ext.indexing;

import java.util.Date;
import java.util.List;

public interface IIndex <T> {

	public void clearIndex() throws EIndexingException;
	
	public List<T> search(String term) throws EIndexingException;
	
	public Object createDocument(String id, String content) throws EIndexingException;
	
	public void indexDocument(T document) throws EIndexingException;
	
	public void deleteDocument(T document) throws EIndexingException;
	
	public void updateDocument(T document) throws EIndexingException;
	
	public Date getLastIndexed();

}
