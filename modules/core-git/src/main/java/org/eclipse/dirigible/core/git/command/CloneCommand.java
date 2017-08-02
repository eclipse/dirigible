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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.auth.UserFacade;
import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.project.ProjectMetadataDependency;
import org.eclipse.dirigible.core.git.project.ProjectMetadataManager;
import org.eclipse.dirigible.core.git.project.ProjectMetadataRepository;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.git.utils.GitProjectProperties;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.service.PublisherCoreService;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clone project(s) from a Git repository and optionally publish it
 */
public class CloneCommand {

	private static final String DOT_GIT = ".git";

	private static final Logger logger = LoggerFactory.getLogger(CloneCommand.class);

	@Inject
	private WorkspacesCoreService workspacesCoreService;

	@Inject
	private PublisherCoreService publisherCoreService;

	@Inject
	private ProjectMetadataManager projectMetadataManager;

	@Inject
	private GitFileUtils gitFileUtils;

	public void execute(String repositoryUri, String repositoryBranch, String username, String password, String workspaceName,
			boolean publishAfterClone) throws GitConnectorException {
		try {
			File gitDirectory = createGitDirectory(repositoryUri);
			Set<String> clonedProjects = new HashSet<String>();
			logger.debug(String.format("Start cloning repository [%s] ...", repositoryUri));
			IWorkspace workspace = workspacesCoreService.getWorkspace(workspaceName);
			cloneProject(repositoryUri, repositoryBranch, username, password, gitDirectory, workspace, clonedProjects);
			logger.debug(String.format("Cloning repository [%s] finished successfully.", repositoryUri));
			if (publishAfterClone) {
				publishProjects(workspace, clonedProjects);
			}
			logger.info(String.format("Project(s) has been cloned successfully from repository: [%s]", repositoryUri));
		} catch (IOException e) {
			throw new GitConnectorException(String.format("An error occurred while cloning repository: [%s]", repositoryUri), e);
		}
	}

	protected File createGitDirectory(String repositoryURI) throws IOException {
		String repositoryName = repositoryURI.substring(repositoryURI.lastIndexOf("/") + 1, repositoryURI.lastIndexOf(DOT_GIT));
		File gitDirectory = GitFileUtils.createTempDirectory(GitFileUtils.TEMP_DIRECTORY_PREFIX + repositoryName);
		return gitDirectory;
	}

	protected void cloneProject(final String repositoryURI, final String repositoryBranch, final String username, final String password,
			File gitDirectory, IWorkspace workspace, Set<String> clonedProjects) throws GitConnectorException {
		try {
			String user = UserFacade.getName();

			logger.debug(String.format("Cloning repository %s, with username %s for branch %s in the directory %s ...", repositoryURI, username,
					repositoryBranch, gitDirectory.getCanonicalPath()));
			GitConnectorFactory.cloneRepository(gitDirectory.getCanonicalPath(), repositoryURI, username, password, repositoryBranch);
			logger.debug(String.format("Cloning repository %s finished.", repositoryURI));

			IGitConnector gitConnector = GitConnectorFactory.getRepository(gitDirectory.getCanonicalPath());

			// final String lastSha = jgit.getLastSHAForBranch(MASTER);
			final String lastSha = gitConnector.getLastSHAForBranch(repositoryBranch);

			GitProjectProperties gitProperties = new GitProjectProperties(repositoryURI, lastSha);

			logger.debug(String.format("Git properties for the repository %s: %s", repositoryURI, gitProperties.toString()));

			String workspacePath = String.format(GitProjectProperties.PATTERN_USERS_WORKSPACE, user, workspace.getName());

			logger.debug(String.format("Start importing projects for repository directory %s ...", gitDirectory.getCanonicalPath()));
			List<String> importedProjects = gitFileUtils.importProject(gitDirectory, workspacePath, user, workspace.getName(), gitProperties);
			logger.debug(String.format("Importing projects for repository directory %s finished", gitDirectory.getCanonicalPath()));

			for (String importedProject : importedProjects) {
				logger.info(String.format("Project [%s] was cloned", importedProject));
			}

			String[] projectNames = GitFileUtils.getValidProjectFolders(gitDirectory);
			for (String projectName : projectNames) {
				projectMetadataManager.ensureProjectMetadata(workspace, projectName, repositoryURI, repositoryBranch);
				clonedProjects.add(projectName);
			}
			logger.debug("Start cloning dependencies ...");
			for (String projectName : projectNames) {
				logger.debug(String.format("Start cloning dependencies of the project %s...", projectName));
				cloneDependencies(username, password, workspace, clonedProjects, projectName);
				logger.debug(String.format("Cloning of dependencies of the project %s finished", projectName));
			}
			logger.debug("Cloning of dependencies finished");

		} catch (InvalidRemoteException e) {
			logger.error(e.getMessage(), e);
			throw new GitConnectorException(e);
		} catch (TransportException e) {
			logger.error("An error occurred while cloning repository", e);
			Throwable rootCause = e.getCause();
			if (rootCause != null) {
				rootCause = rootCause.getCause();
				if (rootCause instanceof UnknownHostException) {
					throw new GitConnectorException("Please check if proxy settings are set properly", e);
				}
				throw new GitConnectorException(e.getCause().getMessage(), e);
			}
			throw new GitConnectorException(e);
		} catch (GitAPIException e) {
			logger.error(e.getMessage(), e);
			throw new GitConnectorException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new GitConnectorException(e);
		} finally {
			GitFileUtils.deleteDirectory(gitDirectory);
		}
	}

