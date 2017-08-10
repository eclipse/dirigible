package org.eclipse.dirigible.runtime.ide.workspaces.processor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.auth.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.json.Folder;
import org.eclipse.dirigible.core.workspace.json.Project;
import org.eclipse.dirigible.core.workspace.json.Workspace;
import org.eclipse.dirigible.core.workspace.json.WorkspaceJsonHelper;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing the Registry Service incoming requests
 */
public class WorkspaceProcessor {

	private static final Logger logger = LoggerFactory.getLogger(WorkspaceProcessor.class);

	private static final String WORKSPACES = ""; // "/workspaces";

	@Inject
	private WorkspacesCoreService workspacesCoreService;

	// Workspace

	public List<IWorkspace> listWorkspaces() {
		return workspacesCoreService.getWorkspaces();
	}

	public IWorkspace getWorkspace(String workspace) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		return workspaceObject;
	}

	public IWorkspace createWorkspace(String workspace) {
		IWorkspace workspaceObject = workspacesCoreService.createWorkspace(workspace);
		return workspaceObject;
	}

	public void deleteWorkspace(String workspace) {
		workspacesCoreService.deleteWorkspace(workspace);
	}

	public boolean existsWorkspace(String workspace) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		return workspaceObject.exists();
	}

	// Project

	public IProject getProject(String workspace, String project) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		return projectObject;
	}

	public IProject createProject(String workspace, String project) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.createProject(project);
		return projectObject;
	}

	public void deleteProject(String workspace, String project) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.deleteProject(project);
	}

	public boolean existsProject(String workspace, String project) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		return projectObject.exists();
	}

	// Folder

	public IFolder getFolder(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		IFolder folderObject = projectObject.getFolder(path);
		return folderObject;
	}

	public IFolder createFolder(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		IFolder folderObject = projectObject.createFolder(path);
		return folderObject;
	}

	public void deleteFolder(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		projectObject.deleteFolder(path);
	}

	// File

	public IFile getFile(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		IFile fileObject = projectObject.getFile(path);
		return fileObject;
	}

	public IFile createFile(String workspace, String project, String path, byte[] content, String contentType) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		if (contentType == null) {
			contentType = ContentTypeHelper.getContentType(ContentTypeHelper.getExtension(path));
		}
		boolean isBinary = ContentTypeHelper.isBinary(contentType);
		IFile fileObject = projectObject.createFile(path, content, isBinary, contentType);
		return fileObject;
	}

	public IFile updateFile(String workspace, String project, String path, byte[] content) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		IFile fileObject = projectObject.getFile(path);
		fileObject.getInternal().setContent(content);
		return fileObject;
	}

	public void deleteFile(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		projectObject.deleteFile(path);
	}

	public URI getURI(String workspace, String project, String path) throws URISyntaxException {
		StringBuilder relativePath = new StringBuilder(workspace);
		if (project != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(project);
		}
		if (path != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(path);
		}
		return new URI(relativePath.toString());
	}

	public Workspace renderWorkspaceTree(ICollection collection) {
		return WorkspaceJsonHelper.traverseWorkspace(collection,
				IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(), WORKSPACES);
	}

	public Project renderProjectTree(ICollection collection) {
		return WorkspaceJsonHelper.traverseProject(collection,
				IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(), WORKSPACES);
	}

	public Folder renderFolderTree(ICollection collection) {
		return WorkspaceJsonHelper.traverseFolder(collection, IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(),
				WORKSPACES);
	}

}
