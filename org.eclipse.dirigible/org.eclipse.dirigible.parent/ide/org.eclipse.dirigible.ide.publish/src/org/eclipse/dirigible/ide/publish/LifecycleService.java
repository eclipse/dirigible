package org.eclipse.dirigible.ide.publish;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IProject;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.runtime.scripting.ILifecycleService;

public class LifecycleService implements ILifecycleService {

	@Override
	public void activateProject(String projectName, HttpServletRequest request) throws PublishException {
		IProject project = WorkspaceLocator.getWorkspace(request).getRoot().getProject(projectName);
		PublishManager.activateProject(project, request);
	}

	@Override
	public void publishProject(String projectName, HttpServletRequest request) throws PublishException {
		IProject project = WorkspaceLocator.getWorkspace(request).getRoot().getProject(projectName);
		PublishManager.publishProject(project, request);

	}

	@Override
	public void publishTemplate(String projectName, HttpServletRequest request) throws PublishException {
		IProject project = WorkspaceLocator.getWorkspace(request).getRoot().getProject(projectName);
		PublishManager.publishTemplate(project, request);

	}

	@Override
	public void activateAll(HttpServletRequest request) throws PublishException {
		PublishManager.activateAll(request);
	}

	@Override
	public void publishAll(HttpServletRequest request) throws PublishException {
		PublishManager.publishAll(request);
	}

}
