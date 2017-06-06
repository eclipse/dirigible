package org.eclipse.dirigible.runtime.workspaces.processor;

import javax.inject.Inject;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.repository.processor.RepositoryJsonHelper;

/**
 * Processing the Registry Service incoming requests 
 *
 */
public class WorkspaceProcessor {
	
	private static final String WORKSPACE = "/workspace";
	
	@Inject
	private IRepository repository;
	
	public IResource getResource(String user, String workspace, String path) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, path);
		return repository.getResource(workspacePath.toString());
	}
	
	public IResource createResource(String user, String workspace, String path, byte[] content, String contentType) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, path);
		return repository.createResource(workspacePath.toString(), content, false, contentType);
	}
	
	public IResource updateResource(String user, String workspace, String path, byte[] content) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, path);
		IResource resource = repository.getResource(workspacePath.toString());
		resource.setContent(content);
		return resource;
	}
	
	public void deleteResource(String user, String workspace, String path) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, path);
		repository.removeResource(workspacePath.toString());
	}
	
	public ICollection getCollection(String user, String workspace, String path) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, path);
		return repository.getCollection(workspacePath.toString());
	}

	private StringBuilder generateWorkspacePath(String user, String workspace, String path) {
		StringBuilder registryPath = new StringBuilder(IRepositoryStructure.USERS)
				.append(IRepositoryStructure.SEPARATOR)
				.append(user)
				.append(IRepositoryStructure.SEPARATOR)
				.append(workspace)
				.append(IRepositoryStructure.SEPARATOR)
				.append(path);
		return registryPath;
	}

	public String renderTree(ICollection collection) {
		return RepositoryJsonHelper.collectionToJsonTree(collection, IRepositoryStructure.USERS, WORKSPACE);
	}

}
