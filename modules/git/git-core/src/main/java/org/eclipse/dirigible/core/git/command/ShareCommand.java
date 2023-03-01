/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
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
import org.eclipse.dirigible.core.git.model.GitShareModel;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.service.PublisherCoreService;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.project.ProjectMetadataManager;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Share the local project to the remote Git repository.
 */
public class ShareCommand {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ShareCommand.class);

	/** The project metadata manager. */
	private ProjectMetadataManager projectMetadataManager = new ProjectMetadataManager();

	/** The publisher core service. */
	private PublisherCoreService publisherCoreService = new PublisherCoreService();

	/**
	 * Execute the share command.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param model
	 *            the git share model
	 * @throws GitConnectorException in case of exception
	 */
	public void execute(final IWorkspace workspace, final IProject project, GitShareModel model) throws GitConnectorException {
		String user = UserFacade.getName();
		shareToGitRepository(user, workspace, project, model);
	}

	/**
	 * Share to git repository.
	 *
	 * @param user
	 *            the user
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param model
	 *            the git share model
	 * @throws GitConnectorException in case of exception
	 */
	private void shareToGitRepository(final String user, final IWorkspace workspace, final IProject project, GitShareModel model) throws GitConnectorException {
		String errorMessage = String.format("Error occurred while sharing project [%s].", project.getName());

		projectMetadataManager.ensureProjectMetadata(workspace, project.getName());

		File tempGitDirectory = null;
		try {
			final String repositoryName = GitFileUtils.generateGitRepositoryName(model.getRepository()); //gitRepositoryURI.substring(gitRepositoryURI.lastIndexOf("/") + 1, gitRepositoryURI.lastIndexOf(DOT_GIT));
			tempGitDirectory = GitFileUtils.getGitDirectory(user, workspace.getName(), repositoryName);
			boolean isExistingGitRepository = tempGitDirectory != null;
			if (!isExistingGitRepository) {
				tempGitDirectory = GitFileUtils.createGitDirectory(user, workspace.getName(), repositoryName);
			}

			if (!isExistingGitRepository) {
				try {
					if (logger.isDebugEnabled()) {logger.debug(String.format("Cloning repository %s, with username %s for branch %s in the directory %s ...", model.getRepository(), model.getUsername(), model.getBranch(), tempGitDirectory.getCanonicalPath()));}
					GitConnectorFactory.cloneRepository(tempGitDirectory.getCanonicalPath(), model.getRepository(), model.getUsername(), model.getPassword(), model.getBranch());
					if (logger.isDebugEnabled()) {logger.debug(String.format("Cloning repository %s finished.", model.getRepository()));}
				} catch (Throwable e) {
					GitFileUtils.deleteGitDirectory(user, workspace.getName(), repositoryName);
					throw e;
				}
			} else {
				if (logger.isDebugEnabled()) {logger.debug(String.format("Sharing to existing git repository %s, with username %s for branch %s in the directory %s ...", model.getRepository(), model.getUsername(), model.getBranch(), tempGitDirectory.getCanonicalPath()));}
			}

			IGitConnector gitConnector = GitConnectorFactory.getConnector(tempGitDirectory.getCanonicalPath());

			GitFileUtils.copyProjectToDirectory(project, tempGitDirectory, model.isShareInRootFolder());
			try {
				gitConnector.add(IGitConnector.GIT_ADD_ALL_FILE_PATTERN);
				gitConnector.commit(model.getCommitMessage(), model.getUsername(), model.getEmail(), true);
				gitConnector.push(model.getUsername(), model.getPassword());
			} catch (Throwable e) {
				GitFileUtils.deleteGitDirectory(user, workspace.getName(), repositoryName);
				throw e;
			}
			
			// delete the local project
			project.delete();
			
			// link the already share project
			File projectGitDirectory = null;
			String projectPath = null;
			if (model.isShareInRootFolder()) {
				projectGitDirectory = tempGitDirectory;
				StringBuilder projectPathBuilder = new StringBuilder();
				String[] projectPathSegments = new RepositoryPath(project.getPath()).getSegments();
				for (int i = 0; i < projectPathSegments.length - 1; i ++) {
					projectPathBuilder.append(File.separator).append(projectPathSegments[i]);
				}
				projectPathBuilder.append(File.separator).append(projectGitDirectory.getName());
				projectPath = projectPathBuilder.toString();
				publisherCoreService.createUnpublishRequest(workspace.getName(), project.getName());
			} else {				
				projectGitDirectory = new File(tempGitDirectory, project.getName());
				projectPath = project.getPath();
			}
			GitFileUtils.importProjectFromGitRepositoryToWorkspace(projectGitDirectory, projectPath);

			String message = String.format("Project [%s] successfully shared.", project.getName());
			if (logger.isInfoEnabled()) {logger.info(message);}
		} catch (IOException | GitAPIException | PublisherException | GitConnectorException e) {
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
			if (logger.isErrorEnabled()) {logger.error(errorMessage);}
			throw new GitConnectorException(errorMessage, e);
		}
	}
}
