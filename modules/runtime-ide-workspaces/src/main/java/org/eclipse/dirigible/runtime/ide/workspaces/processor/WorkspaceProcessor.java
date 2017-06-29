package org.eclipse.dirigible.runtime.ide.workspaces.processor;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.repository.json.RepositoryJsonHelper;

/**
 * Processing the Registry Service incoming requests 
 *
 */
public class WorkspaceProcessor {
	
	private static final String WORKSPACE = "/workspace";
	
	@Inject
	private IRepository repository;
	
	// Workspace
	
	public ICollection getWorkspace(String user, String workspace) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, null, null);
		ICollection collection = repository.getCollection(workspacePath.toString());
		return collection;
	}
	
	public ICollection createWorkspace(String user, String workspace) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, null, null);
		ICollection collection = repository.getCollection(workspacePath.toString());
		collection.create();
		return collection;
	}
	
	public void deleteWorkspace(String user, String workspace) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, null, null);
		ICollection collection = repository.getCollection(workspacePath.toString());
		collection.delete();
	}
	
	public boolean existsWorkspace(String user, String workspace) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, null, null);
		ICollection collection = repository.getCollection(workspacePath.toString());
		return collection.exists();
	}
	
	// Project
	
	public ICollection getProject(String user, String workspace, String project) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, null);
		ICollection collection = repository.getCollection(workspacePath.toString());
		return collection;
	}
	
	public ICollection createProject(String user, String workspace, String project) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, null);
		ICollection collection = repository.getCollection(workspacePath.toString());
		collection.create();
		return collection;
	}
	
	public void deleteProject(String user, String workspace, String project) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, null);
		ICollection collection = repository.getCollection(workspacePath.toString());
		collection.delete();
	}
	
	public boolean existsProject(String user, String workspace, String project) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, null);
		ICollection collection = repository.getCollection(workspacePath.toString());
		return collection.exists();
	}
	
	// Collection
	
	public ICollection getCollection(String user, String workspace, String project, String path) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, path);
		return repository.getCollection(workspacePath.toString());
	}
	
	public ICollection createCollection(String user, String workspace, String project, String path) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, path);
		ICollection collection = repository.getCollection(workspacePath.toString());
		collection.create();
		return collection;
	}
	
	public void deleteCollection(String user, String workspace, String project, String path) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, path);
		repository.removeCollection(workspacePath.toString());
	}
	
	// Resource
	
	public IResource getResource(String user, String workspace, String project, String path) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, path);
		return repository.getResource(workspacePath.toString());
	}
	
	public IResource createResource(String user, String workspace, String project, String path, byte[] content, String contentType) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, path);
		return repository.createResource(workspacePath.toString(), content, false, contentType);
	}
	
	public IResource updateResource(String user, String workspace, String project, String path, byte[] content) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, path);
		IResource resource = repository.getResource(workspacePath.toString());
		resource.setContent(content);
		return resource;
	}
	
	public void deleteResource(String user, String workspace, String project, String path) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, path);
		repository.removeResource(workspacePath.toString());
	}
	
	
	

	private StringBuilder generateWorkspacePath(String user, String workspace, String project, String path) {
		StringBuilder relativePath = new StringBuilder(IRepositoryStructure.USERS)
				.append(IRepositoryStructure.SEPARATOR)
				.append(user)
				.append(IRepositoryStructure.SEPARATOR)
				.append(workspace);
		if (project != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR)
				.append(project);
		}
		if (path != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR)
				.append(path);
		}
		return relativePath;
	}
	
	public URI getURI(String workspace, String project, String path) throws URISyntaxException {
		StringBuilder relativePath = new StringBuilder(workspace);
		if (project != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR)
				.append(project);
		}
		if (path != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR)
				.append(path);
		}
		return new URI(relativePath.toString());
	}

	public String renderTree(ICollection collection) {
		return RepositoryJsonHelper.collectionToJsonTree(collection, IRepositoryStructure.USERS, WORKSPACE);
	}

	

}
