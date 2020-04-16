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

//	private static final String DOT_GIT = ".git"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(ResetCommand.class);

//	/** The project metadata manager. */
//	@Inject
//	private ProjectMetadataManager projectMetadataManager;

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
	 * @param projects
	 *            the projects
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param branch
	 *            the branch
	 */
	public void execute(final IWorkspace workspace, final IProject[] projects, final String username, final String password, final String branch) {

		if (projects.length == 0) {
			logger.warn("No project is selected for the Reset action");
		}
		String user = UserFacade.getName();
		for (IProject selectedProject : projects) {
			if (verifier.verify(workspace, selectedProject)) {
				logger.debug(String.format("Start reseting project [%s]...", selectedProject.getName()));
				hardReset(user, workspace, selectedProject, username, password, branch);
				logger.debug(String.format("Reset of the project [%s] finished.", selectedProject.getName()));
			} else {
				logger.warn(String.format("Project [%s] is local only. Select a previously cloned project for Reset operation.", selectedProject));
			}
		}

	}

	/**
	 * Performing a hard reset low level git commands.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	private void hardReset(final String user, final IWorkspace workspace, final IProject project, final String username, final String password, final String branch) {
		final String errorMessage = String.format("While hard reseting project [%s] error occurred", project.getName());
//		File tempGitDirectory = null;
		try {
//			GitProjectProperties gitProperties = gitFileUtils.getGitPropertiesForProject(workspace, project);
//			String gitRepositoryURI = gitProperties.getURL();
//
//			String gitRepositoryBranch =  branch != null ? branch : ProjectMetadataManager.BRANCH_MASTER;
//			try {
//				projectMetadataManager.ensureProjectMetadata(workspace, project.getName(), gitRepositoryURI, gitRepositoryBranch);
//				if (branch == null) {
//					gitRepositoryBranch = ProjectMetadataManager.getBranch(project);
//					if (gitRepositoryBranch == null) {
//						gitRepositoryBranch = ProjectMetadataManager.BRANCH_MASTER;
//					}
//				}
//			} catch (IOException e) {
//				logger.error(errorMessage, e);
//			}

//			String repositoryName = GitFileUtils.generateGitRepositoryName(gitRepositoryURI); //gitRepositoryURI.substring(gitRepositoryURI.lastIndexOf("/") + 1, gitRepositoryURI.lastIndexOf(DOT_GIT));
//			tempGitDirectory = GitFileUtils.createGitDirectory(repositoryName);
//
//			logger.debug(String.format("Cloning repository %s, with username %s for branch %s in the directory %s ...", gitRepositoryURI, username,
//					gitRepositoryBranch, tempGitDirectory.getCanonicalPath()));
//			GitConnectorFactory.cloneRepository(tempGitDirectory.getCanonicalPath(), gitRepositoryURI, null, null, gitRepositoryBranch);
//			logger.debug(String.format("Cloning repository %s finished.", gitRepositoryURI));
//
//			IGitConnector gitConnector = GitConnectorFactory.getRepository(tempGitDirectory.getCanonicalPath());

//			gitFileUtils.deleteRepositoryProject(project);

//			String dirigibleUser = UserFacade.getName();
//			String workspacePath = GitProjectProperties.generateWorkspacePath(workspace, dirigibleUser);

//			final String lastSHA = gitConnector.getLastSHAForBranch(gitRepositoryBranch);
//			gitProperties.setSHA(lastSHA);

//			gitFileUtils.importProject(tempGitDirectory, workspacePath, dirigibleUser, workspace.getName(), gitProperties, null);
			
			
			String gitDirectoryPath = gitFileUtils.getAbsolutePath(project.getPath());
			File gitDirectory = new File(gitDirectoryPath).getCanonicalFile();
			IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory.getCanonicalPath());
			try {
				gitConnector.hardReset();
			} catch (GitAPIException e) {
				logger.debug(e.getMessage(), e.getMessage());
			}

			String message = String.format("Project [%s] successfully reset.", project.getName());
			logger.info(message);
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
