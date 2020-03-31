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
package org.eclipse.dirigible.core.workspace.json;

import java.util.List;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;

/**
 * The Workspace Json Helper.
 */
public class WorkspaceJsonHelper {

	/**
	 * Describe workspace.
	 *
	 * @param collection
	 *            the collection
	 * @param removePathPrefix
	 *            the remove path prefix
	 * @param addPathPrefix
	 *            the add path prefix
	 * @return the workspace descriptor
	 */
	public static WorkspaceDescriptor describeWorkspace(ICollection collection, String removePathPrefix, String addPathPrefix) {
		WorkspaceDescriptor workspacePojo = new WorkspaceDescriptor();
		workspacePojo.setName(collection.getName());
		workspacePojo.setPath(addPathPrefix + collection.getPath().substring(removePathPrefix.length()));
		List<ICollection> collections = collection.getCollections();
		for (ICollection childCollection : collections) {
			workspacePojo.getProjects().add(describeProject(childCollection, removePathPrefix, addPathPrefix));
		}

		return workspacePojo;
	}

	/**
	 * Describe project.
	 *
	 * @param collection
	 *            the collection
	 * @param removePathPrefix
	 *            the remove path prefix
	 * @param addPathPrefix
	 *            the add path prefix
	 * @return the project descriptor
	 */
	public static ProjectDescriptor describeProject(ICollection collection, String removePathPrefix, String addPathPrefix) {
		ProjectDescriptor projectPojo = new ProjectDescriptor();
		projectPojo.setName(collection.getName());
		projectPojo.setPath(addPathPrefix + collection.getPath().substring(removePathPrefix.length()));
		RepositoryPath repositoryPath = new RepositoryPath(collection.getPath());
		repositoryPath.setSegment(0, "git");
		projectPojo.setGit(collection.getRepository().hasCollection(repositoryPath.toString()));
		List<ICollection> collections = collection.getCollections();
		for (ICollection childCollection : collections) {
			projectPojo.getFolders().add(describeFolder(childCollection, removePathPrefix, addPathPrefix));
		}

		List<IResource> resources = collection.getResources();
		for (IResource childResource : resources) {
			FileDescriptor resourcePojo = new FileDescriptor();
			resourcePojo.setName(childResource.getName());
			resourcePojo.setPath(addPathPrefix + childResource.getPath().substring(removePathPrefix.length()));
			resourcePojo.setContentType(childResource.getContentType());
			projectPojo.getFiles().add(resourcePojo);
		}

		return projectPojo;
	}

	/**
	 * Describe folder.
	 *
	 * @param collection
	 *            the collection
	 * @param removePathPrefix
	 *            the remove path prefix
	 * @param addPathPrefix
	 *            the add path prefix
	 * @return the folder descriptor
	 */
	public static FolderDescriptor describeFolder(ICollection collection, String removePathPrefix, String addPathPrefix) {
		FolderDescriptor folderPojo = new FolderDescriptor();
		folderPojo.setName(collection.getName());
		folderPojo.setPath(addPathPrefix + collection.getPath().substring(removePathPrefix.length()));
		List<ICollection> collections = collection.getCollections();
		for (ICollection childCollection : collections) {
			folderPojo.getFolders().add(describeFolder(childCollection, removePathPrefix, addPathPrefix));
		}

		List<IResource> resources = collection.getResources();
		for (IResource childResource : resources) {
			FileDescriptor resourcePojo = new FileDescriptor();
			resourcePojo.setName(childResource.getName());
			resourcePojo.setPath(addPathPrefix + childResource.getPath().substring(removePathPrefix.length()));
			resourcePojo.setContentType(childResource.getContentType());
			folderPojo.getFiles().add(resourcePojo);
		}

		return folderPojo;
	}

	/**
	 * Describe file.
	 *
	 * @param resource
	 *            the resource
	 * @param removePathPrefix
	 *            the remove path prefix
	 * @param addPathPrefix
	 *            the add path prefix
	 * @return the file descriptor
	 */
	public static FileDescriptor describeFile(IResource resource, String removePathPrefix, String addPathPrefix) {
		FileDescriptor resourcePojo = new FileDescriptor();
		resourcePojo.setName(resource.getName());
		resourcePojo.setPath(addPathPrefix + resource.getPath().substring(removePathPrefix.length()));
		resourcePojo.setContentType(resource.getContentType());
		return resourcePojo;
	}

}
