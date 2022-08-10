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
import java.util.List;

import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.model.GitPushModel;
import org.eclipse.dirigible.core.git.project.ProjectMetadataManager;
import org.eclipse.dirigible.core.git.project.ProjectPropertiesVerifier;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Push the changes of a project from the local repository to remote Git repository.
 */
public class PushCommand {

	/** The Constant SHOULD_BE_EMPTY_REPOSITORY. */
	private static final String SHOULD_BE_EMPTY_REPOSITORY = "Should be empty repository: {}";

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(PushCommand.class);

	/** The project metadata manager. */
	private ProjectMetadataManager projectMetadataManager = new ProjectMetadataManager();

	/** The verifier. */
	private ProjectPropertiesVerifier verifier = new ProjectPropertiesVerifier();


	/**
	 * Execute the Push command.
	 *
	 * @param workspace
	 *            the workspace
	 * @param model
	 *            the git push modeladd
	 * @throws GitConnectorException in case of exception
	 */
	public void execute(final IWorkspace workspace, GitPushModel model) throws GitConnectorException {
		if (model.getProjects().size() == 0) {
			logger.warn("No repository is selected for the Push action");
		}
		for (String repositoryName : model.getProjects()) {
			if (verifier.verify(workspace.getName(), repositoryName)) {
				logger.debug(String.format("Start pushing repository [%s]...", repositoryName));
				pushProjectToGitRepository(workspace, repositoryName, model);
				logger.debug(String.format("Push of the repository [%s] finished.", repositoryName));
			} else {
				logger.warn(String.format("Project [%s] is local only. Select a previously clonned project for Push operation.", repositoryName));
			}
		}

	}

	/**
	 * Push project to git repository by executing several low level Git commands.
	 *
	 * @param workspace            the workspace
	 * @param repositoryName the repository name
	 * @param model            the git push model
	 * @throws GitConnectorException in case of exception
	 */
	private void pushProjectToGitRepository(final IWorkspace workspace, String repositoryName, GitPushModel model) throws GitConnectorException {

		String errorMessage = String.format("Error occurred while pushing repository [%s]. ", repositoryName);
		
		try {
			List<String> projects = GitFileUtils.getGitRepositoryProjects(workspace.getName(), repositoryName);
			for (String projectName : projects) {				
				projectMetadataManager.ensureProjectMetadata(workspace, projectName);
			}

			File gitDirectory = GitFileUtils.getGitDirectoryByRepositoryName(workspace.getName(), repositoryName);
			IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory.getCanonicalPath());

			String gitRepositoryBranch = gitConnector.getBranch();
			if (model.isAutoAdd()) {
				for (String projectName : projects) {				
					gitConnector.add(projectName);
				}
			}
			if (model.isAutoCommit()) {
				gitConnector.commit(model.getCommitMessage(), model.getUsername(), model.getEmail(), true);
			}
			try {
				gitConnector.pull(model.getUsername(), model.getPassword());
			} catch (GitAPIException e) {
				logger.debug(SHOULD_BE_EMPTY_REPOSITORY, e.getMessage());
			}
			int numberOfConflictingFiles = gitConnector.status().getConflicting().size();
			if (numberOfConflictingFiles == 0) {
				
				gitConnector.push(model.getUsername(), model.getPassword());

				logger.info(String.format("Repository [%s] has been pushed to remote repository.", repositoryName));
			} else {
				String statusLineMessage = String.format("Project has %d conflicting file(s).", numberOfConflictingFiles);
				logger.warn(statusLineMessage);
				String message = String.format(
						"Project has %d conflicting file(s). Please merge to [%s] and then continue working on project.",
						numberOfConflictingFiles, gitRepositoryBranch);
				logger.warn(message);
			}
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
