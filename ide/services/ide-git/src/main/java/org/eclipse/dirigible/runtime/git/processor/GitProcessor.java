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
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
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
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
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
import org.eclipse.dirigible.runtime.git.model.BaseGitModel;
import org.eclipse.dirigible.runtime.git.model.GitCheckoutModel;
import org.eclipse.dirigible.runtime.git.model.GitCloneModel;
import org.eclipse.dirigible.runtime.git.model.GitDiffModel;
import org.eclipse.dirigible.runtime.git.model.GitProjectChangedFiles;
import org.eclipse.dirigible.runtime.git.model.GitProjectLocalBranches;
import org.eclipse.dirigible.runtime.git.model.GitProjectRemoteBranches;
import org.eclipse.dirigible.runtime.git.model.GitPullModel;
import org.eclipse.dirigible.runtime.git.model.GitPushModel;
import org.eclipse.dirigible.runtime.git.model.GitResetModel;
import org.eclipse.dirigible.runtime.git.model.GitShareModel;
import org.eclipse.dirigible.runtime.git.model.GitUpdateDependenciesModel;
import org.eclipse.jgit.lib.Constants;

/**
 * Processing the Git Service incoming requests.
 */
public class GitProcessor {

	private static final String DOT_GIT = ".git";

	private WorkspacesCoreService workspacesCoreService = new WorkspacesCoreService();

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
		cloneCommand.execute(model.getRepository(), model.getBranch(), model.getUsername(), getPassword(model), workspace, model.isPublish(), model.getProjectName());
	}

	/**
	 * Pull.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException 
	 */
	public void pull(String workspace, GitPullModel model) throws GitConnectorException {
		IWorkspace workspaceApi = getWorkspace(workspace);
		pullCommand.execute(workspaceApi, model.getProjects(), model.getUsername(), getPassword(model), model.getBranch(), model.isPublish());
	}

	/**
	 * Push.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException 
	 */
	public void push(String workspace, GitPushModel model) throws GitConnectorException {
		IWorkspace workspaceApi = getWorkspace(workspace);
		pushCommand.execute(workspaceApi, model.getProjects(), model.getCommitMessage(), model.getUsername(), getPassword(model), model.getEmail(), model.getBranch(), model.isAutoAdd(), model.isAutoCommit());
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
	 * Reset.
	 *
	 * @param workspace the workspace
	 * @param repositoryName the repositoryName
	 * @throws GitConnectorException in case of exception
	 */
	public void delete(String workspace, String repositoryName) throws GitConnectorException {
		try {
			File gitRepository = getGitRepository(workspace, repositoryName);
			List<String> projects = GitFileUtils.getGitRepositoryProjects(workspace, repositoryName);

			IWorkspace workspaceApi = getWorkspace(workspace);
			IProject[] workspaceProjects = getProjects(workspaceApi, projects);
			for (IProject next : workspaceProjects) {
				if (next.exists()) {					
					next.delete();
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
		shareCommand.execute(workspaceApi, project, model.getRepository(), model.getBranch(), model.getCommitMessage(), model.getUsername(),
				getPassword(model), model.getEmail());
	}
	
	/**
	 * Checkout.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException 
	 */
	public void checkout(String workspace, GitCheckoutModel model) throws GitConnectorException {
		IWorkspace workspaceApi = getWorkspace(workspace);
		checkoutCommand.execute(workspaceApi, model.getProject(), model.getUsername(), getPassword(model), model.getBranch(), model.isPublish());
	}
	
	/**
	 * Commit.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException 
	 */
	public void commit(String workspace, GitPushModel model) throws GitConnectorException {
		IWorkspace workspaceApi = getWorkspace(workspace);
		commitCommand.execute(workspaceApi, model.getProjects(), model.getCommitMessage(), model.getUsername(), getPassword(model), model.getEmail(), model.getBranch(), model.isAutoAdd());
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
		updateDependenciesCommand.execute(workspaceApi, projects, model.getUsername(), getPassword(model), model.isPublish());
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
	 * Gets the password.
	 *
	 * @param model the model
	 * @return the password
	 */
	private String getPassword(BaseGitModel model) {
		if (model.getPassword() == null) {
			return null;
		}
		return new String(Base64.getDecoder().decode(model.getPassword().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
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
	 * @param project the project
	 * @param paths the paths
	 * @throws GitConnectorException in case of an error
	 */
	public void addFileToIndex(String workspace, String project, String paths) throws GitConnectorException {
		try {
			IGitConnector gitConnector = getGitConnector(workspace, project);
			String[] files = paths.split(",");
			for (String file : files) {
				IProject projectCollection = getWorkspace(workspace).getProject(project);
				File canonicalFile = WorkspaceDescriptor.getCanonicalFilePerProjectPath(projectCollection.getRepository(), projectCollection.getParent().getPath() + IRepositoryStructure.SEPARATOR + file);
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
			String original = gitConnector.getFileContent(path, Constants.HEAD);
			String project = path.substring(0, path.indexOf("/"));
			String filePath = path.substring(path.indexOf("/") + 1);
			IFile file = getProject(workspace, project).getFile(filePath);
			String modified = null;
			if (file.exists()) {
				modified = new String(file.getContent());
			}
			GitDiffModel diffModel = new GitDiffModel(original, modified);
			return diffModel;
		} catch (Exception e) {
			throw new GitConnectorException(e);
		}
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
			File repositoryName = GitFileUtils.getGitDirectoryByRepositoryName(workspace, repository);
			List<String> projects = GitFileUtils.getGitRepositoryProjects(workspace, repository);
			IWorkspace workspaceObject = getWorkspace(workspace);
			for (String project : projects) {
				if (!workspaceObject.getProject(project).exists()) {
					String targetPath = repositoryName.getCanonicalFile() + File.separator + project;
					workspaceObject.linkProject(project, targetPath);
				}
			}
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
