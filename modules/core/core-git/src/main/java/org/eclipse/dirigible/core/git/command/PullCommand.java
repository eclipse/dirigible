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

	/** The git file utils. */
	@Inject
	private GitFileUtils gitFileUtils;

	/**
	 * Execute a Pull command.
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
	 * @param publishAfterPull
	 *            the publish after pull
	 */
	public void execute(final IWorkspace workspace, final IProject[] projects, final String username, final String password, 
			final String branch, final boolean publishAfterPull) {
		if (projects.length == 0) {
			logger.warn("No project is selected for the Pull action");
		}
		String user = UserFacade.getName();
		List<IProject> pulledProjects = new ArrayList<IProject>();
		boolean atLeastOne = false;
		for (IProject selectedProject : projects) {
			if (verifier.verify(workspace, selectedProject)) {
				logger.debug(String.format("Start pulling %s project...", selectedProject.getName()));
				boolean pulled = pullProjectFromGitRepository(user, workspace, selectedProject, username, password, branch);
				atLeastOne = atLeastOne ? atLeastOne : pulled;
				logger.debug(String.format("Pull of the Project %s finished.", selectedProject.getName()));
				pulledProjects.add(selectedProject);
			} else {
				logger.warn(String.format("Project %s is local only. Select a previously cloned project for Pull operation.", selectedProject));
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
	 * @param selectedProject
	 *            the selected project
	 * @return true, if successful
	 */
	boolean pullProjectFromGitRepository(final String user, final IWorkspace workspace, final IProject selectedProject, final String username, final String password, final String branch) {
		final String errorMessage = String.format("Error occurred while pulling project [%s]", selectedProject.getName());

		projectMetadataManager.ensureProjectMetadata(workspace, selectedProject.getName());

		try {

			String gitDirectoryPath = gitFileUtils.getAbsolutePath(selectedProject.getPath());
			File gitDirectory = new File(gitDirectoryPath).getCanonicalFile();
			IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory.getCanonicalPath());

			String gitRepositoryBranch = gitConnector.getBranch();
			logger.debug(String.format("Starting pull of the project [%s] for the branch %s...", selectedProject.getName(), gitRepositoryBranch));
			gitConnector.pull(username, password);
			logger.debug(String.format("Pull of the project %s finished.", selectedProject.getName()));

			int numberOfConflictingFiles = gitConnector.status().getConflicting().size();
			logger.debug(String.format("Number of conflicting files in the project [%s]: %d.", selectedProject.getName(), numberOfConflictingFiles));
			
			if (numberOfConflictingFiles > 0) {
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
							logger.error(String.format("An error occurred while publishing the pulled project [%s]", project.getName()), e);
						}
						break;
					}
				}
			}
		}
	}

}
