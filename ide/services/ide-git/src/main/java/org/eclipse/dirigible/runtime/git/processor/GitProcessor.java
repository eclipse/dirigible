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
package org.eclipse.dirigible.runtime.git.processor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.security.UserFacade;
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
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.json.WorkspaceDescriptor;
import org.eclipse.dirigible.core.workspace.json.WorkspaceJsonHelper;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.dirigible.repository.local.LocalWorkspaceMapper;
import org.eclipse.dirigible.runtime.git.model.BaseGitModel;
import org.eclipse.dirigible.runtime.git.model.GitCheckoutModel;
import org.eclipse.dirigible.runtime.git.model.GitCloneModel;
import org.eclipse.dirigible.runtime.git.model.GitProjectLocalBranches;
import org.eclipse.dirigible.runtime.git.model.GitProjectRemoteBranches;
import org.eclipse.dirigible.runtime.git.model.GitPullModel;
import org.eclipse.dirigible.runtime.git.model.GitPushModel;
import org.eclipse.dirigible.runtime.git.model.GitResetModel;
import org.eclipse.dirigible.runtime.git.model.GitShareModel;
import org.eclipse.dirigible.runtime.git.model.GitUpdateDependenciesModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing the Git Service incoming requests.
 */
public class GitProcessor {

	private static final String DOT_GIT = ".git";

	private static final Logger logger = LoggerFactory.getLogger(GitProcessor.class);

	@Inject
	private WorkspacesCoreService workspacesCoreService;

	@Inject
	private CloneCommand cloneCommand;

	@Inject
	private PullCommand pullCommand;

	@Inject
	private PushCommand pushCommand;

	@Inject
	private ResetCommand resetCommand;

	@Inject
	private ShareCommand shareCommand;
	
	@Inject
	private CheckoutCommand checkoutCommand;
	
	@Inject
	private CommitCommand commitCommand;

	@Inject
	private UpdateDependenciesCommand updateDependenciesCommand;

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
	 */
	public void pull(String workspace, GitPullModel model) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject[] projects = getProjects(workspaceApi, model.getProjects());
		pullCommand.execute(workspaceApi, projects, model.getUsername(), getPassword(model), model.getBranch(), model.isPublish());
	}

	/**
	 * Push.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 */
	public void push(String workspace, GitPushModel model) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject[] projects = getProjects(workspaceApi, model.getProjects());
		pushCommand.execute(workspaceApi, projects, model.getCommitMessage(), model.getUsername(), getPassword(model), model.getEmail(), model.getBranch());
	}

	/**
	 * Reset.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 */
	public void reset(String workspace, GitResetModel model) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject[] projects = getProjects(workspaceApi, model.getProjects());
		resetCommand.execute(workspaceApi, projects, model.getUsername(), getPassword(model), model.getBranch());
	}

	/**
	 * Share.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 */
	public void share(String workspace, GitShareModel model) {
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
	 */
	public void checkout(String workspace, GitCheckoutModel model) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject project = getProject(workspaceApi, model.getProject());
		checkoutCommand.execute(workspaceApi, project, model.getUsername(), getPassword(model), model.getBranch(), model.isPublish());
	}
	
	/**
	 * Commit.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 */
	public void commit(String workspace, GitPushModel model) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject[] projects = getProjects(workspaceApi, model.getProjects());
		commitCommand.execute(workspaceApi, projects, model.getCommitMessage(), model.getUsername(), getPassword(model), model.getEmail(), model.getBranch());
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
		try {
			IProject projectCollection = getWorkspace(workspace).getProject(project);
			if (projectCollection.getRepository() instanceof FileSystemRepository) {
				String path = LocalWorkspaceMapper.getMappedName((FileSystemRepository) projectCollection.getRepository(), projectCollection.getPath());
				String gitDirectory = new File(path).getCanonicalPath();
				if (Paths.get(Paths.get(gitDirectory).getParent().toString(), DOT_GIT).toFile().exists()) {
					GitProjectLocalBranches branches = new GitProjectLocalBranches();
					IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory);
					branches.setLocal(gitConnector.getLocalBranches());
					return branches;
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
	
	/**
	 * Return the list of the Git branches - remote ones
	 *  
	 * @param workspace the workspace
	 * @param project the project
	 * @throws GitConnectorException in case of an error
	 * @return the branches
	 */
	public GitProjectRemoteBranches getRemoteBranches(String workspace, String project) throws GitConnectorException {
		try {
			IProject projectCollection = getWorkspace(workspace).getProject(project);
			if (projectCollection.getRepository() instanceof FileSystemRepository) {
				String path = LocalWorkspaceMapper.getMappedName((FileSystemRepository) projectCollection.getRepository(), projectCollection.getPath());
				String gitDirectory = new File(path).getCanonicalPath();
				if (Paths.get(Paths.get(gitDirectory).getParent().toString(), DOT_GIT).toFile().exists()) {
					GitProjectRemoteBranches branches = new GitProjectRemoteBranches();
					IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory);
					branches.setRemote(gitConnector.getRemoteBranches());
					return branches;
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
}
