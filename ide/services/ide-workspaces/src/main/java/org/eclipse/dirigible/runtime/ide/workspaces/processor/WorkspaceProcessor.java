/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.ide.workspaces.processor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.core.ExtensionsServiceFacade;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.api.v3.utils.UrlFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
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
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing the Workspace Service incoming requests.
 */
public class WorkspaceProcessor {
	
	private static final String EXTENSION_PARAMETER_PATH = "path";

	private static final String EXTENSION_PARAMETER_PROJECT = "project";

	private static final String EXTENSION_PARAMETER_WORKSPACE = "workspace";

	private static final String EXTENSION_POINT_IDE_WORKSPACE_ON_SAVE = "ide-workspace-on-save";

	private static final Logger logger = LoggerFactory.getLogger(WorkspaceProcessor.class);

	private static final String WORKSPACES_SERVICE_PREFIX = "ide/workspaces";

	@Inject
	private WorkspacesCoreService workspacesCoreService;

	/**
	 * Gets the workspaces core service.
	 *
	 * @return the workspaces core service
	 */
	public WorkspacesCoreService getWorkspacesCoreService() {
		return workspacesCoreService;
	}

	// Workspace

	/**
	 * List workspaces.
	 *
	 * @return the list
	 */
	public List<IWorkspace> listWorkspaces() {
		return workspacesCoreService.getWorkspaces();
	}

