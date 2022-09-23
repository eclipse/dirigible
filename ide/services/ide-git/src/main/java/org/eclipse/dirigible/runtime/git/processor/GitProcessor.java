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
package org.eclipse.dirigible.runtime.git.processor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.core.git.GitCommitInfo;
import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.command.CheckoutCommand;
import org.eclipse.dirigible.core.git.command.CloneCommand;
import org.eclipse.dirigible.core.git.command.CommitCommand;
import org.eclipse.dirigible.core.git.command.PullCommand;
import org.eclipse.dirigible.core.git.command.PushCommand;
import org.eclipse.dirigible.core.git.command.ResetCommand;
import org.eclipse.dirigible.core.git.command.ShareCommand;
import org.eclipse.dirigible.core.git.command.UpdateDependenciesCommand;
import org.eclipse.dirigible.core.git.model.BaseGitModel;
import org.eclipse.dirigible.core.git.model.GitCheckoutModel;
import org.eclipse.dirigible.core.git.model.GitCloneModel;
import org.eclipse.dirigible.core.git.model.GitDiffModel;
import org.eclipse.dirigible.core.git.model.GitProjectChangedFiles;
import org.eclipse.dirigible.core.git.model.GitProjectLocalBranches;
import org.eclipse.dirigible.core.git.model.GitProjectRemoteBranches;
import org.eclipse.dirigible.core.git.model.GitPullModel;
import org.eclipse.dirigible.core.git.model.GitPushModel;
import org.eclipse.dirigible.core.git.model.GitResetModel;
import org.eclipse.dirigible.core.git.model.GitShareModel;
import org.eclipse.dirigible.core.git.model.GitUpdateDependenciesModel;
import org.eclipse.dirigible.core.git.project.ProjectOriginUrls;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.service.PublisherCoreService;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.json.ProjectDescriptor;
import org.eclipse.dirigible.core.workspace.json.WorkspaceDescriptor;
import org.eclipse.dirigible.core.workspace.json.WorkspaceGitHelper;
import org.eclipse.dirigible.core.workspace.json.WorkspaceJsonHelper;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Constants;

/**
 * Processing the Git Service incoming requests.
 */
public class GitProcessor {

	/** The Constant DOT_GIT. */
	private static final String DOT_GIT = ".git";

	/** The workspaces core service. */
	private WorkspacesCoreService workspacesCoreService = new WorkspacesCoreService();

	/** The publisher core service. */
	private PublisherCoreService publisherCoreService = new PublisherCoreService();

	/** The clone command. */
	private CloneCommand cloneCommand = new CloneCommand();

	/** The pull command. */
	private PullCommand pullCommand = new PullCommand();

	/** The push command. */
	private PushCommand pushCommand = new PushCommand();

	/** The reset command. */
	private ResetCommand resetCommand = new ResetCommand();

	/** The share command. */
	private ShareCommand shareCommand = new ShareCommand();
	
	/** The checkout command. */
	private CheckoutCommand checkoutCommand = new CheckoutCommand();
	
	/** The commit command. */
	private CommitCommand commitCommand = new CommitCommand();

	/** The update dependencies command. */
	private UpdateDependenciesCommand updateDependenciesCommand = new UpdateDependenciesCommand();

	/**
	 * Clone.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException the git connector exception
	 */
	public void clone(String workspace, GitCloneModel model) throws GitConnectorException {
		cloneCommand.execute(getWorkspace(workspace), model);
	}

	/**
	 * Pull.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException the git connector exception
	 */
	public void pull(String workspace, GitPullModel model) throws GitConnectorException {
		pullCommand.execute(getWorkspace(workspace), model);
	}

	/**
	 * Push.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException the git connector exception
	 */
	public void push(String workspace, GitPushModel model) throws GitConnectorException {
		pushCommand.execute(getWorkspace(workspace), model);
	}

	/**
	 * Reset.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException the git connector exception
	 */
	public void reset(String workspace, GitResetModel model) throws GitConnectorException {
		resetCommand.execute(workspace, model.getProjects());
	}

