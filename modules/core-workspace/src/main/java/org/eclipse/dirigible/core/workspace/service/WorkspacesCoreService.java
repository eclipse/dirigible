package org.eclipse.dirigible.core.workspace.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.api.IWorkspacesCoreService;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class WorkspacesCoreService implements IWorkspacesCoreService {

	private static final String DEFAULT_WORKSPACE_NAME = "workspace";

	private static final Logger logger = LoggerFactory.getLogger(WorkspacesCoreService.class);

	@Inject
	private IRepository repository;

	@Override
	public IWorkspace createWorkspace(String name) {
		ICollection collection = getWorkspace(name);
		collection.create();
		logger.info("Workspace created [{}]", collection.getPath());
		return new Workspace(collection);
	}

	@Override
	public IWorkspace getWorkspace(String name) {
		StringBuilder workspacePath = generateWorkspacePath(name, null, null);
		ICollection collection = repository.getCollection(workspacePath.toString());
		return new Workspace(collection);
	}

	@Override
	public List<IWorkspace> getWorkspaces() {
		StringBuilder workspacePath = generateWorkspacePath(null, null, null);
		ICollection root = repository.getCollection(workspacePath.toString());
		List<IWorkspace> workspaces = new ArrayList<IWorkspace>();
		if (!root.exists()) {
			root.create();
		}
		List<ICollection> collections = root.getCollections();
		for (ICollection collection : collections) {
			workspaces.add(new Workspace(collection));
		}
		if (workspaces.isEmpty()) {
			ICollection collection = root.createCollection(DEFAULT_WORKSPACE_NAME);
			workspaces.add(new Workspace(collection));
		}
		return workspaces;
	}

	@Override
	public void deleteWorkspace(String name) {
		ICollection collection = getWorkspace(name);
		collection.delete();
		logger.info("Workspace deleted [{}]", collection.getPath());
	}

	private StringBuilder generateWorkspacePath(String workspace, String project, String path) {
		StringBuilder relativePath = new StringBuilder(IRepositoryStructure.PATH_USERS).append(IRepositoryStructure.SEPARATOR)
				.append(UserFacade.getName());
		if (workspace != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(workspace);
		}
		if (project != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(project);
		}
		if (path != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(path);
		}
		return relativePath;
	}

}