	protected void cloneDependencies(final String username, final String password, IWorkspace workspace, Set<String> clonedProjects,
			String projectName) throws IOException, GitConnectorException {
		IProject selectedProject = workspace.getProject(projectName);
		ProjectMetadataDependency[] dependencies = ProjectMetadataManager.getDependencies(selectedProject);
		PullCommand pull = new PullCommand();
		if (dependencies != null) {
			for (ProjectMetadataDependency dependency : dependencies) {
				if (ProjectMetadataRepository.GIT.equalsIgnoreCase(dependency.getType())) {
					String projectGuid = dependency.getGuid();
					if (!clonedProjects.contains(projectGuid)) {
						IProject alreadyClonedProject = workspace.getProject(projectGuid);
						if (!alreadyClonedProject.exists()) {
							String projectRepositoryURI = dependency.getUrl();
							String projectRepositoryBranch = dependency.getBranch();
							File projectGitDirectory = createGitDirectory(projectRepositoryURI);
							logger.debug(
									String.format("Start cloning of the project %s from the repository %s and branch %s into the directory %s ...",
											projectGuid, projectRepositoryURI, projectRepositoryBranch, projectGitDirectory.getCanonicalPath()));
							cloneProject(projectRepositoryURI, projectRepositoryBranch, username, password, projectGitDirectory, workspace,
									clonedProjects); // assume
						} else {
							logger.debug(String.format("Project %s has been already cloned, hence do pull instead.", projectGuid));
							pull.pullProjectFromGitRepository(workspace, alreadyClonedProject);
						}
						clonedProjects.add(projectGuid);

					} else {
						logger.debug(String.format("Project %s has been already cloned during this session.", projectGuid));
					}

				} else {
					String errorMessage = String.format("Repository type is not supported: %s.", dependency.getType());
					logger.error(errorMessage);
					throw new GitConnectorException(errorMessage);
				}
			}
		}
	}

	protected void publishProjects(IWorkspace workspace, Set<String> clonedProjects) {
		if (clonedProjects.size() > 0) {
			for (String projectName : clonedProjects) {
				List<IProject> projects = workspace.getProjects();
				for (IProject project : projects) {
					if (project.getName().equals(projectName)) {
						try {
							publisherCoreService.createPublishRequest(workspace.getName(), projectName);
							logger.info(String.format("Project [%s] has been published", project.getName()));
						} catch (PublisherException e) {
							logger.error(String.format("An error occurred while publishing the cloned project [%s]"), e);
						}
						break;
					}
				}
			}
		}
	}

}
