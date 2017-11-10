/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.git.command;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.project.ProjectMetadataManager;
import org.eclipse.dirigible.core.git.project.ProjectPropertiesVerifier;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.git.utils.GitProjectProperties;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reset the state of the local project, clear local changes
 */
public class ResetCommand {

	private static final String DOT_GIT = ".git"; //$NON-NLS-1$
	private static final String MASTER = "master"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(ResetCommand.class);

	@Inject
	private ProjectMetadataManager projectMetadataManager;

	@Inject
	private ProjectPropertiesVerifier verifier;

	@Inject
	private GitFileUtils gitFileUtils;

	public void execute(final IWorkspace workspace, final IProject[] projects, final String username, final String password) {

		if (projects.length == 0) {
			logger.warn("No project is selected for the Reset action");
		}

		for (IProject selectedProject : projects) {
			if (verifier.verify(workspace, selectedProject)) {
				logger.debug(String.format("Start reseting project [%s]...", selectedProject.getName()));
				hardReset(workspace, selectedProject, username, password);
				logger.debug(String.format("Reset of the project [%s] finished.", selectedProject.getName()));
			} else {
				logger.warn(String.format("Project [%s] is local only. Select a previously clonned project for Reset operation.", selectedProject));
			}
		}

	}

	private void hardReset(final IWorkspace workspace, final IProject project, final String username, final String password) {
		final String errorMessage = String.format("While hard reseting project [%s] error occurred", project.getName());
		File tempGitDirectory = null;
		try {
			GitProjectProperties gitProperties = gitFileUtils.getGitPropertiesForProject(workspace, project);
			String gitRepositoryURI = gitProperties.getURL();

			String gitRepositoryBranch = MASTER;
			try {
				projectMetadataManager.ensureProjectMetadata(workspace, project.getName(), gitRepositoryURI, MASTER);
				gitRepositoryBranch = ProjectMetadataManager.getBranch(project);
			} catch (IOException e) {
				logger.error(errorMessage, e);
			}

			String repositoryName = gitRepositoryURI.substring(gitRepositoryURI.lastIndexOf("/") + 1, gitRepositoryURI.lastIndexOf(DOT_GIT));
			tempGitDirectory = GitFileUtils.createTempDirectory(GitFileUtils.TEMP_DIRECTORY_PREFIX + repositoryName);

			logger.debug(String.format("Cloning repository %s, with username %s for branch %s in the directory %s ...", gitRepositoryURI, username,
					gitRepositoryBranch, tempGitDirectory.getCanonicalPath()));
			GitConnectorFactory.cloneRepository(tempGitDirectory.getCanonicalPath(), gitRepositoryURI, null, null, gitRepositoryBranch);
			logger.debug(String.format("Cloning repository %s finished.", gitRepositoryURI));

			IGitConnector gitConnector = GitConnectorFactory.getRepository(tempGitDirectory.getCanonicalPath());

			gitFileUtils.deleteRepositoryProject(project);

			String dirigibleUser = UserFacade.getName();
			String workspacePath = GitProjectProperties.generateWorkspacePath(workspace, dirigibleUser);

			final String lastSHA = gitConnector.getLastSHAForBranch(gitRepositoryBranch);
			gitProperties.setSHA(lastSHA);

			gitFileUtils.importProject(tempGitDirectory, workspacePath, dirigibleUser, workspace.getName(), gitProperties);

			String message = String.format("Project [%s] successfully reset.", project.getName());
			logger.info(message);
		} catch (IOException e) {
			logger.error(errorMessage, e);
		} catch (InvalidRemoteException e) {
			logger.error(errorMessage, e);
		} catch (TransportException e) {
			logger.error(errorMessage, e);
			Throwable rootCause = e.getCause();
			if (rootCause != null) {
				rootCause = rootCause.getCause();
				if (rootCause instanceof UnknownHostException) {
					logger.error("Please check if proxy settings are set properly");
				} else {
					logger.error("Doublecheck the correctness of the [Username] and/or [Password] or [Git Repository URI]");
				}
			}
		} catch (GitAPIException e) {
			logger.error(errorMessage, e);
		} finally {
			GitFileUtils.deleteDirectory(tempGitDirectory);
		}
	}

}