	/**
	 * Delete.
	 *
	 * @param workspace the workspace
	 * @param repositoryName the repositoryName
	 * @param unpublish whether to unpublish the project(s)
	 * @throws GitConnectorException in case of exception
	 * @throws PublisherException in case of exception
	 */
	public void delete(String workspace, String repositoryName, boolean unpublish) throws GitConnectorException, PublisherException {
		try {
			File gitRepository = getGitRepository(workspace, repositoryName);
			List<String> projects = GitFileUtils.getGitRepositoryProjects(workspace, repositoryName);

			IWorkspace workspaceApi = getWorkspace(workspace);
			IRepository repository = workspaceApi.getRepository();
			IProject[] workspaceProjects = getProjects(workspaceApi, projects);
			for (IProject next : workspaceProjects) {
				if (next.exists()) {					
					next.delete();
				} else if (repository.isLinkedPath(next.getPath())) {
					repository.deleteLinkedPath(next.getPath());
				}
				if (unpublish) {
					publisherCoreService.createUnpublishRequest(workspace, next.getName());
				}
			}

			FileUtils.deleteDirectory(gitRepository);
		} catch (IOException e) {
			throw new GitConnectorException("Unable to delete Git repository [" + repositoryName + "]", e);
		}
	}

	/**
	 * Share.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException the git connector exception
	 */
	public void share(String workspace, GitShareModel model) throws GitConnectorException {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject project = getProject(workspaceApi, model.getProject());
		shareCommand.execute(workspaceApi, project, model);
	}
	
	/**
	 * Checkout.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException the git connector exception
	 */
	public void checkout(String workspace, GitCheckoutModel model) throws GitConnectorException {
		checkoutCommand.execute(getWorkspace(workspace), model);
	}
	
	/**
	 * Commit.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException the git connector exception
	 */
	public void commit(String workspace, GitPushModel model) throws GitConnectorException {
		commitCommand.execute(getWorkspace(workspace), model);
	}

	/**
	 * Update dependencies.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException the git connector exception
	 */
	public void updateDependencies(String workspace, GitUpdateDependenciesModel model) throws GitConnectorException {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject[] projects = getProjects(workspaceApi, model.getProjects());
		updateDependenciesCommand.execute(workspaceApi, projects, model);
	}

	/**
	 * Gets the workspace.
	 *
	 * @param workspace the workspace
	 * @return the workspace
	 */
	public IWorkspace getWorkspace(String workspace) {
		return workspacesCoreService.getWorkspace(workspace);
	}

	/**
	 * Gets the project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the project
	 */
	public IProject getProject(String workspace, String project) {
		return getWorkspace(workspace).getProject(project);
	}

	/**
	 * Gets the project.
	 *
	 * @param workspaceApi the workspace api
	 * @param project the project
	 * @return the project
	 */
	public IProject getProject(IWorkspace workspaceApi, String project) {
		return workspaceApi.getProject(project);
	}

	/**
	 * Gets the projects.
	 *
	 * @param workspace the workspace
	 * @param projectsNames the projects names
	 * @return the projects
	 */
	public IProject[] getProjects(IWorkspace workspace, List<String> projectsNames) {
		List<IProject> projects = new ArrayList<IProject>();
		for (String next : projectsNames) {
			projects.add(workspace.getProject(next));
		}
		return projects.toArray(new IProject[] {});
	}

