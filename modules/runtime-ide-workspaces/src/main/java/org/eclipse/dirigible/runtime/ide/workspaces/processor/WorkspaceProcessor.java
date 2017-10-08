package org.eclipse.dirigible.runtime.ide.workspaces.processor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.api.v3.utils.UrlFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.json.FileDescriptor;
import org.eclipse.dirigible.core.workspace.json.FolderDescriptor;
import org.eclipse.dirigible.core.workspace.json.ProjectDescriptor;
import org.eclipse.dirigible.core.workspace.json.WorkspaceDescriptor;
import org.eclipse.dirigible.core.workspace.json.WorkspaceJsonHelper;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing the Registry Service incoming requests
 */
public class WorkspaceProcessor {

	private static final Logger logger = LoggerFactory.getLogger(WorkspaceProcessor.class);

	private static final String WORKSPACES_SERVICE_PREFIX = "ide/workspaces";

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

	public boolean existsFolder(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		return projectObject.existsFolder(path);
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

	public boolean existsFile(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		return projectObject.existsFile(path);
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
		StringBuilder relativePath = new StringBuilder(WORKSPACES_SERVICE_PREFIX).append(IRepositoryStructure.SEPARATOR).append(workspace);
		if (project != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(project);
		}
		if (path != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(path);
		}
		return new URI(UrlFacade.escape(relativePath.toString()));
	}

	public WorkspaceDescriptor renderWorkspaceTree(IWorkspace workspace) {
		return WorkspaceJsonHelper.describeWorkspace(workspace,
				IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(), "");
	}

	public ProjectDescriptor renderProjectTree(IProject project) {
		return WorkspaceJsonHelper.describeProject(project, IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(),
				"");
	}

	public FolderDescriptor renderFolderTree(IFolder folder) {
		return WorkspaceJsonHelper.describeFolder(folder, IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(),
				"");
	}

	public FileDescriptor renderFileDescription(IFile file) {
		return WorkspaceJsonHelper.describeFile(file, IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(), "");
	}

	public void copyProject(String workspace, String sourceProject, String targetProject) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.copyProject(sourceProject, targetProject);
	}

	public void copyFolder(String workspace, String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.copyFolder(sourceProject, sourceFolderPath, targetProject, targetFolderPath);
	}

	public void copyFile(String workspace, String sourceProject, String sourceFilePath, String targetProject, String targetFilePath) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.copyFile(sourceProject, sourceFilePath, targetProject, targetFilePath);
	}

	public void moveProject(String workspace, String sourceProject, String targetProject) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.moveProject(sourceProject, targetProject);
	}

	public void moveFolder(String workspace, String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.moveFolder(sourceProject, sourceFolderPath, targetProject, targetFolderPath);
	}

	public void moveFile(String workspace, String sourceProject, String sourceFilePath, String targetProject, String targetFilePath) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.moveFile(sourceProject, sourceFilePath, targetProject, targetFilePath);
	}

}
