package org.eclipse.dirigible.core.workspace.api;

import java.util.List;

public interface IWorkspace extends IFolder {

	public IProject createProject(String name);

	public IProject getProject(String name);

	public List<IProject> getProjects();

	public void deleteProject(String name);

}
