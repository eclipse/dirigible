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
import org.eclipse.jgit.lib.Constants;

/**
 * Processing the Git Service incoming requests.
 */
public class GitProcessor {

	private static final String DOT_GIT = ".git";

	private WorkspacesCoreService workspacesCoreService = new WorkspacesCoreService();

	private PublisherCoreService publisherCoreService = new PublisherCoreService();

	private CloneCommand cloneCommand = new CloneCommand();

	private PullCommand pullCommand = new PullCommand();

	private PushCommand pushCommand = new PushCommand();

	private ResetCommand resetCommand = new ResetCommand();

	private ShareCommand shareCommand = new ShareCommand();
	
	private CheckoutCommand checkoutCommand = new CheckoutCommand();
	
	private CommitCommand commitCommand = new CommitCommand();

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
	 * @throws GitConnectorException 
	 */
	public void pull(String workspace, GitPullModel model) throws GitConnectorException {
		pullCommand.execute(getWorkspace(workspace), model);
	}

	/**
	 * Push.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException 
	 */
	public void push(String workspace, GitPushModel model) throws GitConnectorException {
		pushCommand.execute(getWorkspace(workspace), model);
	}

	/**
	 * Reset.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException 
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
					publisherCoreService.createUnpublishRequest(workspace, next.getName(), IRepositoryStructure.PATH_REGISTRY_PUBLIC);
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
	 * @throws GitConnectorException 
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
	 * @throws GitConnectorException 
	 */
	public void checkout(String workspace, GitCheckoutModel model) throws GitConnectorException {
		checkoutCommand.execute(getWorkspace(workspace), model);
	}
	
