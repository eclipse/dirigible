package org.eclipse.dirigible.core.workspace.api;

import java.util.List;

public interface IWorkspace extends IFolder {

	public IProject createProject(String name);

	public IProject getProject(String name);

	public List<IProject> getProjects();

	public void deleteProject(String name);

	public void copyProject(String sourceProject, String targetProject);

	public void copyFolder(String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath);

	public void copyFile(String sourceProject, String sourceFilePath, String targetProject, String targetFilePath);

	public void moveProject(String sourceProject, String targetProject);

	public void moveFolder(String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath);

	public void moveFile(String sourceProject, String sourceFilePath, String targetProject, String targetFilePath);

}
