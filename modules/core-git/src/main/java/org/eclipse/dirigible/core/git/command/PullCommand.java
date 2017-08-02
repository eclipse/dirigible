/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.core.git.command;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.auth.UserFacade;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.project.ProjectMetadataManager;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.git.utils.GitProjectProperties;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.service.PublisherCoreService;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pull project(s) from a Git repository and optionally publish it
 */
public class PullCommand {

	private static final String CHANGES_BRANCH = "changes_branch_"; //$NON-NLS-1$
	private static final String SLASH = "/"; //$NON-NLS-1$
	private static final String DOT_GIT = ".git"; //$NON-NLS-1$
	private static final String MASTER = "master"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(PullCommand.class);

	@Inject
	private PublisherCoreService publisherCoreService;

	@Inject
	private ProjectMetadataManager projectMetadataManager;

	@Inject
	private GitProjectPropertyVerifier verifier;

	@Inject
	private GitFileUtils gitFileUtils;

	public void execute(final IWorkspace workspace, final IProject[] projects, boolean publishAfterPull) {
		if (projects.length == 0) {
			logger.warn("No project is selected for the Pull action");
		}

		List<IProject> pulledProjects = new ArrayList<IProject>();
		boolean atLeastOne = false;
		for (IProject selectedProject : projects) {
			if (verifier.verify(workspace, selectedProject)) {
				logger.debug(String.format("Start pulling %s project...", selectedProject.getName()));
				boolean pulled = pullProjectFromGitRepository(workspace, selectedProject);
				atLeastOne = atLeastOne ? atLeastOne : pulled;
				logger.debug(String.format("Pull of the Project %s finished.", selectedProject.getName()));
				pulledProjects.add(selectedProject);
			} else {
				logger.warn(String.format("Project %s is local only. Select a previously clonned project for Pull operation.", selectedProject));
			}
		}

		if (atLeastOne && publishAfterPull) {
			publishProjects(workspace, pulledProjects);
		}

	}

