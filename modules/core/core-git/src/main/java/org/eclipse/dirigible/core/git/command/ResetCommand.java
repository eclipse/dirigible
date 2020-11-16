/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.git.command;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.project.ProjectMetadataManager;
import org.eclipse.dirigible.core.git.project.ProjectPropertiesVerifier;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reset the state of the local project, clear local changes.
 */
public class ResetCommand {

	private static final Logger logger = LoggerFactory.getLogger(ResetCommand.class);

	/** The project metadata manager. */
	@Inject
	private ProjectMetadataManager projectMetadataManager;

	/** The verifier. */
	@Inject
	private ProjectPropertiesVerifier verifier;

	/** The git file utils. */
	@Inject
	private GitFileUtils gitFileUtils;

	/**
	 * Execute the Reset command.
	 *
	 * @param workspace
	 *            the workspace
	 * @param repositories
	 *            the repositories
	 */
	public void execute(String workspace, List<String> repositories) {
		if (repositories.size() == 0) {
			logger.warn("No repository is selected for the Reset action");
		}

		for (String repositoryName : repositories) {
			if (verifier.verify(workspace, repositoryName)) {
				logger.debug(String.format("Start reseting repository [%s]...", repositoryName));
				hardReset(workspace, repositoryName);
				logger.debug(String.format("Reset of the repository [%s] finished.", repositoryName));
			} else {
				logger.warn(String.format("Project [%s] is local only. Select a previously cloned repository for Reset operation.", repositoryName));
			}
		}

	}

	/**
	 * Performing a hard reset low level git commands.
	 *
	 * @param workspace
	 *            the workspace
	 * @param repositoryName
	 *            the project
	 */
	private void hardReset(String workspace, String repositoryName) {
		String errorMessage = String.format("While hard reseting repository [%s] error occurred", repositoryName);

		try {
			File gitDirectory = GitFileUtils.getGitDirectoryByRepositoryName(workspace, repositoryName).getCanonicalFile();
			IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory.getCanonicalPath());
			try {
				gitConnector.hardReset();
			} catch (GitAPIException e) {
				logger.debug(e.getMessage(), e.getMessage());
			}

			String message = String.format("Repository [%s] successfully reset.", repositoryName);
			logger.info(message);
		} catch (IOException | GitConnectorException e) {
			logger.error(errorMessage, e);
		}
	}

}
