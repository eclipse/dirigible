/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v4.git;
import org.apache.commons.io.FileUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;

import org.eclipse.dirigible.core.git.*;
import org.eclipse.dirigible.core.workspace.json.ProjectDescriptor;
import org.eclipse.dirigible.core.workspace.json.WorkspaceGitHelper;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitFacade implements IScriptingFacade {

    private static WorkspacesCoreService workspacesCoreService = new WorkspacesCoreService();

    public static void initRepository(String username, String email, String workspaceName, String projectName, String repositoryName, String commitMessage) throws IOException, GitAPIException, GitConnectorException {

        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspaceName);
        IProject projectObject = workspaceObject.getProject(projectName);
        String user = UserFacade.getName();
        File gitDirectory = GitFileUtils.getGitDirectoryByRepositoryName(workspaceName, repositoryName);
        boolean isExistingGitRepository = gitDirectory != null;

        if (!isExistingGitRepository) {
            gitDirectory = GitFileUtils.createGitDirectory(user, workspaceName, repositoryName);
        } else {
            throw new RefAlreadyExistsException("Git repository already exists");
        }
        GitFileUtils.copyProjectToDirectory(projectObject, gitDirectory);

        projectObject.delete();

        File projectGitDirectory = new File(gitDirectory, projectObject.getName());
        GitFileUtils gitFileUtils = new GitFileUtils();

        GitConnectorFactory.initRepository(gitDirectory.getCanonicalPath(), false);
        gitFileUtils.importProjectFromGitRepositoryToWorkspace(projectGitDirectory, projectObject.getPath());

        //the code below is needed because otherwise getHistory method will throw an error in the git perspective
        IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory.getCanonicalPath());
        gitConnector.add(IGitConnector.GIT_ADD_ALL_FILE_PATTERN);
        gitConnector.commit(commitMessage, username, email, true);
    }

    public static void commit(String username, String email, final String workspaceName, String repositoryName, String commitMessage, Boolean add) throws GitAPIException, IOException, GitConnectorException {
        String user = UserFacade.getName();
        File tempGitDirectory = GitFileUtils.getGitDirectory(user, workspaceName, repositoryName);
        boolean isExistingGitRepository = tempGitDirectory != null;
        if (!isExistingGitRepository) {
            throw new RefNotFoundException("Repository not found");
        }

        IGitConnector gitConnector = GitConnectorFactory.getConnector(tempGitDirectory.getCanonicalPath());
        gitConnector.add(IGitConnector.GIT_ADD_ALL_FILE_PATTERN);
        gitConnector.commit(commitMessage, username, email, add);
    }

    public static List<ProjectDescriptor> getGitRepositories(String workspaceName) {
        String user = UserFacade.getName();
        IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspaceName);
        if (!workspaceObject.exists()) {
            return null;
        }

        List<ProjectDescriptor> gitRepositories = new ArrayList<ProjectDescriptor>();
        File gitDirectory = GitFileUtils.getGitDirectory(user, workspaceName);
        if (gitDirectory != null) {
            for (File next : gitDirectory.listFiles()) {
                if (!next.isFile()) {
                    gitRepositories.add(WorkspaceGitHelper.describeProject(next));
                }
            }
        }
        return gitRepositories;
    }

    public static List<GitCommitInfo> getHistory(String repositoryName, String workspaceName, String path) throws GitConnectorException {
        try {
            List<GitCommitInfo> history = getConnector(workspaceName, repositoryName).getHistory(path);
            return history;
        } catch (Exception e) {
            throw new GitConnectorException(e);
        }
    }

    public static void deleteRepository(String workspaceName, String repositoryName) throws GitConnectorException {
        try {

            File gitRepository = GitFileUtils.getGitDirectoryByRepositoryName(workspaceName, repositoryName);
            if (gitRepository == null) {
                throw new RefNotFoundException("Repository not found");
            }

            IWorkspace workspaceApi = workspacesCoreService.getWorkspace(workspaceName);
            IProject[] workspaceProjects = workspaceApi.getProjects().toArray(new IProject[0]);
            for (IProject next : workspaceProjects) {
                if (next.exists()) {
                    next.delete();
                }
            }

            FileUtils.deleteDirectory(gitRepository);
        } catch (IOException | RefNotFoundException e) {
            throw new GitConnectorException("Unable to delete Git repository [" + repositoryName + "]", e);
        }
    }

    public static IGitConnector cloneRepository(String workspaceName, String repositoryUri, String username, String password, String branch) throws IOException, GitAPIException {
        String user = UserFacade.getName();
        File gitDirectory = GitFileUtils.createGitDirectory(user, workspaceName, repositoryUri);
        return GitConnectorFactory.cloneRepository(gitDirectory.getCanonicalPath(), repositoryUri, username, password, branch);
    }

    public static void pull(String workspaceName, String repositoryName, String username, String password) throws GitAPIException, IOException, GitConnectorException {
        getConnector(workspaceName, repositoryName).pull(username, password);
    }

    public static void push(String workspaceName, String repositoryName, String username, String password) throws GitAPIException, IOException, GitConnectorException {
        getConnector(workspaceName, repositoryName).push(username, password);
    }

    public static void checkout(String workspaceName, String repositoryName, String branchName) throws GitAPIException, IOException, GitConnectorException {
        getConnector(workspaceName, repositoryName).checkout(branchName);
    }

    public static void createBranch(String workspaceName, String repositoryName, String branchName, String startingPoint) throws GitAPIException, IOException, GitConnectorException {
        getConnector(workspaceName, repositoryName).createBranch(branchName, startingPoint);
    }

    public static void hardReset(String workspaceName, String repositoryName) throws GitAPIException, IOException, GitConnectorException {
        getConnector(workspaceName, repositoryName).hardReset();
    }

    public static void rebase(String workspaceName, String repositoryName, String branchName) throws GitAPIException, IOException, GitConnectorException {
        getConnector(workspaceName, repositoryName).rebase(branchName);
    }

    public static Status status(String workspaceName, String repositoryName) throws GitAPIException, IOException, GitConnectorException {
        return getConnector(workspaceName, repositoryName).status();
    }

    public static String getBranch(String workspaceName, String repositoryName) throws GitAPIException, IOException, GitConnectorException {
        return getConnector(workspaceName, repositoryName).getBranch();
    }

    public static List<GitBranch> getLocalBranches(String workspaceName, String repositoryName) throws GitAPIException, IOException, GitConnectorException {
        return getConnector(workspaceName, repositoryName).getLocalBranches();
    }

    public static List<GitBranch> getRemoteBranches(String workspaceName, String repositoryName) throws GitAPIException, IOException, GitConnectorException {
        return getConnector(workspaceName, repositoryName).getRemoteBranches();
    }

    public static List<GitChangedFile> getUnstagedChanges(String workspaceName, String repositoryName) throws GitAPIException, IOException, GitConnectorException {
        return getConnector(workspaceName, repositoryName).getUnstagedChanges();
    }

    public static List<GitChangedFile> getStagedChanges(String workspaceName, String repositoryName) throws GitAPIException, IOException, GitConnectorException {
        return getConnector(workspaceName, repositoryName).getStagedChanges();
    }

    public static String getFileContent(String workspaceName, String repositoryName, String filePath, String revStr) throws GitAPIException, IOException, GitConnectorException {
        return getConnector(workspaceName, repositoryName).getFileContent(filePath, revStr);
    }

    private static IGitConnector getConnector(String workspaceName, String repositoryName) throws GitConnectorException {
        try {
            String user = UserFacade.getName();
            File gitDirectory = GitFileUtils.getGitDirectory(user, workspaceName, repositoryName);
            IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory.getCanonicalPath());
            return gitConnector;
        } catch (IOException e) {
            throw new GitConnectorException(e);
        }
    }
}
