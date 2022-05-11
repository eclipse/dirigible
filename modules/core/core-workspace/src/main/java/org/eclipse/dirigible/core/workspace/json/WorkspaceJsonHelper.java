/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.workspace.json;

import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.dirigible.core.workspace.api.IProjectStatusProvider;
import org.eclipse.dirigible.core.workspace.api.ProjectStatus;
import org.eclipse.dirigible.core.workspace.api.Status;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;

/**
 * The Workspace Json Helper.
 */
public class WorkspaceJsonHelper {
	
	private static ServiceLoader<IProjectStatusProvider> statusProviders = ServiceLoader.load(IProjectStatusProvider.class);

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
	 * Describe workspace projects.
	 *
	 * @param collection
	 *            the collection
	 * @param removePathPrefix
	 *            the remove path prefix
	 * @param addPathPrefix
	 *            the add path prefix
	 * @return the workspace descriptor
	 */
	public static WorkspaceDescriptor describeWorkspaceProjects(ICollection collection, String removePathPrefix, String addPathPrefix) {
		WorkspaceDescriptor workspacePojo = new WorkspaceDescriptor();
		workspacePojo.setName(collection.getName());
		workspacePojo.setPath(addPathPrefix + collection.getPath().substring(removePathPrefix.length()));
		List<ICollection> collections = collection.getCollections();
		for (ICollection childCollection : collections) {
			workspacePojo.getProjects().add(describeProjectOnly(childCollection, removePathPrefix, addPathPrefix));
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

		Pair<Boolean, String> gitInfo = WorkspaceGitHelper.getGitAware(collection.getRepository(), repositoryPath.toString());

		projectPojo.setGit(gitInfo.getLeft());
		projectPojo.setGitName(gitInfo.getRight());
		ProjectStatus status = null;
		if (gitInfo.getLeft()) {
			for (IProjectStatusProvider statusProvider : statusProviders) {
				status = statusProvider.getProjectStatus(collection.getParent().getName(), collection.getName());
				break;
			}
		}
		
		List<ICollection> collections = collection.getCollections();
		for (ICollection childCollection : collections) {
			projectPojo.getFolders().add(describeFolder(childCollection, removePathPrefix, addPathPrefix, status));
		}

		List<IResource> resources = collection.getResources();
		for (IResource childResource : resources) {
			projectPojo.getFiles().add(describeFile(childResource, removePathPrefix, addPathPrefix, status));
		}

		return projectPojo;
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
	public static ProjectDescriptor describeProjectOnly(ICollection collection, String removePathPrefix, String addPathPrefix) {
		ProjectDescriptor projectPojo = new ProjectDescriptor();
		projectPojo.setName(collection.getName());
		projectPojo.setPath(addPathPrefix + collection.getPath().substring(removePathPrefix.length()));
		RepositoryPath repositoryPath = new RepositoryPath(collection.getPath());

		Pair<Boolean, String> gitInfo = WorkspaceGitHelper.getGitAware(collection.getRepository(), repositoryPath.toString());

		projectPojo.setGit(gitInfo.getLeft());
		projectPojo.setGitName(gitInfo.getRight());
		
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
		return describeFolder(collection, removePathPrefix, addPathPrefix, null);
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
	 * @param status
	 *            the project status
	 * @return the folder descriptor
	 */
	public static FolderDescriptor describeFolder(ICollection collection, String removePathPrefix, String addPathPrefix, ProjectStatus status) {
		FolderDescriptor folderPojo = new FolderDescriptor();
		folderPojo.setName(collection.getName());
		folderPojo.setPath(addPathPrefix + collection.getPath().substring(removePathPrefix.length()));
		if (status != null) {
			
			String path = folderPojo.getPath().substring(1);
			path = path.substring(path.indexOf(IRepository.SEPARATOR) + 1);
			
			if (status.getUntrackedFolders().contains(path)) {
				folderPojo.setStatus(Status.U.name());
			}
		}
		List<ICollection> collections = collection.getCollections();
		for (ICollection childCollection : collections) {
			folderPojo.getFolders().add(describeFolder(childCollection, removePathPrefix, addPathPrefix, status));
		}

		List<IResource> resources = collection.getResources();
		for (IResource childResource : resources) {
			folderPojo.getFiles().add(describeFile(childResource, removePathPrefix, addPathPrefix, status));
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
		return describeFile(resource, removePathPrefix, addPathPrefix, null);
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
	 * @param status
	 *            the project status
	 * @return the file descriptor
	 */
	public static FileDescriptor describeFile(IResource resource, String removePathPrefix, String addPathPrefix, ProjectStatus status) {
		FileDescriptor resourcePojo = new FileDescriptor();
		resourcePojo.setName(resource.getName());
		resourcePojo.setPath(addPathPrefix + resource.getPath().substring(removePathPrefix.length()));
		resourcePojo.setContentType(resource.getContentType());
		if (status != null) {
			
			String path = resourcePojo.getPath().substring(1);
			path = path.substring(path.indexOf(IRepository.SEPARATOR) + 1);
			
			if (status.getAdded().contains(path)) {
				resourcePojo.setStatus(Status.A.name());
			} else if (status.getChanged().contains(path) 
					|| status.getModified().contains(path)
					|| status.getMissing().contains(path)) {
				resourcePojo.setStatus(Status.M.name());
			} else if (status.getConflicting().contains(path)) {
				resourcePojo.setStatus(Status.C.name());
			} else if (status.getRemoved().contains(path)) {
				resourcePojo.setStatus(Status.D.name());
			} else if (status.getUntracked().contains(path)) {
				resourcePojo.setStatus(Status.U.name());
			}
		}
		return resourcePojo;
	}

}
