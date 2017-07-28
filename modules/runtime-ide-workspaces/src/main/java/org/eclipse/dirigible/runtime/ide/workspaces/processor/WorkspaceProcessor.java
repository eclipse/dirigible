package org.eclipse.dirigible.runtime.ide.workspaces.processor;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.repository.json.RepositoryJsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing the Registry Service incoming requests 
 *
 */
public class WorkspaceProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkspaceProcessor.class);

	private static final String WORKSPACE = "/workspace";
	
	@Inject
	private IRepository repository;
	
	// Workspace

	public ICollection listWorkspaces(String user) {
		return getWorkspace(user, null);
	}

	public ICollection getWorkspace(String user, String workspace) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, null, null);
		ICollection collection = repository.getCollection(workspacePath.toString());
		return collection;
	}
	
	public ICollection createWorkspace(String user, String workspace) {
		ICollection collection = getWorkspace(user, workspace);
		collection.create();
		logger.info("Workspace created [{}]", collection.getPath());
		return collection;
	}
	
	public void deleteWorkspace(String user, String workspace) {
		ICollection collection = getWorkspace(user, workspace);
		collection.delete();
		logger.info("Workspace deleted [{}]", collection.getPath());
	}
	
	public boolean existsWorkspace(String user, String workspace) {
		ICollection collection = getWorkspace(user, workspace);
		return collection.exists();
	}
	
	// Project
	
	public ICollection getProject(String user, String workspace, String project) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, null);
		ICollection collection = repository.getCollection(workspacePath.toString());
		return collection;
	}
	
	public ICollection createProject(String user, String workspace, String project) {
		ICollection collection = getProject(user, workspace, project);
		collection.create();
		logger.info("Project created [{}]", collection.getPath());
		return collection;
	}
	
	public void deleteProject(String user, String workspace, String project) {
		ICollection collection = getProject(user, workspace, project);
		collection.delete();
		logger.info("Project deleted [{}]", collection.getPath());
	}
	
	public boolean existsProject(String user, String workspace, String project) {
		ICollection collection = getProject(user, workspace, project);
		return collection.exists();
	}
	
	// Collection
	
	public ICollection getCollection(String user, String workspace, String project, String path) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, path);
		return repository.getCollection(workspacePath.toString());
	}
	
	public ICollection createCollection(String user, String workspace, String project, String path) {
		ICollection collection = getCollection(user, workspace, project, path);
		collection.create();
		logger.info("Collection created [{}]", collection.getPath());
		return collection;
	}
	
	public void deleteCollection(String user, String workspace, String project, String path) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, path);
		repository.removeCollection(workspacePath.toString());
		logger.info("Collection deleted [{}]", workspacePath.toString());
	}
	
	// Resource
	
	public IResource getResource(String user, String workspace, String project, String path) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, path);
		return repository.getResource(workspacePath.toString());
	}
	
	public IResource createResource(String user, String workspace, String project, String path, byte[] content, String contentType) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, path);
		IResource resource = repository.createResource(workspacePath.toString(), content, false, contentType);
		logger.info("Resource created [{}]", resource.getPath());
		return resource;
	}
	
	public IResource updateResource(String user, String workspace, String project, String path, byte[] content) {
		IResource resource = getResource(user, workspace, project, path);
		resource.setContent(content);
		logger.info("Resource updated [{}]", resource.getPath());
		return resource;
	}
	
	public void deleteResource(String user, String workspace, String project, String path) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, project, path);
		repository.removeResource(workspacePath.toString());
		logger.info("Resource removed [{}]", workspacePath.toString());
	}

	private StringBuilder generateWorkspacePath(String user, String workspace, String project, String path) {
		StringBuilder relativePath = new StringBuilder(IRepositoryStructure.USERS)
				.append(IRepositoryStructure.SEPARATOR)
				.append(user);
		if (workspace != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR)
			.append(workspace);
		}
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
