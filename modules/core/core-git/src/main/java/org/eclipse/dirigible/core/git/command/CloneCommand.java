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
package org.eclipse.dirigible.core.git.command;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.project.ProjectMetadataDependency;
import org.eclipse.dirigible.core.git.project.ProjectMetadataManager;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.service.PublisherCoreService;
import org.eclipse.dirigible.core.publisher.synchronizer.PublisherSynchronizer;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clone project(s) from a Git repository and optionally publish it.
 */
public class CloneCommand {

	private static final Logger logger = LoggerFactory.getLogger(CloneCommand.class);
	
	/** The Constant PATTERN_USERS_WORKSPACE. */
	public static final String PATTERN_USERS_WORKSPACE = IRepositoryStructure.PATH_USERS + "/%s/%s/"; // /users/john/workspace1

	/** The workspaces core service. */
	private WorkspacesCoreService workspacesCoreService = new WorkspacesCoreService();

	/** The publisher core service. */
	private PublisherCoreService publisherCoreService = new PublisherCoreService();

	/** The project metadata manager. */
	private ProjectMetadataManager projectMetadataManager = new ProjectMetadataManager();

	/** The git file utils. */
	private GitFileUtils gitFileUtils = new GitFileUtils();

	/**
	 * Execute a Clone command.
	 *
	 * @param repositoryUri
	 *            the repository uri
	 * @param repositoryBranch
	 *            the repository branch
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param workspaceName
	 *            the workspace name
	 * @param publishAfterClone
	 *            the publish after clone
	 * @param projectName
	 *            the workspace name
	 * @throws GitConnectorException
	 *             the git connector exception
	 */
	public void execute(String repositoryUri, String repositoryBranch, String username, String password, String workspaceName,
			boolean publishAfterClone, String projectName) throws GitConnectorException {
		try {
			if (repositoryUri != null && !repositoryUri.endsWith(GitFileUtils.DOT_GIT)) {
				repositoryUri += GitFileUtils.DOT_GIT;
			}
			Set<String> clonedProjects = new HashSet<String>();
			logger.debug(String.format("Start cloning repository [%s] ...", repositoryUri));
			IWorkspace workspace = workspacesCoreService.getWorkspace(workspaceName);
			String user = UserFacade.getName();
			File gitDirectory = GitFileUtils.createGitDirectory(user, workspaceName, repositoryUri);
			cloneProject(user, repositoryUri, repositoryBranch, username, password, gitDirectory, workspace, clonedProjects, projectName);
			logger.debug(String.format("Cloning repository [%s] into folder [%s] finished successfully.", repositoryUri, gitDirectory.getCanonicalPath()));
			if (publishAfterClone) {
				publishProjects(workspace, clonedProjects);
			}
			logger.info(String.format("Project(s) has been cloned successfully from repository: [%s]", repositoryUri));
		} catch (IOException e) {
			throw new GitConnectorException(String.format("An error occurred while cloning repository: [%s]", repositoryUri), e);
		}
	}

//	/**
//	 * Creates the git directory.
//	 *
//	 * @param user the logged in user
//	 * @param workspace the current workspace
//	 * @param repositoryURI
//	 *            the repository URI
//	 * @return the file
//	 * @throws IOException
//	 *             Signals that an I/O exception has occurred.
//	 */
//	protected File createGitDirectory(String user, String workspace, String repositoryURI) throws IOException {
//		String repositoryName = GitFileUtils.generateGitRepositoryName(repositoryURI);
//		File gitDirectory = GitFileUtils.createGitDirectory(user, workspace, repositoryName);
//		return gitDirectory;
//	}

