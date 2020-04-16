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
 * Share the local project to the remote Git repository.
 */
public class ShareCommand {

//	private static final String DOT_GIT = ".git"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(ShareCommand.class);

	/** The project metadata manager. */
	@Inject
	private ProjectMetadataManager projectMetadataManager;

//	/** The verifier. */
//	@Inject
//	private ProjectPropertiesVerifier verifier;

	/** The git file utils. */
	@Inject
	private GitFileUtils gitFileUtils;

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
	 */
	public void execute(final IWorkspace workspace, final IProject project, String repositoryUri, String repositoryBranch, final String commitMessage,
			final String username, final String password, final String email) {
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
	 */
	private void shareToGitRepository(final String user, final IWorkspace workspace, final IProject project, final String commitMessage, final String username,
			final String email, final String password, final String gitRepositoryURI, final String gitRepositoryBranch) {
		final String errorMessage = String.format("Error occurred while sharing project [%s]", project.getName());
		
		String branch = gitRepositoryBranch != null ? gitRepositoryBranch : ProjectMetadataManager.BRANCH_MASTER;

		projectMetadataManager.ensureProjectMetadata(workspace, project.getName(), gitRepositoryURI, branch);

		File tempGitDirectory = null;
		try {
			final String repositoryName = GitFileUtils.generateGitRepositoryName(gitRepositoryURI); //gitRepositoryURI.substring(gitRepositoryURI.lastIndexOf("/") + 1, gitRepositoryURI.lastIndexOf(DOT_GIT));
			tempGitDirectory = GitFileUtils.createGitDirectory(user, workspace.getName(), repositoryName);

			logger.debug(String.format("Cloning repository %s, with username %s for branch %s in the directory %s ...", gitRepositoryURI, username,
					branch, tempGitDirectory.getCanonicalPath()));
			GitConnectorFactory.cloneRepository(tempGitDirectory.getCanonicalPath(), gitRepositoryURI, username, password, branch);
			logger.debug(String.format("Cloning repository %s finished.", gitRepositoryURI));

			IGitConnector gitConnector = GitConnectorFactory.getConnector(tempGitDirectory.getCanonicalPath());

			GitFileUtils.copyProjectToDirectory(project, tempGitDirectory);
			gitConnector.add(IGitConnector.GIT_ADD_ALL_FILE_PATTERN);
			gitConnector.commit(commitMessage, username, email, true);
			gitConnector.push(username, password);

//			String lastSHA = gitConnector.getLastSHAForBranch(branch);
//			GitProjectProperties properties = new GitProjectProperties(gitRepositoryURI, lastSHA);
//			String user = UserFacade.getName();
			
			// delete the local project
			project.delete();
			// link the already share project
			gitFileUtils.importProjectFromGitRepositoryToWorkspace(tempGitDirectory, project.getPath());

//			gitFileUtils.saveGitPropertiesFile(properties, user, workspace.getName(), project.getName());

			String message = String.format("Project [%s] successfully shared.", project.getName());
			logger.info(message);
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
					logger.error("Double-check the correctness of the [Username] and/or [Password] or [Git Repository URI]");
				}
			}
		} catch (GitAPIException e) {
			logger.error(errorMessage, e);
		} catch (IOException e) {
			logger.error(errorMessage, e);
		} catch (GitConnectorException e) {
			logger.error(errorMessage, e);
		} finally {
//			try {
//				GitFileUtils.deleteDirectory(tempGitDirectory);
//			} catch (IOException e) {
//				logger.error(e.getMessage(), e);
//			}
		}
	}
}
