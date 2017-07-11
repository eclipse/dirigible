package org.eclipse.dirigible.engine.api;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryException;

public interface IResourceExecutor {
	
	public byte[] getResourceContent(String root, String module) throws RepositoryException;
	
	public byte[] getResourceContent(String root, String module, String extension) throws RepositoryException;
	
	public ICollection getCollection(String root, String module) throws RepositoryException;

	public IResource getResource(String root, String module) throws RepositoryException;
	
	public IResource getResource(String root, String module, String extension) throws RepositoryException;
	
	public boolean existResource(String root, String module) throws RepositoryException;
	
	public boolean existResource(String root, String module, String extension) throws RepositoryException;
	
	public String createResourcePath(String root, String module);
	
	public String createResourcePath(String root, String module, String extension);

}