	boolean pullProjectFromGitRepository(final IWorkspace workspace, final IProject selectedProject) {
		final String errorMessage = String.format("Error occurred while pulling project [%s]", selectedProject.getName());
		GitProjectProperties gitProperties = null;
		try {
			gitProperties = gitFileUtils.getGitPropertiesForProject(workspace, selectedProject);
			if (gitProperties != null) {
				logger.debug(String.format("Git properties for the project [%s]: %s", selectedProject.getName(), gitProperties.toString()));
			} else {
				logger.debug(String.format("Git properties file for the project [%s] is null", selectedProject.getName()));
				return false;
			}
		} catch (IOException e) {
			logger.error("This is not a git project!", e);
			return false;
		}

		String gitRepositoryURI = gitProperties.getURL();
		String gitRepositoryBranch = MASTER;
		try {
			projectMetadataManager.ensureProjectMetadata(workspace, selectedProject.getName(), gitRepositoryURI, MASTER);
			gitRepositoryBranch = ProjectMetadataManager.getBranch(selectedProject);
			logger.debug(String.format("Repository URL for the project [%s]: %s", selectedProject.getName(), gitRepositoryURI));
			logger.debug(String.format("Branch for the project [%s]: %s", selectedProject.getName(), gitRepositoryBranch));
		} catch (IOException e) {
			logger.error("Error during pull!", e);
		}

		File tempGitDirectory = null;
		try {
			String repositoryName = gitRepositoryURI.substring(gitRepositoryURI.lastIndexOf(SLASH) + 1, gitRepositoryURI.lastIndexOf(DOT_GIT));
			tempGitDirectory = GitFileUtils.createTempDirectory(GitFileUtils.TEMP_DIRECTORY_PREFIX + repositoryName);
			logger.debug(
					String.format("Temp Git Directory for the project [%s]: %s", selectedProject.getName(), tempGitDirectory.getCanonicalPath()));

			logger.debug(String.format("Cloning repository %s, with username %s for branch %s in the directory %s ...", gitRepositoryURI, "[nobody]",
					gitRepositoryBranch, tempGitDirectory.getCanonicalPath()));
			GitConnectorFactory.cloneRepository(tempGitDirectory.getCanonicalPath(), gitRepositoryURI, null, null, gitRepositoryBranch);
			logger.debug(String.format("Cloning repository %s finished.", gitRepositoryURI));

			IGitConnector gitConnector = GitConnectorFactory.getRepository(tempGitDirectory.getCanonicalPath());

			String lastSHA = gitProperties.getSHA();

			gitProperties.setSHA(gitConnector.getLastSHAForBranch(gitRepositoryBranch));

			final String changesBranch = CHANGES_BRANCH + System.currentTimeMillis() + "_" + UserFacade.getName();
			logger.debug(String.format("Last SHA for the project [%s]: %s", selectedProject.getName(), lastSHA));
			logger.debug(String.format("'Changes' branch for the project [%s]: %s", selectedProject.getName(), changesBranch));

			logger.debug(String.format("Staring checkout of the project [%s] for the branch %s...", selectedProject.getName(), gitRepositoryBranch));
			gitConnector.checkout(lastSHA);
			logger.debug(String.format("Checkout of the project [%s] finished.", selectedProject.getName()));

			gitConnector.createBranch(changesBranch, lastSHA);

			logger.debug(String.format("Staring checkout of the project [%s] for the branch %s...", selectedProject.getName(), changesBranch));
			gitConnector.checkout(changesBranch);
			logger.debug(String.format("Checkout of the project [%s] finished.", selectedProject.getName()));

			logger.debug(String.format("Clean and copy the sources of the project [%s] in directory %s...", selectedProject.getName(),
					tempGitDirectory.getCanonicalPath()));
			GitFileUtils.deleteProjectFolderFromDirectory(tempGitDirectory, selectedProject.getName());
			GitFileUtils.copyProjectToDirectory(selectedProject, tempGitDirectory);
			logger.debug(String.format("Clean and copy the sources of the project [%s] finished.", selectedProject.getName()));

			gitConnector.add(IGitConnector.GIT_ADD_ALL_FILE_PATTERN);
			gitConnector.commit("", "", "", true); //$NON-NLS-1$
			logger.debug(String.format("Commit changes for the project [%s] finished.", selectedProject.getName()));

			logger.debug(String.format("Staring pull of the project [%s] for the branch %s...", selectedProject.getName(), gitRepositoryBranch));
			gitConnector.pull();
			logger.debug(String.format("Pull of the project %s finished.", selectedProject.getName()));

			int numberOfConflictingFiles = gitConnector.status().getConflicting().size();
			logger.debug(String.format("Number of conflicting files in the project [%s]: %d.", selectedProject.getName(), numberOfConflictingFiles));
			if (numberOfConflictingFiles == 0) {
				logger.debug(String.format("No conflicting files in the project [%s]. Staring checkout and rebase...", selectedProject.getName()));
				gitConnector.checkout(gitRepositoryBranch);
				logger.debug(String.format("Checkout for the project [%s] finished.", selectedProject.getName()));
				gitConnector.rebase(changesBranch);
				logger.debug(String.format("Rebase for the project [%s] finished.", selectedProject.getName()));

				String dirigibleUser = UserFacade.getName();

				gitFileUtils.deleteRepositoryProject(selectedProject);

				String workspacePath = GitProjectProperties.generateWorkspacePath(workspace, dirigibleUser);

				logger.debug(String.format("Starting importing projects from the Git directory %s.", tempGitDirectory.getCanonicalPath()));
				gitFileUtils.importProject(tempGitDirectory, workspacePath, dirigibleUser, workspace.getName(), gitProperties);
				logger.debug(String.format("Importing projects from the Git directory %s finished.", tempGitDirectory.getCanonicalPath()));
			} else {
				String message = String.format(
						"Project [%s] has %d conflicting file(s). You can use Push to submit your changes in a new branch for further merge or use Reset to abandon your changes.",
						selectedProject.getName(), numberOfConflictingFiles);
				logger.error(message);
			}
		} catch (CheckoutConflictException e) {
			logger.error(errorMessage, e);
		} catch (IOException e) {
			logger.error(errorMessage, e);
		} catch (InvalidRemoteException e) {
			logger.error(errorMessage, e);
		} catch (TransportException e) {
			logger.error(errorMessage, e);
			Throwable rootCause = e.getCause();
			if (rootCause != null) {
				rootCause = rootCause.getCause();
				logger.error(errorMessage, e);
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
		return true;
	}

	protected void publishProjects(IWorkspace workspace, List<IProject> pulledProjects) {
		if (pulledProjects.size() > 0) {
			for (IProject pulledProject : pulledProjects) {
				List<IProject> projects = workspace.getProjects();
				for (IProject project : projects) {
					if (project.getName().equals(pulledProject.getName())) {
						try {
							publisherCoreService.createPublishRequest(workspace.getName(), pulledProject.getName());
							logger.info(String.format("Project [%s] has been published", project.getName()));
						} catch (PublisherException e) {
							logger.error(String.format("An error occurred while publishing the pulled project [%s]"), e);
						}
						break;
					}
				}
			}
		}
	}

}
