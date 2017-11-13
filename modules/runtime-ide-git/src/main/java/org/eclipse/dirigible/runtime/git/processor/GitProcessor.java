/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.git.processor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.command.CloneCommand;
import org.eclipse.dirigible.core.git.command.PullCommand;
import org.eclipse.dirigible.core.git.command.PushCommand;
import org.eclipse.dirigible.core.git.command.ResetCommand;
import org.eclipse.dirigible.core.git.command.ShareCommand;
import org.eclipse.dirigible.core.git.command.UpdateDependenciesCommand;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.dirigible.runtime.git.model.BaseGitModel;
import org.eclipse.dirigible.runtime.git.model.GitCloneModel;
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
	private UpdateDependenciesCommand updateDependenciesCommand;

	/**
	 * Clone.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @throws GitConnectorException the git connector exception
	 */
	public void clone(String workspace, GitCloneModel model) throws GitConnectorException {
		cloneCommand.execute(model.getRepository(), model.getBranch(), model.getUsername(), getPassword(model), workspace, model.isPublish());
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
		pullCommand.execute(workspaceApi, projects, model.isPublish());
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
		pushCommand.execute(workspaceApi, projects, model.getCommitMessage(), model.getUsername(), getPassword(model), model.getEmail());
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
		resetCommand.execute(workspaceApi, projects, model.getUsername(), getPassword(model));
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
	private IWorkspace getWorkspace(String workspace) {
		return workspacesCoreService.getWorkspace(workspace);
	}

	/**
	 * Gets the project.
	 *
	 * @param workspaceApi the workspace api
	 * @param project the project
	 * @return the project
	 */
	private IProject getProject(IWorkspace workspaceApi, String project) {
		return workspaceApi.getProject(project);
	}

	/**
	 * Gets the projects.
	 *
	 * @param workspace the workspace
	 * @param projectsNames the projects names
	 * @return the projects
	 */
	private IProject[] getProjects(IWorkspace workspace, List<String> projectsNames) {
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
		return new String(Base64.getDecoder().decode(model.getPassword().getBytes()), StandardCharsets.UTF_8);
	}
}