	/**
	 * Commit.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException 
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
	 * @param workspace the workspace
	 * @param project the project
	 * @throws GitConnectorException in case of an error
	 * @return the branches
	 */
	public GitProjectLocalBranches getLocalBranches(String workspace, String project) throws GitConnectorException {
		GitProjectLocalBranches branches = new GitProjectLocalBranches();
		IGitConnector gitConnector = getGitConnector(workspace, project);
		branches.setLocal(gitConnector.getLocalBranches());
		return branches;
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
		GitProjectRemoteBranches branches = new GitProjectRemoteBranches();
		IGitConnector gitConnector = getGitConnector(workspace, project);
		branches.setRemote(gitConnector.getRemoteBranches());
		return branches;
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

	public ProjectDescriptor renderWorkspaceProject(IWorkspace workspace, String projectName) {
		IProject project = workspace.getProject(projectName);
		return WorkspaceJsonHelper.describeProject(project, IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + UserFacade.getName(), "");
	}

	/**
	 * Get the unstaged files for project
	 * 
	 * @param workspace the workspace
	 * @param project the project
	 * @return the list of files
	 * @throws GitConnectorException in case of an error
	 */
	public GitProjectChangedFiles getUnstagedFiles(String workspace, String project) throws GitConnectorException {
		GitProjectChangedFiles gitProjectChangedFiles = new GitProjectChangedFiles();
		IGitConnector gitConnector = getGitConnector(workspace, project);
		gitProjectChangedFiles.setFiles(gitConnector.getUnstagedChanges());
		return gitProjectChangedFiles;
	}

	/**
	 * Get the staged files for project
	 * 
	 * @param workspace the workspace
	 * @param project the project
	 * @return the list of files
	 * @throws GitConnectorException in case of an error
	 */
	public GitProjectChangedFiles getStagedFiles(String workspace, String project) throws GitConnectorException {
		GitProjectChangedFiles gitProjectChangedFiles = new GitProjectChangedFiles();
		IGitConnector gitConnector = getGitConnector(workspace, project);
		gitProjectChangedFiles.setFiles(gitConnector.getStagedChanges());
		return gitProjectChangedFiles;
	}
	
	/**
	 * Add file(s) to index
	 * 
	 * @param workspace the workspace
	 * @param repositoryName the name of the git repository
	 * @param paths the paths
	 * @throws GitConnectorException in case of an error
	 */
	public void addFileToIndex(String workspace, String repositoryName, String paths) throws GitConnectorException {
		try {
			IGitConnector gitConnector = getGitConnector(workspace, repositoryName);
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
		} catch (Exception e) {
			throw new GitConnectorException(e);
		}
	}
	
	/**
	 * Revert file(s) to index
	 * 
	 * @param workspace the workspace
	 * @param project the project
	 * @param paths the paths
	 * @throws GitConnectorException in case of an error
	 */
	public void revertToHeadRevision(String workspace, String project, String paths) throws GitConnectorException {
		try {
			IGitConnector gitConnector = getGitConnector(workspace, project);
			String[] files = paths.split(",");
			for (String file : files) {
				gitConnector.revert(file);
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
	 * @throws GitConnectorException in case of an error
	 * @return Origin URLs info
	 *
	 */
	public ProjectOriginUrls getOriginUrls(String workspace, String project) throws GitConnectorException {
		IGitConnector gitConnector = getGitConnector(workspace, project);
		return gitConnector.getOriginUrls();
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
		gitConnector.setFetchUrl(url);
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
		gitConnector.setPushUrl(url);
	}

	/**
	 * Remove file(s) from index
	 * 
	 * @param workspace the workspace
	 * @param project the project
	 * @param paths the paths
	 * @throws GitConnectorException in case of an error
	 */
	public void removeFileFromIndex(String workspace, String project, String paths) throws GitConnectorException {
		try {
			IGitConnector gitConnector = getGitConnector(workspace, project);
			String[] files = paths.split(",");
			for (String file : files) {
				gitConnector.remove(file);
			}
		} catch (Exception e) {
			throw new GitConnectorException(e);
		}
	}
	
	/**
	 * Get file diff
	 * @param workspace the workspace
	 * @param repositoryName the project
	 * @param path the path
	 * @return the diff
	 * @throws GitConnectorException in case of an error
	 */
	public GitDiffModel getFileDiff(String workspace, String repositoryName, String path) throws GitConnectorException {
		try {
			IGitConnector gitConnector = getGitConnector(workspace, repositoryName);
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
		} catch (Exception e) {
			throw new GitConnectorException(e);
		}
	}

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

	private String extractProjectLocation(File project) {
		StringBuilder projectLocation = new StringBuilder();
		String projectPath = project.getPath();
		if (projectPath.indexOf(DOT_GIT) > 0) {
			String path = projectPath.substring(projectPath.indexOf(DOT_GIT) + DOT_GIT.length() + IRepository.SEPARATOR.length());
			String[] tokens = path.split(IRepository.SEPARATOR);
			for (int i = 3 ; i < tokens.length; i ++) {
				projectLocation.append(tokens[i]).append(IRepository.SEPARATOR);
			}
		}
		return projectLocation.toString();
	}

	private String getOriginalFileContent(File project, String filePath, IGitConnector gitConnector) throws GitConnectorException {
		String original = null;
		if (project != null) {
			String projectLocation = extractProjectLocation(project);
			original = gitConnector.getFileContent(projectLocation + filePath, Constants.HEAD);
		}
		return original;
	}

	private String getModifiedFileContent(IFile file) {
		String modified = null;
		if (file.exists()) {
			modified = new String(file.getContent());
		}
		return modified;
	}
	/**
	 * Get file history
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @return the history
	 * @throws GitConnectorException in case of an error
	 */
	public List<GitCommitInfo> getHistory(String workspace, String project, String path) throws GitConnectorException {
		try {
			IGitConnector gitConnector = getGitConnector(workspace, project);
			List<GitCommitInfo> history = gitConnector.getHistory(path);
			return history;
		} catch (Exception e) {
			throw new GitConnectorException(e);
		}
	}

	public void importProjects(String workspace, String repository) throws GitConnectorException {
		try {
			GitFileUtils.importProject(workspace, repository);
		} catch (Exception e) {
			throw new GitConnectorException(e);
		}
	}

	private IGitConnector getGitConnector(String workspace, String repositoryName) throws GitConnectorException {
		try {
			IWorkspace workspaceObject = getWorkspace(workspace);
			if (workspaceObject.getRepository() instanceof FileSystemRepository) {
				String gitDirectory = getGitRepository(workspace, repositoryName).getCanonicalPath();
				if (Paths.get(Paths.get(gitDirectory).toString(), DOT_GIT).toFile().exists()) {
					IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory);
					return gitConnector;
				} else {
					throw new GitConnectorException("Not a Git project directory");
				}
			} else {
				throw new GitConnectorException("Not a file based repository used, hence no git support");
			}
		} catch (RepositoryWriteException | IOException e) {
			throw new GitConnectorException(e);
		}
	}

	private File getGitRepository(String workspace, String repositoryName) throws GitConnectorException {
		if (getWorkspace(workspace).getRepository() instanceof FileSystemRepository) {			
			return GitFileUtils.getGitDirectoryByRepositoryName(workspace, repositoryName);
		}
		throw new GitConnectorException("Not a file based repository used, hence no git support");
	}


}
