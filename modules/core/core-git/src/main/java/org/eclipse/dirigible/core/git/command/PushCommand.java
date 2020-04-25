/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.git.command;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

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
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Push the changes of a project from the local repository to remote Git repository.
 */
public class PushCommand {

	private static final String SHOULD_BE_EMPTY_REPOSITORY = "Should be empty repository: {}";

//	private static final String CHANGES_BRANCH = "changes_branch_"; //$NON-NLS-1$

//	private static final String DOT_GIT = ".git"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(PushCommand.class);

//	/** The project metadata manager. */
	@Inject
	private ProjectMetadataManager projectMetadataManager;

	/** The verifier. */
	@Inject
	private ProjectPropertiesVerifier verifier;

	/** The git file utils. */
	@Inject
	private GitFileUtils gitFileUtils;

	/**
	 * Execute the Push command.
	 *
	 * @param workspace
	 *            the workspace
	 * @param projects
	 *            the projects
	 * @param commitMessage
	 *            the commit message
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param email
	 *            the email
	 * @param branch
	 *            the branch
	 */
	public void execute(final IWorkspace workspace, final IProject[] projects, final String commitMessage, final String username,
			final String password, final String email, final String branch, boolean add, boolean commit) {
		if (projects.length == 0) {
			logger.warn("No project is selected for the Push action");
		}
		String user = UserFacade.getName();
		for (IProject selectedProject : projects) {
			if (verifier.verify(workspace, selectedProject)) {
				logger.debug(String.format("Start pushing project [%s]...", selectedProject.getName()));
				pushProjectToGitRepository(user, workspace, selectedProject, commitMessage, username, password, email, branch, add, commit);
				logger.debug(String.format("Push of the project [%s] finished.", selectedProject.getName()));
			} else {
				logger.warn(String.format("Project [%s] is local only. Select a previously clonned project for Push operation.", selectedProject));
			}
		}

	}

	/**
	 * Push project to git repository by executing several low level Git commands.
	 *
	 * @param workspace
	 *            the workspace
	 * @param selectedProject
	 *            the selected project
	 * @param commitMessage
	 *            the commit message
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param email
	 *            the email
	 */
	private void pushProjectToGitRepository(final String user, final IWorkspace workspace, final IProject selectedProject, final String commitMessage,
			final String username, final String password, final String email, final String branch, boolean add, boolean commit) {

		final String errorMessage = String.format("Error occurred while pushing project [%s]. ", selectedProject.getName());
		
		try {
			projectMetadataManager.ensureProjectMetadata(workspace, selectedProject.getName());

			String gitDirectoryPath = gitFileUtils.getAbsolutePath(selectedProject.getPath());
			File gitDirectory = new File(gitDirectoryPath).getCanonicalFile();
			IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory.getCanonicalPath());

			String gitRepositoryBranch = gitConnector.getBranch();
			if (add) {
				gitConnector.add(selectedProject.getName());
			}
			if (commit) {
				gitConnector.commit(commitMessage, username, email, true);
			}
			try {
				gitConnector.pull(username, password);
			} catch (GitAPIException e) {
				logger.debug(SHOULD_BE_EMPTY_REPOSITORY, e.getMessage());
			}
			int numberOfConflictingFiles = gitConnector.status().getConflicting().size();
			if (numberOfConflictingFiles == 0) {
				
				gitConnector.push(username, password);

				logger.info(String.format("Project [%s] has been pushed to remote repository.", selectedProject.getName()));
			} else {
				String statusLineMessage = String.format("Project has %d conflicting file(s).", numberOfConflictingFiles);
				logger.warn(statusLineMessage);
				String message = String.format(
						"Project has %d conflicting file(s). Please merge to [%s] and then continue working on project.",
						numberOfConflictingFiles, gitRepositoryBranch);
				logger.warn(message);
			}
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
		} catch (GitConnectorException e) {
			logger.error(errorMessage, e);
		}
	}
}
