package org.eclipse.dirigible.core.workspace.json;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;

public class WorkspaceJsonHelper {

	public static Workspace traverseWorkspace(ICollection collection, String removePathPrefix, String addPathPrefix) {
		Workspace workspacePojo = new Workspace();
		workspacePojo.setName(collection.getName());
		workspacePojo.setPath(addPathPrefix + collection.getPath().substring(removePathPrefix.length()));
		for (ICollection childCollection : collection.getCollections()) {
			workspacePojo.getProjects().add(traverseProject(childCollection, removePathPrefix, addPathPrefix));
		}

		return workspacePojo;
	}

	public static Project traverseProject(ICollection collection, String removePathPrefix, String addPathPrefix) {
		Project projectPojo = new Project();
		projectPojo.setName(collection.getName());
		projectPojo.setPath(addPathPrefix + collection.getPath().substring(removePathPrefix.length()));
		for (ICollection childCollection : collection.getCollections()) {
			projectPojo.getFolders().add(traverseFolder(childCollection, removePathPrefix, addPathPrefix));
		}

		return projectPojo;
	}

	public static Folder traverseFolder(ICollection collection, String removePathPrefix, String addPathPrefix) {
		Folder folderPojo = new Folder();
		folderPojo.setName(collection.getName());
		folderPojo.setPath(addPathPrefix + collection.getPath().substring(removePathPrefix.length()));
		for (ICollection childCollection : collection.getCollections()) {
			folderPojo.getFolders().add(traverseFolder(childCollection, removePathPrefix, addPathPrefix));
		}

		for (IResource childResource : collection.getResources()) {
			File resourcePojo = new File();
			resourcePojo.setName(childResource.getName());
			resourcePojo.setPath(addPathPrefix + childResource.getPath().substring(removePathPrefix.length()));
			resourcePojo.setContentType(childResource.getContentType());
			folderPojo.getFiles().add(resourcePojo);
		}

		return folderPojo;
	}

}
