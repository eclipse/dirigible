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
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.project.ProjectMetadataManager;
import org.eclipse.dirigible.core.git.project.ProjectPropertiesVerifier;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
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
 * Pull project(s) from a Git repository and optionally publish it.
 */
public class PullCommand {

	private static final String CHANGES_BRANCH = "changes_branch_"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(PullCommand.class);

	/** The publisher core service. */
	@Inject
	private PublisherCoreService publisherCoreService;

	/** The project metadata manager. */
	@Inject
	private ProjectMetadataManager projectMetadataManager;

	/** The verifier. */
	@Inject
	private ProjectPropertiesVerifier verifier;

	/**
	 * Execute a Pull command.
	 *
	 * @param workspace
	 *            the workspace
	 * @param repositories
	 *            the projects
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param branch
	 *            the branch
	 * @param publishAfterPull
	 *            the publish after pull
	 */
	public void execute(final IWorkspace workspace, List<String> repositories, final String username, final String password, 
			final String branch, final boolean publishAfterPull) {
		if (repositories.size() == 0) {
			logger.warn("No repository is selected for the Pull action");
		}
		List<String> pulledProjects = new ArrayList<String>();
		boolean atLeastOne = false;
		for (String repositoryName : repositories) {
			if (verifier.verify(workspace.getName(), repositoryName)) {
				logger.debug(String.format("Start pulling %s repository...", repositoryName));
				boolean pulled = pullProjectFromGitRepository(workspace, repositoryName, username, password, branch);
				atLeastOne = atLeastOne ? atLeastOne : pulled;
				logger.debug(String.format("Pull of the repository %s finished.", repositoryName));
				List<String> projects = GitFileUtils.getGitRepositoryProjects(workspace.getName(), repositoryName);
				pulledProjects.addAll(projects);
			} else {
				logger.warn(String.format("Project %s is local only. Select a previously cloned project for Pull operation.", repositoryName));
			}
		}

		if (atLeastOne && publishAfterPull) {
			publishProjects(workspace, pulledProjects);
		}

	}

	/**
	 * Pull project from git repository by executing several low level Git commands.
	 *
	 * @param workspace
	 *            the workspace
	 * @param repositoryName
	 *            the selected project
	 * @return true, if successful
	 */
	boolean pullProjectFromGitRepository(final IWorkspace workspace, String repositoryName, final String username, final String password, final String branch) {
		final String errorMessage = String.format("Error occurred while pulling repository [%s]", repositoryName);

		List<String> projects = GitFileUtils.getGitRepositoryProjects(workspace.getName(), repositoryName);
		for (String projectName: projects) {
			projectMetadataManager.ensureProjectMetadata(workspace, projectName);
		}

		try {

			File gitDirectory = GitFileUtils.getGitDirectoryByRepositoryName(workspace.getName(), repositoryName);
			IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory.getCanonicalPath());

			String gitRepositoryBranch = gitConnector.getBranch();
			logger.debug(String.format("Starting pull of the repository [%s] for the branch %s...", repositoryName, gitRepositoryBranch));
			gitConnector.pull(username, password);
			logger.debug(String.format("Pull of the repository %s finished.", repositoryName));

			int numberOfConflictingFiles = gitConnector.status().getConflicting().size();
			logger.debug(String.format("Number of conflicting files in the repository [%s]: %d.", repositoryName, numberOfConflictingFiles));
			
			if (numberOfConflictingFiles > 0) {
				String message = String.format(
					"Repository [%s] has %d conflicting file(s). You can use Push to submit your changes in a new branch for further merge or use Reset to abandon your changes.",
					repositoryName, numberOfConflictingFiles);
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
		} catch (GitConnectorException e) {
			logger.error(errorMessage, e);
		}
		return true;
	}

	/**
	 * Publish projects.
	 *
	 * @param workspace
	 *            the workspace
	 * @param pulledProjects
	 *            the pulled projects
	 */
	protected void publishProjects(IWorkspace workspace, List<String> pulledProjects) {
		if (pulledProjects.size() > 0) {
			for (String pulledProject : pulledProjects) {
				List<IProject> projects = workspace.getProjects();
				for (IProject project : projects) {
					if (project.getName().equals(pulledProject)) {
						try {
							publisherCoreService.createPublishRequest(workspace.getName(), pulledProject);
							logger.info(String.format("Project [%s] has been published", project.getName()));
						} catch (PublisherException e) {
							logger.error(String.format("An error occurred while publishing the pulled project [%s]", project.getName()), e);
						}
						break;
					}
				}
			}
		}
	}

}
