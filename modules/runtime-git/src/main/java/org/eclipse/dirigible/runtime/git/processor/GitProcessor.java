package org.eclipse.dirigible.runtime.git.processor;

import java.util.ArrayList;
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
import org.eclipse.dirigible.runtime.git.model.GitCloneModel;
import org.eclipse.dirigible.runtime.git.model.GitPullModel;
import org.eclipse.dirigible.runtime.git.model.GitPushModel;
import org.eclipse.dirigible.runtime.git.model.GitResetModel;
import org.eclipse.dirigible.runtime.git.model.GitShareModel;
import org.eclipse.dirigible.runtime.git.model.GitUpdateDepenciesModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing the Database SQL Queries Service incoming requests
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

	public void clone(String workspace, GitCloneModel model) throws GitConnectorException {
		cloneCommand.execute(model.getRepository(), model.getBranch(), model.getUsername(), model.getPassword(), workspace, model.isPublish());
	}

	public void pull(String workspace, GitPullModel model) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject[] projects = getProjects(workspaceApi, model.getProjects());
		pullCommand.execute(workspaceApi, projects, model.isPublish());
	}

	public void push(String workspace, GitPushModel model) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject[] projects = getProjects(workspaceApi, model.getProjects());
		pushCommand.execute(workspaceApi, projects, model.getCommitMessage(), model.getUsername(), model.getPassword(), model.getEmail());
	}

	public void reset(String workspace, GitResetModel model) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject[] projects = getProjects(workspaceApi, model.getProjects());
		resetCommand.execute(workspaceApi, projects, model.getUsername(), model.getPassword());
	}

	public void share(String workspace, GitShareModel model) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject project = getProject(workspaceApi, model.getProject());
		shareCommand.execute(workspaceApi, project, model.getRepository(), model.getBranch(), model.getCommitMessage(), model.getUsername(),
				model.getPassword(), model.getEmail());
	}

	public void updateDependencies(String workspace, GitUpdateDepenciesModel model) throws GitConnectorException {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject[] projects = getProjects(workspaceApi, model.getProjects());
		updateDependenciesCommand.execute(workspaceApi, projects, model.getUsername(), model.getPassword(), model.isPublish());
	}

	private IWorkspace getWorkspace(String workspace) {
		return workspacesCoreService.getWorkspace(workspace);
	}

	private IProject getProject(IWorkspace workspaceApi, String project) {
		return workspaceApi.getProject(project);
	}

	private IProject[] getProjects(IWorkspace workspace, List<String> projectsNames) {
		List<IProject> projects = new ArrayList<IProject>();
		for (String next : projectsNames) {
			projects.add(workspace.getProject(next));
		}
		return projects.toArray(new IProject[] {});
	}
}
