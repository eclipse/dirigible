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

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.project.ProjectMetadataManager;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Share the local project to the remote Git repository.
 */
public class ShareCommand {

	private static final Logger logger = LoggerFactory.getLogger(ShareCommand.class);

	/** The project metadata manager. */
	private ProjectMetadataManager projectMetadataManager = new ProjectMetadataManager();

	/**
	 * Execute the share command.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param repositoryUri
	 *            the repository uri
	 * @param repositoryBranch
	 *            the repository branch
	 * @param commitMessage
	 *            the commit message
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param email
	 *            the email
	 * @throws GitConnectorException 
	 */
	public void execute(final IWorkspace workspace, final IProject project, String repositoryUri, String repositoryBranch, final String commitMessage,
			final String username, final String password, final String email) throws GitConnectorException {
		String user = UserFacade.getName();
		shareToGitRepository(user, workspace, project, commitMessage, username, email, password, repositoryUri, repositoryBranch);
	}

	/**
	 * Share to git repository.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param commitMessage
	 *            the commit message
	 * @param username
	 *            the username
	 * @param email
	 *            the email
	 * @param password
	 *            the password
	 * @param gitRepositoryURI
	 *            the git repository URI
	 * @param gitRepositoryBranch
	 *            the git repository branch
	 * @throws GitConnectorException 
	 */
	private void shareToGitRepository(final String user, final IWorkspace workspace, final IProject project, final String commitMessage, final String username,
			final String email, final String password, final String gitRepositoryURI, final String gitRepositoryBranch) throws GitConnectorException {
		String errorMessage = String.format("Error occurred while sharing project [%s].", project.getName());

		projectMetadataManager.ensureProjectMetadata(workspace, project.getName());

		File tempGitDirectory = null;
		try {
			final String repositoryName = GitFileUtils.generateGitRepositoryName(gitRepositoryURI); //gitRepositoryURI.substring(gitRepositoryURI.lastIndexOf("/") + 1, gitRepositoryURI.lastIndexOf(DOT_GIT));
			tempGitDirectory = GitFileUtils.getGitDirectory(user, workspace.getName(), repositoryName);
			boolean isExistingGitRepository = tempGitDirectory != null;
			if (!isExistingGitRepository) {
				tempGitDirectory = GitFileUtils.createGitDirectory(user, workspace.getName(), repositoryName);
			}

			if (!isExistingGitRepository) {
				try {
					logger.debug(String.format("Cloning repository %s, with username %s for branch %s in the directory %s ...", gitRepositoryURI, username, gitRepositoryBranch, tempGitDirectory.getCanonicalPath()));
					GitConnectorFactory.cloneRepository(tempGitDirectory.getCanonicalPath(), gitRepositoryURI, username, password, gitRepositoryBranch);
					logger.debug(String.format("Cloning repository %s finished.", gitRepositoryURI));
				} catch (Throwable e) {
					GitFileUtils.deleteGitDirectory(user, workspace.getName(), repositoryName);
					throw e;
				}
			} else {
				logger.debug(String.format("Sharing to existing git repository %s, with username %s for branch %s in the directory %s ...", gitRepositoryURI, username, gitRepositoryBranch, tempGitDirectory.getCanonicalPath()));
			}

			IGitConnector gitConnector = GitConnectorFactory.getConnector(tempGitDirectory.getCanonicalPath());

			GitFileUtils.copyProjectToDirectory(project, tempGitDirectory);
			try {
				gitConnector.add(IGitConnector.GIT_ADD_ALL_FILE_PATTERN);
				gitConnector.commit(commitMessage, username, email, true);
				gitConnector.push(username, password);
			} catch (Throwable e) {
				GitFileUtils.deleteGitDirectory(user, workspace.getName(), repositoryName);
				throw e;
			}
			
			// delete the local project
			project.delete();
			
			// link the already share project
			File projectGitDirectory = new File(tempGitDirectory, project.getName());
			GitFileUtils.importProjectFromGitRepositoryToWorkspace(projectGitDirectory, project.getPath());

			String message = String.format("Project [%s] successfully shared.", project.getName());
			logger.info(message);
		} catch (IOException | GitAPIException | GitConnectorException e) {
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
		}
	}
}