	/**
	 * Gets the workspace.
	 *
	 * @param workspace
	 *            the workspace
	 * @return the workspace
	 */
	public IWorkspace getWorkspace(String workspace) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		return workspaceObject;
	}

	/**
	 * Creates the workspace.
	 *
	 * @param workspace
	 *            the workspace
	 * @return the i workspace
	 */
	public IWorkspace createWorkspace(String workspace) {
		IWorkspace workspaceObject = workspacesCoreService.createWorkspace(workspace);
		return workspaceObject;
	}

	/**
	 * Delete workspace.
	 *
	 * @param workspace
	 *            the workspace
	 */
	public void deleteWorkspace(String workspace) {
		workspacesCoreService.deleteWorkspace(workspace);
	}

	/**
	 * Exists workspace.
	 *
	 * @param workspace
	 *            the workspace
	 * @return true, if successful
	 */
	public boolean existsWorkspace(String workspace) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		return workspaceObject.exists();
	}

	// Project

	/**
	 * Gets the project.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @return the project
	 */
	public IProject getProject(String workspace, String project) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		return projectObject;
	}

	/**
	 * Creates the project.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @return the i project
	 */
	public IProject createProject(String workspace, String project) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.createProject(project);
		return projectObject;
	}

	/**
	 * Delete project.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 */
	public void deleteProject(String workspace, String project) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.deleteProject(project);
	}

	/**
	 * Exists project.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @return true, if successful
	 */
	public boolean existsProject(String workspace, String project) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		return projectObject.exists();
	}

	// Folder

	/**
	 * Gets the folder.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @return the folder
	 */
	public IFolder getFolder(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		IFolder folderObject = projectObject.getFolder(path);
		return folderObject;
	}

	/**
	 * Exists folder.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @return true, if successful
	 */
	public boolean existsFolder(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		return projectObject.existsFolder(path);
	}

	/**
	 * Creates the folder.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @return the i folder
	 */
	public IFolder createFolder(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		IFolder folderObject = projectObject.createFolder(path);
		return folderObject;
	}

	/**
	 * Delete folder.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 */
	public void deleteFolder(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		projectObject.deleteFolder(path);
	}

	// File

	/**
	 * Gets the file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @return the file
	 */
	public IFile getFile(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		IFile fileObject = projectObject.getFile(path);
		return fileObject;
	}

	/**
	 * Exists file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @return true, if successful
	 */
	public boolean existsFile(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		return projectObject.existsFile(path);
	}

	/**
	 * Creates the file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @param contentType
	 *            the content type
	 * @return the i file
	 */
	public IFile createFile(String workspace, String project, String path, byte[] content, String contentType) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		if (contentType == null) {
			contentType = ContentTypeHelper.getContentType(ContentTypeHelper.getExtension(path));
		}
		boolean isBinary = ContentTypeHelper.isBinary(contentType);
		IFile fileObject = projectObject.createFile(path, content, isBinary, contentType);
		triggerOnSaveExtensions(workspace, project, path);
		return fileObject;
	}

	/**
	 * Update file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @return the i file
	 */
	public IFile updateFile(String workspace, String project, String path, byte[] content) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		IFile fileObject = projectObject.getFile(path);
		fileObject.getInternal().setContent(content);
		triggerOnSaveExtensions(workspace, project, path);
		return fileObject;
	}

	/**
	 * Delete file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 */
	public void deleteFile(String workspace, String project, String path) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		projectObject.deleteFile(path);
	}

	/**
	 * Gets the uri.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @return the uri
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
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

	/**
	 * Render workspace tree.
	 *
	 * @param workspace
	 *            the workspace
	 * @return the workspace descriptor
	 */
	public WorkspaceDescriptor renderWorkspaceTree(IWorkspace workspace) {
		return WorkspaceJsonHelper.describeWorkspace(workspace,
				IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(), "");
	}

	/**
	 * Render project tree.
	 *
	 * @param project
	 *            the project
	 * @return the project descriptor
	 */
	public ProjectDescriptor renderProjectTree(IProject project) {
		return WorkspaceJsonHelper.describeProject(project, IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(),
				"");
	}

	/**
	 * Render folder tree.
	 *
	 * @param folder
	 *            the folder
	 * @return the folder descriptor
	 */
	public FolderDescriptor renderFolderTree(IFolder folder) {
		return WorkspaceJsonHelper.describeFolder(folder, IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(),
				"");
	}

	/**
	 * Render file description.
	 *
	 * @param file
	 *            the file
	 * @return the file descriptor
	 */
	public FileDescriptor renderFileDescription(IFile file) {
		return WorkspaceJsonHelper.describeFile(file, IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(), "");
	}

	/**
	 * Render file descriptions.
	 *
	 * @param files
	 *            the files
	 * @return the file descriptors
	 */
	public List<FileDescriptor> renderFileDescriptions(List<IFile> files) {
		List<FileDescriptor> fileDescriptors = new ArrayList<>();
		for (IFile file : files) {
			fileDescriptors.add(renderFileDescription(file));
		}
		return fileDescriptors;
	}

	/**
	 * Copy project.
	 *
	 * @param workspace
	 *            the workspace
	 * @param sourceProject
	 *            the source project
	 * @param targetProject
	 *            the target project
	 */
	public void copyProject(String workspace, String sourceProject, String targetProject) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.copyProject(sourceProject, targetProject);
	}

	/**
	 * Copy folder.
	 *
	 * @param workspace
	 *            the workspace
	 * @param sourceProject
	 *            the source project
	 * @param sourceFolderPath
	 *            the source folder path
	 * @param targetProject
	 *            the target project
	 * @param targetFolderPath
	 *            the target folder path
	 */
	public void copyFolder(String workspace, String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.copyFolder(sourceProject, sourceFolderPath, targetProject, targetFolderPath);
	}

	/**
	 * Copy file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param sourceProject
	 *            the source project
	 * @param sourceFilePath
	 *            the source file path
	 * @param targetProject
	 *            the target project
	 * @param targetFilePath
	 *            the target file path
	 */
	public void copyFile(String workspace, String sourceProject, String sourceFilePath, String targetProject, String targetFilePath) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.copyFile(sourceProject, sourceFilePath, targetProject, targetFilePath);
	}

	/**
	 * Move project.
	 *
	 * @param workspace
	 *            the workspace
	 * @param sourceProject
	 *            the source project
	 * @param targetProject
	 *            the target project
	 */
	public void moveProject(String workspace, String sourceProject, String targetProject) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.moveProject(sourceProject, targetProject);
	}

	/**
	 * Move folder.
	 *
	 * @param workspace
	 *            the workspace
	 * @param sourceProject
	 *            the source project
	 * @param sourceFolderPath
	 *            the source folder path
	 * @param targetProject
	 *            the target project
	 * @param targetFolderPath
	 *            the target folder path
	 */
	public void moveFolder(String workspace, String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.moveFolder(sourceProject, sourceFolderPath, targetProject, targetFolderPath);
	}

	/**
	 * Move file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param sourceProject
	 *            the source project
	 * @param sourceFilePath
	 *            the source file path
	 * @param targetProject
	 *            the target project
	 * @param targetFilePath
	 *            the target file path
	 */
	public void moveFile(String workspace, String sourceProject, String sourceFilePath, String targetProject, String targetFilePath) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		workspaceObject.moveFile(sourceProject, sourceFilePath, targetProject, targetFilePath);
	}

	// Search

	/**
	 * Free-text search in the files content.
	 *
	 * @param workspace
	 *            the workspace
	 * @param term
	 *            the term
	 * @return the files list
	 */
	public List<IFile> search(String workspace, String term) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		List<IFile> files = workspaceObject.search(term);
		return files;
	}
	
	/**
	 * Triggers the post save file extensions
	 * 
	 * @param workspace the workspace
	 * @param project the project name
	 * @param path the file path
	 */
	private void triggerOnSaveExtensions(String workspace, String project, String path) {
		try {
			Map<Object, Object> context = new HashMap<Object, Object>();
			context.put(EXTENSION_PARAMETER_WORKSPACE, workspace);
			context.put(EXTENSION_PARAMETER_PROJECT, project);
			context.put(EXTENSION_PARAMETER_PATH, path);
			String[] modules = ExtensionsServiceFacade.getExtensions(EXTENSION_POINT_IDE_WORKSPACE_ON_SAVE);
			for (String module : modules) {
				try {
					logger.trace("Workspace On Save Extension: {} triggered...", module);
					ScriptEngineExecutorsManager.executeServiceModule("javascript", module, context);
					logger.trace("Workspace On Save Extension: {} finshed.", module);
				} catch (ScriptingException e) {
					logger.error(e.getMessage(), e);
				}
			}
		} catch (ExtensionsException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