	/**
	 * Clone project execute several low level Git commands.
	 *
	 * @param user
	 *            logged in user
	 * @param repositoryURI
	 *            the repository URI
	 * @param repositoryBranch
	 *            the repository branch
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param gitDirectory
	 *            the git directory
	 * @param workspace
	 *            the workspace
	 * @param clonedProjects
	 *            the cloned projects
	 * @param optionalProjectName
	 *            an optional project name in case of an empty reposiotry
	 * @throws GitConnectorException
	 *             the git connector exception
	 */
	protected void cloneProject(final String user, final String repositoryURI, String repositoryBranch, final String username, final String password,
			File gitDirectory, IWorkspace workspace, Set<String> clonedProjects, String optionalProjectName) throws GitConnectorException {
		try {
			logger.debug(String.format("Cloning repository %s, with username %s for branch %s in the directory %s ...", repositoryURI, username,
					repositoryBranch, gitDirectory.getCanonicalPath()));
			GitConnectorFactory.cloneRepository(gitDirectory.getCanonicalPath(), repositoryURI, username, password, repositoryBranch);
			logger.debug(String.format("Cloning repository %s finished.", repositoryURI));

			String workspacePath = String.format(PATTERN_USERS_WORKSPACE, user, workspace.getName());

			logger.debug(String.format("Start importing projects for repository directory %s ...", gitDirectory.getCanonicalPath()));
			List<String> importedProjects = gitFileUtils.importProject(gitDirectory, workspacePath, user, workspace.getName(), optionalProjectName);
			logger.debug(String.format("Importing projects for repository directory %s finished", gitDirectory.getCanonicalPath()));

			for (String importedProject : importedProjects) {
				logger.info(String.format("Project [%s] was cloned", importedProject));
			}

			String[] projectNames = GitFileUtils.getValidProjectFolders(gitDirectory);
			for (String projectName : projectNames) {
				projectMetadataManager.ensureProjectMetadata(workspace, projectName);
				clonedProjects.add(projectName);
			}
			logger.debug("Start cloning dependencies ...");
			for (String projectName : projectNames) {
				logger.debug(String.format("Start cloning dependencies of the project %s...", projectName));
				cloneDependencies(user, username, password, workspace, clonedProjects, projectName);
				logger.debug(String.format("Cloning of dependencies of the project %s finished", projectName));
			}
			logger.debug("Cloning of dependencies finished");

		} catch (IOException | GitAPIException | GitConnectorException e) {
			String errorMessage = "An error occurred while cloning repository.";
			Throwable rootCause = e.getCause();
			if (rootCause != null) {
				rootCause = rootCause.getCause();
				if (rootCause instanceof UnknownHostException) {
					errorMessage += " Please check your network, or if proxy settings are set properly";
				} else {
					errorMessage += " Doublecheck the correctness of the [Username] and/or [Password] or [Git Repository URI]";
				}
			} else {
				errorMessage += " " + e.getMessage();
			}
			logger.error(errorMessage);
			throw new GitConnectorException(errorMessage, e);
		} finally {
//			try {
//				GitFileUtils.deleteDirectory(gitDirectory);
//			} catch (IOException e) {
//				logger.error(e.getMessage(), e);
//			}
		}
	}

	/**
	 * Clone project's dependencies if any along with the main project.
	 *
	 * @param user
	 *            the logged in user
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param workspace
	 *            the workspace
	 * @param clonedProjects
	 *            the cloned projects
	 * @param projectName
	 *            the project name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GitConnectorException
	 *             the git connector exception
	 */
	protected void cloneDependencies(final String user, final String username, final String password, IWorkspace workspace, Set<String> clonedProjects,
			String projectName) throws IOException, GitConnectorException {
		IProject selectedProject = workspace.getProject(projectName);
		ProjectMetadataDependency[] dependencies = ProjectMetadataManager.getDependencies(selectedProject);
		for (ProjectMetadataDependency dependency : dependencies) {
			String projectGuid = dependency.getGuid();
			if (!clonedProjects.contains(projectGuid)) {
				IProject alreadyClonedProject = workspace.getProject(projectGuid);
				String projectRepositoryURI = dependency.getUrl();
				String projectRepositoryBranch = dependency.getBranch();
				if (!alreadyClonedProject.exists()) {
					File projectGitDirectory = GitFileUtils.createGitDirectory(user, workspace.getName(), projectRepositoryURI);
					logger.debug(String.format("Start cloning of the project %s from the repository %s and branch %s into the directory %s ...",
							projectGuid, projectRepositoryURI, projectRepositoryBranch, projectGitDirectory.getCanonicalPath()));
					cloneProject(user, projectRepositoryURI, projectRepositoryBranch, username, password, projectGitDirectory, workspace,
							clonedProjects, null); // assume
				} else {
					logger.debug(String.format("Project %s has been already cloned, hence do pull instead.", projectGuid));
				}
				clonedProjects.add(projectGuid);

			} else {
				logger.debug(String.format("Project %s has been already cloned during this session.", projectGuid));
			}

			
		}
	}

	/**
	 * Publish projects.
	 *
	 * @param workspace
	 *            the workspace
	 * @param clonedProjects
	 *            the cloned projects
	 */
	protected void publishProjects(IWorkspace workspace, Set<String> clonedProjects) {
		if (clonedProjects.size() > 0) {
			for (String projectName : clonedProjects) {
				List<IProject> projects = workspace.getProjects();
				for (IProject project : projects) {
					if (project.getName().equals(projectName)) {
						try {
							publisherCoreService.createPublishRequest(generateWorkspacePath(workspace.getName()), projectName);
							PublisherSynchronizer.forceSynchronization();
							logger.info(String.format("Project [%s] has been published", project.getName()));
						} catch (PublisherException e) {
							logger.error(String.format("An error occurred while publishing the cloned project [%s]", project.getName()), e);
						}
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Generate workspace path.
	 *
	 * @param workspace
	 *            the workspace
	 * @return the string builder
	 */
	private String generateWorkspacePath(String workspace) {
		StringBuilder relativePath = new StringBuilder(IRepositoryStructure.PATH_USERS).append(IRepositoryStructure.SEPARATOR).append(UserFacade.getName())
				.append(IRepositoryStructure.SEPARATOR).append(workspace);
		return relativePath.toString();
	}

}
