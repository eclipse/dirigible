package org.eclipse.dirigible.core.workspace.api;

public interface IProjectStatusProvider {
	
	ProjectStatus getProjectStatus(String workspace, String project);

}
