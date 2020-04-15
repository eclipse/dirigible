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

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.dirigible.repository.local.LocalWorkspaceMapper;

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
		
		projectPojo.setGit(setGitAware(collection, repositoryPath));
		
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
	 * Set the git flag
	 * 
	 * @param collection the collection
	 * @param repositoryPath the path
	 */
	protected static boolean setGitAware(ICollection collection, RepositoryPath repositoryPath) {
		try {
			if (collection.getRepository() instanceof FileSystemRepository) {
				String path = LocalWorkspaceMapper.getMappedName((FileSystemRepository) collection.getRepository(), repositoryPath.toString());
				String gitDirectory = new File(path).getCanonicalPath();
				return Paths.get(Paths.get(gitDirectory).getParent().toString(), ".git").toFile().exists();
			}
		} catch (Exception e) {
			// do nothing
		}
		return false;
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
