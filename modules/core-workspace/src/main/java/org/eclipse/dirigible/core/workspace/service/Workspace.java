package org.eclipse.dirigible.core.workspace.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.repository.api.ICollection;

public class Workspace extends Folder implements IWorkspace {

	public Workspace(ICollection workspaceCollection) {
		super(workspaceCollection);
	}

	@Override
	public IProject createProject(String name) {
		ICollection collection = this.createCollection(name);
		return new Project(collection);
	}

	@Override
	public IProject getProject(String name) {
		ICollection collection = this.getCollection(name);
		return new Project(collection);
	}

	@Override
	public List<IProject> getProjects() {
		List<IProject> projects = new ArrayList<IProject>();
		List<ICollection> collections = this.getCollections();
		for (ICollection collection : collections) {
			projects.add(new Project(collection));
		}
		return projects;
	}

	@Override
	public void deleteProject(String name) {
		this.removeCollection(name);
	}

	@Override
	public void copyProject(String sourceProject, String targetProject) {
		ICollection collection = this.createCollection(sourceProject);
		collection.copyTo(this.getCollection(targetProject).getPath());
	}

	@Override
	public void copyFolder(String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath) {
		ICollection collection = this.createCollection(sourceProject);
		collection.getCollection(sourceFolderPath).copyTo(this.getCollection(targetProject).getCollection(targetFolderPath).getPath());
	}

	@Override
	public void copyFile(String sourceProject, String sourceFilePath, String targetProject, String targetFilePath) {
		ICollection collection = this.createCollection(sourceProject);
		collection.getResource(sourceFilePath).copyTo(this.getCollection(targetProject).getResource(targetFilePath).getPath());
	}

	@Override
	public void moveProject(String sourceProject, String targetProject) {
		ICollection collection = this.createCollection(sourceProject);
		collection.moveTo(this.getCollection(targetProject).getPath());
	}

	@Override
	public void moveFolder(String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath) {
		ICollection collection = this.createCollection(sourceProject);
		collection.getCollection(sourceFolderPath).moveTo(this.getCollection(targetProject).getCollection(targetFolderPath).getPath());
	}

	@Override
	public void moveFile(String sourceProject, String sourceFilePath, String targetProject, String targetFilePath) {
		ICollection collection = this.createCollection(sourceProject);
		collection.getResource(sourceFilePath).moveTo(this.getCollection(targetProject).getResource(targetFilePath).getPath());
	}

}
