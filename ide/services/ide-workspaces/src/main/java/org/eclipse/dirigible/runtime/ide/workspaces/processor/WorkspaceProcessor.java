/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.ide.workspaces.processor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.dirigible.api.v3.core.ExtensionsServiceFacade;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.api.v3.utils.UrlFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.helpers.FileSystemUtils;
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
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing the Workspace Service incoming requests.
 */
public class WorkspaceProcessor {

    /** The Constant EXTENSION_PARAMETER_PATH. */
    private static final String EXTENSION_PARAMETER_PATH = "path";

    /** The Constant EXTENSION_PARAMETER_PROJECT. */
    private static final String EXTENSION_PARAMETER_PROJECT = "project";

    /** The Constant EXTENSION_PARAMETER_WORKSPACE. */
    private static final String EXTENSION_PARAMETER_WORKSPACE = "workspace";

    /** The Constant EXTENSION_POINT_IDE_WORKSPACE_ON_SAVE. */
    private static final String EXTENSION_POINT_IDE_WORKSPACE_ON_SAVE = "ide-workspace-on-save";

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceProcessor.class);

    /** The Constant WORKSPACES_SERVICE_PREFIX. */
    private static final String WORKSPACES_SERVICE_PREFIX = "ide/workspaces";

    /** The workspaces core service. */
    private WorkspacesCoreService workspacesCoreService = new WorkspacesCoreService();

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
     * @param workspace the workspace
     * @return the workspace
     */
    public IWorkspace getWorkspace(String workspace) {
        return workspacesCoreService.getWorkspace(workspace);
    }

    /**
     * Creates the workspace.
     *
     * @param workspace the workspace
     * @return the i workspace
     */
    public IWorkspace createWorkspace(String workspace) {
        return workspacesCoreService.createWorkspace(workspace);
    }

    /**
     * Delete workspace.
     *
     * @param workspace the workspace
     */
    public void deleteWorkspace(String workspace) {
        workspacesCoreService.deleteWorkspace(workspace);
    }

    /**
     * Exists workspace.
     *
     * @param workspace the workspace
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
     * @param workspace the workspace
     * @param project   the project
     * @return the project
     */
    public IProject getProject(String workspace, String project) {
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        return workspaceObject.getProject(project);
    }

    /**
     * Creates the project.
     *
     * @param workspace the workspace
     * @param project   the project
     * @return the i project
     */
    public IProject createProject(String workspace, String project) {
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        return workspaceObject.createProject(project);
    }

    /**
     * Delete project.
     *
     * @param workspace the workspace
     * @param project   the project
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void deleteProject(String workspace, String project) throws IOException {
        String user = UserFacade.getName();
        List<String> gitRepositories = FileSystemUtils.getGitRepositories(user, workspace);
        List<String> gitProjects = new ArrayList<String>();
        for (String repositoryName : gitRepositories) {
            gitProjects.addAll(FileSystemUtils.getGitRepositoryProjects(user, workspace, repositoryName));
        }
        boolean isGitProject = gitProjects.contains(project);

        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        IProject workspaceProject = workspaceObject.getProject(project);
        String repositoryRootFolder = workspaceObject.getRepository().getParameter("REPOSITORY_ROOT_FOLDER");

        File projectFile = null;
        if (isGitProject && repositoryRootFolder != null && workspaceProject.exists()) {
            projectFile = new File(repositoryRootFolder + workspaceProject.getPath());
            isGitProject = projectFile.exists() && FileUtils.isSymlink(projectFile);
        }

        if (isGitProject) {
            Files.delete(projectFile.toPath());
        } else {
            workspaceObject.deleteProject(project);
        }
    }

    /**
     * Exists project.
     *
     * @param workspace the workspace
     * @param project   the project
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
     * @param workspace the workspace
     * @param project   the project
     * @param path      the path
     * @return the folder
     */
    public IFolder getFolder(String workspace, String project, String path) {
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        IProject projectObject = workspaceObject.getProject(project);
        return projectObject.getFolder(path);
    }

    /**
     * Exists folder.
     *
     * @param workspace the workspace
     * @param project   the project
     * @param path      the path
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
     * @param workspace the workspace
     * @param project   the project
     * @param path      the path
     * @return the i folder
     */
    public IFolder createFolder(String workspace, String project, String path) {
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        IProject projectObject = workspaceObject.getProject(project);
        return projectObject.createFolder(path);
    }

    /**
     * Delete folder.
     *
     * @param workspace the workspace
     * @param project   the project
     * @param path      the path
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
     * @param workspace the workspace
     * @param project   the project
     * @param path      the path
     * @return the file
     */
    public IFile getFile(String workspace, String project, String path) {
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        IProject projectObject = workspaceObject.getProject(project);
        return projectObject.getFile(path);
    }

    /**
     * Exists file.
     *
     * @param workspace the workspace
     * @param project   the project
     * @param path      the path
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
     * @param workspace   the workspace
     * @param project     the project
     * @param path        the path
     * @param content     the content
     * @param contentType the content type
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
     * @param workspace the workspace
     * @param project   the project
     * @param path      the path
     * @param content   the content
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
     * @param workspace the workspace
     * @param project   the project
     * @param path      the path
     */
    public void deleteFile(String workspace, String project, String path) {
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        IProject projectObject = workspaceObject.getProject(project);
        projectObject.deleteFile(path);
    }

    /**
     * Gets the uri.
     *
     * @param workspace the workspace
     * @param project   the project
     * @param path      the path
     * @return the uri
     * @throws URISyntaxException the URI syntax exception
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
     * @param workspace the workspace
     * @return the workspace descriptor
     */
    public WorkspaceDescriptor renderWorkspaceTree(IWorkspace workspace) {
        return WorkspaceJsonHelper.describeWorkspace(workspace,
                IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(), "");
    }

    /**
     * Render project tree.
     *
     * @param project the project
     * @return the project descriptor
     */
    public ProjectDescriptor renderProjectTree(String workspace, IProject project) {
        return WorkspaceJsonHelper.describeProject(workspace, project, IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(),
                "");
    }

    /**
     * Render folder tree.
     *
     * @param folder the folder
     * @return the folder descriptor
     */
    public FolderDescriptor renderFolderTree(String workspace, IFolder folder) {
        return WorkspaceJsonHelper.describeFolder(workspace, folder, IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(),
                "");
    }

    /**
     * Render file description.
     *
     * @param file the file
     * @return the file descriptor
     */
    public FileDescriptor renderFileDescription(String workspace, IFile file) {
        return WorkspaceJsonHelper.describeFile(workspace, file, IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(), "");
    }

    /**
     * Render file descriptions.
     *
     * @param files the files
     * @return the file descriptors
     */
    public List<FileDescriptor> renderFileDescriptions(List<IFile> files) {
        List<FileDescriptor> fileDescriptors = new ArrayList<>();
        for (IFile file : files) {
            fileDescriptors.add(renderFileDescription("", file));
        }
        return fileDescriptors;
    }

    /**
     * Copy project.
     *
     * @param sourceWorkspace the source workspace
     * @param targetWorkspace the target workspace
     * @param sourceProject   the source project
     * @param targetProject   the target project
     */
    public void copyProject(String sourceWorkspace, String targetWorkspace, String sourceProject, String targetProject) {
        if (existsProject(targetWorkspace, targetProject)) {
            int inc = 1;
            String projectName = targetProject + " (copy " + inc + ")";
            while (
                    existsProject(
                            targetWorkspace,
                            projectName
                    )
            ) {
                projectName = targetProject + " (copy " + ++inc + ")";
            }
            targetProject = projectName;
        }
        if (sourceWorkspace.equals(targetWorkspace)) {
            IWorkspace workspaceObject = workspacesCoreService.getWorkspace(targetWorkspace);
            workspaceObject.copyProject(sourceProject, targetProject);
        } else { // This is a temporary workaround
            IWorkspace sourceWorkspaceObject = workspacesCoreService.getWorkspace(sourceWorkspace);
            IProject sourceProjectObject = sourceWorkspaceObject.getProject(sourceProject);
            String basePath = sourceProjectObject.getPath();
            IProject targetProjectObject = createProject(targetWorkspace, targetProject);
            List<Pair<String, String>> allFilesFolders = getAllFilesFolders(sourceProjectObject, false);
            for (Pair<String, String> path : allFilesFolders) {
                if (path.getKey().equals("file")) {
                    String filePath = path.getValue().replace(basePath, "");
                    IFile sourceFile = sourceProjectObject.getFile(filePath);
                    targetProjectObject.createFile(
                            filePath,
                            sourceFile.getContent(),
                            sourceFile.isBinary(),
                            sourceFile.getContentType()
                    );
                } else {
                    targetProjectObject.createFolder(
                            path.getValue().replace(
                                    basePath + IRepository.SEPARATOR, ""
                            ) + IRepository.SEPARATOR
                    );
                }
            }
        }
    }

    /**
     * Copy folder.
     *
     * @param sourceWorkspace  the source workspace
     * @param targetWorkspace  the target workspace
     * @param sourceProject    the source project
     * @param sourceFolderPath the source folder path
     * @param targetProject    the target project
     * @param targetBasePath   the target folder path
     * @param targetFolderName the target folder name
     */
    public void copyFolder(String sourceWorkspace, String targetWorkspace, String sourceProject, String sourceFolderPath, String targetProject, String targetBasePath, String targetFolderName) {
        String targetFolderPath = targetBasePath + targetFolderName;
        if (existsFolder(targetWorkspace, targetProject, targetFolderPath) || existsFile(targetWorkspace, targetProject, targetFolderPath)) {
            int inc = 1;
            String folderName = targetFolderName + " (copy " + inc + ")";
            while (
                    existsFolder(
                            targetWorkspace,
                            targetProject,
                            targetBasePath + folderName
                    )
            ) {
                folderName = targetFolderName + " (copy " + ++inc + ")";
            }
            targetFolderPath = targetBasePath + folderName;
        }
        if (sourceWorkspace.equals(targetWorkspace)) {
            IWorkspace workspaceObject = workspacesCoreService.getWorkspace(targetWorkspace);
            workspaceObject.copyFolder(sourceProject, sourceFolderPath, targetProject, targetFolderPath);
        } else { // This is a temporary workaround
            IWorkspace sourceWorkspaceObject = workspacesCoreService.getWorkspace(sourceWorkspace);
            IWorkspace targetWorkspaceObject = workspacesCoreService.getWorkspace(targetWorkspace);
            IProject sourceProjectObject = sourceWorkspaceObject.getProject(sourceProject);
            IProject targetProjectObject = targetWorkspaceObject.getProject(targetProject);
            IFolder baseFolder = sourceProjectObject.getFolder(sourceFolderPath);
            String basePath = baseFolder.getPath();
            List<Pair<String, String>> allFilesFolders = getAllFilesFolders(baseFolder, false);
            targetProjectObject.createFolder(targetFolderPath + IRepository.SEPARATOR);
            for (Pair<String, String> path : allFilesFolders) {
                if (path.getKey().equals("file")) {
                    String filePath = path.getValue().replace(basePath, "");
                    IFile sourceFile = baseFolder.getFile(filePath);
                    targetProjectObject.createFile(
                            targetFolderPath + filePath,
                            sourceFile.getContent(),
                            sourceFile.isBinary(),
                            sourceFile.getContentType()
                    );
                } else {
                    targetProjectObject.createFolder(
                            targetFolderPath + IRepository.SEPARATOR + path.getValue().replace(
                                    basePath + IRepository.SEPARATOR, ""
                            ) + IRepository.SEPARATOR
                    );
                }
            }
        }
    }

    /**
     * Copy file.
     *
     * @param sourceWorkspace the source workspace
     * @param targetWorkspace the target workspace
     * @param sourceProject   the source project
     * @param sourceFilePath  the source file path
     * @param targetProject   the target project
     * @param targetFilePath  the target file path
     */
    public void copyFile(String sourceWorkspace, String targetWorkspace, String sourceProject, String sourceFilePath, String targetProject, String targetFilePath) {
        if (sourceWorkspace.equals(targetWorkspace) && !existsFile(targetWorkspace, targetProject, targetFilePath) && !existsFolder(targetWorkspace, targetProject, targetFilePath)) {
            IWorkspace workspaceObject = workspacesCoreService.getWorkspace(targetWorkspace);
            workspaceObject.copyFile(sourceProject, sourceFilePath, targetProject, targetFilePath);
        } else { // This is a temporary workaround
            IWorkspace sourceWorkspaceObject = workspacesCoreService.getWorkspace(sourceWorkspace);
            IWorkspace targetWorkspaceObject = workspacesCoreService.getWorkspace(targetWorkspace);
            IFile sourceFile = sourceWorkspaceObject.getProject(sourceProject).getFile(sourceFilePath);
            String baseTargetPath = "";
            if (targetFilePath.endsWith(IRepository.SEPARATOR)) {
                baseTargetPath = targetFilePath;
                targetFilePath += sourceFile.getName();
            } else {
                baseTargetPath = IRepository.SEPARATOR + targetFilePath;
                targetFilePath += IRepository.SEPARATOR + sourceFile.getName();
            }
            if (existsFile(targetWorkspace, targetProject, targetFilePath) || existsFolder(targetWorkspace, targetProject, targetFilePath)) {
                String fileName = sourceFile.getName();
                String fileTitle = "";
                String fileExt = "";
                if (fileName.indexOf('.') != -1) {
                    fileTitle = fileName.substring(0, fileName.lastIndexOf("."));
                    fileExt = fileName.substring(fileName.lastIndexOf("."));
                } else {
                    fileTitle = fileName;
                }
                int inc = 1;
                fileName = fileTitle + " (copy " + inc + ")" + fileExt;
                while (
                        existsFile(
                                targetWorkspace,
                                targetProject,
                                baseTargetPath + IRepository.SEPARATOR + fileName
                        )
                ) {
                    fileName = fileTitle + " (copy " + ++inc + ")" + fileExt;
                }
                targetFilePath = baseTargetPath + IRepository.SEPARATOR + fileName;
            }
            targetWorkspaceObject.getProject(targetProject).createFile(
                    targetFilePath,
                    sourceFile.getContent(),
                    sourceFile.isBinary(),
                    sourceFile.getContentType()
            );
        }
    }

    /**
     * Move project.
     *
     * @param workspace     the workspace
     * @param sourceProject the source project
     * @param targetProject the target project
     */
    public void moveProject(String workspace, String sourceProject, String targetProject) {
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        workspaceObject.moveProject(sourceProject, targetProject);
    }

    /**
     * Move folder.
     *
     * @param workspace        the workspace
     * @param sourceProject    the source project
     * @param sourceFolderPath the source folder path
     * @param targetProject    the target project
     * @param targetFolderPath the target folder path
     */
    public void moveFolder(String workspace, String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath) {
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        workspaceObject.moveFolder(sourceProject, sourceFolderPath, targetProject, targetFolderPath);
    }

    /**
     * Move file.
     *
     * @param workspace      the workspace
     * @param sourceProject  the source project
     * @param sourceFilePath the source file path
     * @param targetProject  the target project
     * @param targetFilePath the target file path
     */
    public void moveFile(String workspace, String sourceProject, String sourceFilePath, String targetProject, String targetFilePath) {
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        workspaceObject.moveFile(sourceProject, sourceFilePath, targetProject, targetFilePath);
    }

    // Search

    /**
     * Free-text search in the files content.
     *
     * @param workspace the workspace
     * @param term      the term
     * @return the files list
     */
    public List<IFile> search(String workspace, String term) {
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        return workspaceObject.search(term);
    }

    // Find

    /**
     * Find files by pattern.
     *
     * @param pattern the pattern
     * @return the files list
     */
    public List<IFile> find(String pattern) {
        List<IFile> allFiles = new ArrayList<IFile>();
        List<IWorkspace> workspaces = workspacesCoreService.getWorkspaces();
        for (IWorkspace workspace : workspaces) {
            IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace.getName());
            List<IFile> files = workspaceObject.find(pattern);
            allFiles.addAll(files);
        }
        return allFiles;
    }

    /**
     * Find files by pattern within a given workspace.
     *
     * @param workspace the workspace
     * @param pattern   the pattern
     * @return the files list
     */
    public List<IFile> find(String workspace, String pattern) {
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        return workspaceObject.find(pattern);
    }

    /**
     * Triggers the post save file extensions.
     *
     * @param workspace the workspace
     * @param project   the project name
     * @param path      the file path
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
                	if (logger.isTraceEnabled()) {logger.trace("Workspace On Save Extension: {} triggered...", module);}
                    ScriptEngineExecutorsManager.executeServiceModule("javascript", module, context);
                    if (logger.isTraceEnabled()) {logger.trace("Workspace On Save Extension: {} finshed.", module);}
                } catch (Exception | Error e) {
                	if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
                }
            }
        } catch (ExtensionsException e) {
        	if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
        }
    }

    /**
     * Link project.
     *
     * @param workspace the workspace
     * @param sourceProject the source project
     * @param targetPath the target path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void linkProject(String workspace, String sourceProject, String targetPath) throws IOException {
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
        workspaceObject.linkProject(sourceProject, targetPath);
    }

    // Other

    /**
     * Gets the all files folders.
     *
     * @param baseFolder the base folder
     * @param includeBaseFolder the include base folder
     * @return the all files folders
     */
    private List<Pair<String, String>> getAllFilesFolders(IFolder baseFolder, Boolean includeBaseFolder) {
        List<Pair<String, String>> allFilesFolders = new ArrayList<>();
        List<IFile> files = baseFolder.getFiles();
        List<IFolder> folders = baseFolder.getFolders();
        if (files.size() == 0 && includeBaseFolder) {
            allFilesFolders.add(Pair.of("folder", baseFolder.getPath()));
        }
        for (IFile file : files) {
            allFilesFolders.add(Pair.of("file", file.getPath()));
        }
        for (IFolder folder : folders) {
            allFilesFolders.addAll(getAllFilesFolders(folder, true));
        }
        return allFilesFolders;
    }
}