	/**
	 * Return the list of the Git branches - local ones
	 *  
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the branches
	 * @throws GitConnectorException in case of an error
	 */
	public GitProjectLocalBranches getLocalBranches(String workspace, String project) throws GitConnectorException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		if (gitConnector != null) {
			GitProjectLocalBranches branches = new GitProjectLocalBranches();
			branches.setLocal(gitConnector.getLocalBranches());
			return branches;
		}
		return null;
	}
	
	/**
	 * Return the list of the Git branches - remote ones
	 *  
	 * @param workspace the workspace
	 * @param project the project
	 * @return the branches
	 * @throws GitConnectorException in case of an error
	 */
	public GitProjectRemoteBranches getRemoteBranches(String workspace, String project) throws GitConnectorException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		if (gitConnector != null) {
			GitProjectRemoteBranches branches = new GitProjectRemoteBranches();
			branches.setRemote(gitConnector.getRemoteBranches());
			return branches;
		}
		return null;
	}
	
	/**
	 * Exists workspace.
	 *
	 * @param workspace
	 *            the workspace
	 * @return true, if successful
	 */
	public boolean existsWorkspace(String workspace) {
		IWorkspace workspaceObject = workspacesCoreService.getWorkspace(workspace);
		return workspaceObject.exists();
	}

	/**
	 * Render workspace tree.
	 *
	 * @param workspace
	 *            the workspace
	 * @return the workspace descriptor
	 */
	public WorkspaceDescriptor renderWorkspaceTree(IWorkspace workspace) {
		return WorkspaceJsonHelper.describeWorkspaceProjects(workspace,
				IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(), "");
	}

	/**
	 * Render git repositories.
	 *
	 * @param user the user
	 * @param workspace the workspace
	 * @return the list
	 */
	public List<ProjectDescriptor> renderGitRepositories(String user, String workspace) {
		List<ProjectDescriptor> gitRepositories = new ArrayList<ProjectDescriptor>();
		File gitDirectory = GitFileUtils.getGitDirectory(user, workspace);
		if (gitDirectory != null) {
			for (File next : gitDirectory.listFiles()) {
				if (!next.isFile()) {
					gitRepositories.add(WorkspaceGitHelper.describeProject(next));
				}
			}
		}
		IWorkspace workspaceObject = getWorkspace(workspace);
		IRepository repository = workspaceObject.getRepository();
		for (IProject next : workspaceObject.getProjects()) {
			if (!repository.isLinkedPath(next.getPath())) {				
				ProjectDescriptor project = new ProjectDescriptor();
				project.setGit(false);
				project.setName(next.getName());
				project.setPath(next.getPath());
				gitRepositories.add(project);
			}
		}
		return gitRepositories;
	}

	/**
	 * Render workspace project.
	 *
	 * @param workspace the workspace
	 * @param projectName the project name
	 * @return the project descriptor
	 */
	public ProjectDescriptor renderWorkspaceProject(IWorkspace workspace, String projectName) {
		IProject project = workspace.getProject(projectName);
		return WorkspaceJsonHelper.describeProject(workspace.getName(), project, IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(), "");
	}

	/**
	 * Get the unstaged files for project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the list of files
	 * @throws GitConnectorException in case of an error
	 */
	public GitProjectChangedFiles getUnstagedFiles(String workspace, String project) throws GitConnectorException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		if (gitConnector != null) {
			GitProjectChangedFiles gitProjectChangedFiles = new GitProjectChangedFiles();
			gitProjectChangedFiles.setFiles(gitConnector.getUnstagedChanges());
			return gitProjectChangedFiles;
		}
		return null;
	}

	/**
	 * Get the staged files for project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the list of files
	 * @throws GitConnectorException in case of an error
	 */
	public GitProjectChangedFiles getStagedFiles(String workspace, String project) throws GitConnectorException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		if (gitConnector != null) {
			GitProjectChangedFiles gitProjectChangedFiles = new GitProjectChangedFiles();
			gitProjectChangedFiles.setFiles(gitConnector.getStagedChanges());
			return gitProjectChangedFiles;
		}
		return null;
	}
	
	/**
	 * Add file(s) to index.
	 *
	 * @param workspace the workspace
	 * @param repositoryName the name of the git repository
	 * @param paths the paths
	 * @throws GitConnectorException in case of an error
	 */
	public void addFileToIndex(String workspace, String repositoryName, String paths) throws GitConnectorException {
		try {
			IGitConnector gitConnector = getGitConnector(workspace, repositoryName);
			if (gitConnector != null) {
				List<File> projects = GitFileUtils.getGitRepositoryProjectsFiles(workspace, repositoryName);
	
				String[] files = paths.split(",");
				for (String file : files) {
					File projectFile = null;
					String projectLocation = null;
					for (File next : projects) {
						projectLocation = extractProjectLocation(next);
						if (file.startsWith(projectLocation)) {
							projectFile = next;
							break;
						}
					}
					if (projectFile == null) {
						throw new IllegalArgumentException("Project not found in git repository [" + repositoryName + "] for file [" + file + "]");
					}
					String fileLocation = projectFile.getPath() + File.separator + file.substring(projectLocation.length());
					File canonicalFile = new File(fileLocation).getCanonicalFile();
					if (canonicalFile.exists()) {
						gitConnector.add(file);
					} else {
						gitConnector.addDeleted(file);
					}
				}
			}
		} catch (Exception e) {
			throw new GitConnectorException(e);
		}
	}
	
	/**
	 * Revert file(s) to index.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param paths the paths
	 * @throws GitConnectorException in case of an error
	 */
	public void revertToHeadRevision(String workspace, String project, String paths) throws GitConnectorException {
		try {
			IGitConnector gitConnector = getGitConnector(workspace, project);
			if (gitConnector != null) {
				String[] files = paths.split(",");
				for (String file : files) {
					gitConnector.revert(file);
				}
			}
		} catch (Exception e) {
			throw new GitConnectorException(e);
		}
	}

	/**
	 * Remote origin URLs.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return Origin URLs info
	 * @throws GitConnectorException in case of an error
	 */
	public ProjectOriginUrls getOriginUrls(String workspace, String project) throws GitConnectorException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		if (gitConnector != null) {
			return gitConnector.getOriginUrls();
		}
		return null;
	}

	/**
	 * Update remote origin fetch URL.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param url the new fetch URL
	 * @throws GitConnectorException Git Connector Exception
	 * @throws GitAPIException Git API Exception
	 * @throws URISyntaxException URL with wrong format provided
	 */
	public void setFetchUrl(String workspace, String project, String url) throws GitConnectorException, GitAPIException, URISyntaxException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		if (gitConnector != null) {
			gitConnector.setFetchUrl(url);
		}
	}

	/**
	 * Update remote origin push URL.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param url the new fetch URL
	 * @throws GitConnectorException Git Connector Exception
	 * @throws GitAPIException Git API Exception
	 * @throws URISyntaxException URL with wrong format provided
	 */
	public void setPushUrl(String workspace, String project, String url) throws GitConnectorException, GitAPIException, URISyntaxException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		if (gitConnector != null) {
			gitConnector.setPushUrl(url);
		}
	}

	/**
	 * Remove file(s) from index.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param paths the paths
	 * @throws GitConnectorException in case of an error
	 */
	public void removeFileFromIndex(String workspace, String project, String paths) throws GitConnectorException {
		try {
			IGitConnector gitConnector = getGitConnector(workspace, project);
			if (gitConnector != null) {
				String[] files = paths.split(",");
				for (String file : files) {
					gitConnector.remove(file);
				}
			}
		} catch (Exception e) {
			throw new GitConnectorException(e);
		}
	}
	
	/**
	 * Get file diff.
	 *
	 * @param workspace the workspace
	 * @param repositoryName the project
	 * @param path the path
	 * @return the diff
	 * @throws GitConnectorException in case of an error
	 */
	public GitDiffModel getFileDiff(String workspace, String repositoryName, String path) throws GitConnectorException {
		try {
			IGitConnector gitConnector = getGitConnector(workspace, repositoryName);
			if (gitConnector != null) {
				File project = getProjectFile(workspace, repositoryName, path);
				String projectLocation = extractProjectLocation(project);
				String filePath = null;
				if (projectLocation.length() > 0 && path.startsWith(projectLocation)) {
					filePath = path.substring(projectLocation.length());
				} else {
					File gitRepo = GitFileUtils.getGitDirectoryByRepositoryName(workspace, repositoryName);
					boolean isRootProject = gitRepo.getCanonicalPath().equals(project.getCanonicalPath());
					if (!isRootProject || path.startsWith(project.getName())) {
						filePath = path.substring(path.indexOf("/") + 1);
					} else {
						filePath = path;
					}
				}
				IFile file = getProject(workspace, project.getName()).getFile(filePath);
				String original = getOriginalFileContent(project, filePath, gitConnector);
				String modified = getModifiedFileContent(file);
				GitDiffModel diffModel = new GitDiffModel(original, modified);
				return diffModel;
			}
		} catch (Exception e) {
			throw new GitConnectorException(e);
		}
		return null;
	}

	/**
	 * Gets the project file.
	 *
	 * @param workspace the workspace
	 * @param repositoryName the repository name
	 * @param path the path
	 * @return the project file
	 */
	private File getProjectFile(String workspace, String repositoryName, String path) {
		String projectName = null;
		if (path.indexOf("/") > 0) {
			projectName = path.substring(0, path.indexOf("/"));
		} else {
			// It's "project.json" file in the root Git folder
			projectName = repositoryName;
		}
		List<File> projects = GitFileUtils.getGitRepositoryProjectsFiles(workspace, repositoryName);
		File project = null;
		for (File next : projects) {
			if (next.exists() && next.getName().equals(projectName)) {
				project = next;
				break;
			}
		}
		// Fallback for Project search by full path (e.g. while in Git perspective)
		if (project == null) {
			for (File next : projects) {
				if (path.startsWith(extractProjectLocation(next))) {
					project = next;
					break;
				}
			}
		}
		return project;
	}

	/**
	 * Extract project location.
	 *
	 * @param project the project
	 * @return the string
	 */
	private String extractProjectLocation(File project) {
		StringBuilder projectLocation = new StringBuilder();
		String projectPath = FilenameUtils.normalize(project.getPath(), true);
		if (projectPath.indexOf(DOT_GIT) > 0) {
			String path = projectPath.substring(projectPath.indexOf(DOT_GIT) + DOT_GIT.length() + IRepository.SEPARATOR.length());
			String[] tokens = path.split(IRepository.SEPARATOR);
			for (int i = 3 ; i < tokens.length; i ++) {
				projectLocation.append(tokens[i]).append(IRepository.SEPARATOR);
			}
		}
		return projectLocation.toString();
	}

	/**
	 * Gets the original file content.
	 *
	 * @param project the project
	 * @param filePath the file path
	 * @param gitConnector the git connector
	 * @return the original file content
	 * @throws GitConnectorException the git connector exception
	 */
	private String getOriginalFileContent(File project, String filePath, IGitConnector gitConnector) throws GitConnectorException {
		String original = null;
		if (project != null) {
			String projectLocation = extractProjectLocation(project);
			original = gitConnector.getFileContent(projectLocation + filePath, Constants.HEAD);
		}
		return original;
	}

	/**
	 * Gets the modified file content.
	 *
	 * @param file the file
	 * @return the modified file content
	 */
	private String getModifiedFileContent(IFile file) {
		String modified = null;
		if (file.exists()) {
			modified = new String(file.getContent());
		}
		return modified;
	}
	
	/**
	 * Get file history.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @return the history
	 * @throws GitConnectorException in case of an error
	 */
	public List<GitCommitInfo> getHistory(String workspace, String project, String path) throws GitConnectorException {
		try {
			IGitConnector gitConnector = getGitConnector(workspace, project);
			if (gitConnector != null) {
				List<GitCommitInfo> history = gitConnector.getHistory(path);
				return history;
			}
		} catch (Exception e) {
			throw new GitConnectorException(e);
		}
		return null;
	}

	/**
	 * Import projects.
	 *
	 * @param workspace the workspace
	 * @param repository the repository
	 * @throws GitConnectorException the git connector exception
	 */
	public void importProjects(String workspace, String repository) throws GitConnectorException {
		try {
			GitFileUtils.importProject(workspace, repository);
		} catch (Exception e) {
			throw new GitConnectorException(e);
		}
	}

	/**
	 * Gets the git connector.
	 *
	 * @param workspace the workspace
	 * @param repositoryName the repository name
	 * @return the git connector
	 * @throws GitConnectorException the git connector exception
	 */
	private IGitConnector getGitConnector(String workspace, String repositoryName) throws GitConnectorException {
		try {
			IWorkspace workspaceObject = getWorkspace(workspace);
			if (workspaceObject.getRepository() instanceof FileSystemRepository) {
				File gitDirectoryFile = getGitRepository(workspace, repositoryName);
				if (gitDirectoryFile != null) {
						String gitDirectory	= gitDirectoryFile.getCanonicalPath();
					if (Paths.get(Paths.get(gitDirectory).toString(), DOT_GIT).toFile().exists()) {
						IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory);
						return gitConnector;
					} else {
						throw new GitConnectorException("Not a Git project directory");
					}
				} else {
					return null;
				}
			} else {
				throw new GitConnectorException("Not a file based repository used, hence no git support");
			}
		} catch (RepositoryWriteException | IOException e) {
			throw new GitConnectorException(e);
		}
	}

	/**
	 * Gets the git repository.
	 *
	 * @param workspace the workspace
	 * @param repositoryName the repository name
	 * @return the git repository
	 * @throws GitConnectorException the git connector exception
	 */
	private File getGitRepository(String workspace, String repositoryName) throws GitConnectorException {
		if (getWorkspace(workspace).getRepository() instanceof FileSystemRepository) {			
			return GitFileUtils.getGitDirectoryByRepositoryName(workspace, repositoryName);
		}
		throw new GitConnectorException("Not a file based repository used, hence no git support");
	}

	/**
	 * Creates the local branch.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param branch the branch
	 * @throws GitConnectorException the git connector exception
	 * @throws RefAlreadyExistsException the ref already exists exception
	 * @throws RefNotFoundException the ref not found exception
	 * @throws InvalidRefNameException the invalid ref name exception
	 * @throws GitAPIException the git API exception
	 */
	public void createLocalBranch(String workspace, String project, String branch) throws GitConnectorException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		if (gitConnector != null) {
			gitConnector.createBranch(branch, "HEAD");
		}
	}
	
	/**
	 * Delete the local branch.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param branch the branch
	 * @throws GitConnectorException the git connector exception
	 * @throws RefAlreadyExistsException the ref already exists exception
	 * @throws RefNotFoundException the ref not found exception
	 * @throws InvalidRefNameException the invalid ref name exception
	 * @throws GitAPIException the git API exception
	 */
	public void deleteLocalBranch(String workspace, String project, String branch) throws GitConnectorException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		if (gitConnector != null) {
			gitConnector.deleteBranch(branch);
		}
	}
	
	/**
	 * Rename the local branch.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param oldName the oldName
	 * @param newName the newName
	 * @throws GitConnectorException the git connector exception
	 * @throws RefAlreadyExistsException the ref already exists exception
	 * @throws RefNotFoundException the ref not found exception
	 * @throws InvalidRefNameException the invalid ref name exception
	 * @throws GitAPIException the git API exception
	 */
	public void renameLocalBranch(String workspace, String project, String oldName, String newName) throws GitConnectorException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		if (gitConnector != null) {
			gitConnector.renameBranch(oldName, newName);
		}
	}

	/**
	 * Creates the remote branch.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param branch the branch
	 * @param model the model
	 * @throws GitConnectorException the git connector exception
	 * @throws RefAlreadyExistsException the ref already exists exception
	 * @throws RefNotFoundException the ref not found exception
	 * @throws InvalidRefNameException the invalid ref name exception
	 * @throws GitAPIException the git API exception
	 */
	public void createRemoteBranch(String workspace, String project, String branch, BaseGitModel model) throws GitConnectorException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		if (gitConnector != null) {
			gitConnector.createRemoteBranch(branch, "HEAD", model.getUsername(), model.getPassword());
		}
	}

	/**
	 * Delete remote branch.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param branch the branch
	 * @param model the model
	 * @throws GitConnectorException the git connector exception
	 * @throws RefAlreadyExistsException the ref already exists exception
	 * @throws RefNotFoundException the ref not found exception
	 * @throws InvalidRefNameException the invalid ref name exception
	 * @throws GitAPIException the git API exception
	 */
	public void deleteRemoteBranch(String workspace, String project, String branch, BaseGitModel model) throws GitConnectorException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		if (gitConnector != null) {
			gitConnector.deleteRemoteBranch(branch, model.getUsername(), model.getPassword());
		}
	}


}
