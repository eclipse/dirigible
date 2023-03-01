/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.transport.processor;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.DecoderException;
import org.eclipse.dirigible.api.v3.utils.UrlFacade;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

/**
 * Processing the Transport Service incoming requests.
 */
public class TransportProcessor {

	/** The workspaces core service. */
	private WorkspacesCoreService workspacesCoreService = new WorkspacesCoreService();
	
	/** The repository. */
	private IRepository repository = null;
	
	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	protected synchronized IRepository getRepository() {
		if (repository == null) {
			repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		}
		return repository;
	}

	/**
	 * Import project.
	 *
	 * @param workspaceName the workspace name
	 * @param content the content
	 */
	public void importProjectInWorkspace(String workspaceName, InputStream content) {
		IWorkspace workspace = getWorkspace(workspaceName);
		String workspacePath = workspace.getPath();
		importProject(workspacePath, content);
	}

	/**
	 * Import files from zip to folder.
	 *
	 * @param workspaceName the workspace name
	 * @param projectName the project name
	 * @param pathInProject the path in project
	 * @param content the content
	 * @param override the override
	 */
	public void importZipToPath(String workspaceName, String projectName, String pathInProject, byte[] content, Boolean override) {
		if (override == null) override = true;
		IWorkspace workspace = getWorkspace(workspaceName);
		String projectPath = workspace.getProject(projectName).getPath();
		String importPath = projectPath + IRepositoryStructure.SEPARATOR  + pathInProject;
		getRepository().importZip(content, importPath, override, false, null);
	}
  
	/**
	 * Import project in path.
	 *
	 * @param path the path
	 * @param content the content
	 */
	public void importProjectInPath(String path, InputStream content) {
		ICollection importedZipFolder = getOrCreateCollection(path);
		String importedZipFolderPath = importedZipFolder.getPath();
		importProject(importedZipFolderPath, content);
	}

	/**
	 * Import project.
	 *
	 * @param path the path
	 * @param content the content
	 */
	private void importProject(String path, InputStream content) {
		ZipInputStream str = new ZipInputStream(content);
		getRepository().importZip(str, path, true);
	}

	/**
	 * Gets the or create collection.
	 *
	 * @param path the path
	 * @return the or create collection
	 */
	private ICollection getOrCreateCollection(String path) {
		ICollection repositoryRootCollection = getRepository().getRoot();
		ICollection importedZipFolder = repositoryRootCollection.getCollection(path);

		if (!importedZipFolder.exists()) {
			importedZipFolder = repositoryRootCollection.createCollection(path);
		}

		return importedZipFolder;
	}

	/**
	 * Export project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the byte[]
	 */
	public byte[] exportProject(String workspace, String project) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject projectApi = getProject(workspaceApi, project);
		return getRepository().exportZip(projectApi.getPath(), true);
	}
	
	/**
	 * Export workspace.
	 *
	 * @param workspace the workspace
	 * @return the byte[]
	 */
	public byte[] exportWorkspace(String workspace) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		return getRepository().exportZip(workspaceApi.getPath(), false);
	}

	/**
	 * Export folder.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param folder the project
	 * @return the byte[]
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws DecoderException the decoder exception
	 */
	public byte[] exportFolder(String workspace, String project, String folder) throws  UnsupportedEncodingException, DecoderException {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject projectApi = getProject(workspaceApi, project);
		UrlFacade decodedFolder = new UrlFacade();
		String decodedPath = decodedFolder.decode(folder, null);
		return getRepository().exportZip(projectApi.getPath() + IRepositoryStructure.SEPARATOR + decodedPath, true);
	}

	/**
	 * Gets the workspace.
	 *
	 * @param workspace the workspace
	 * @return the workspace
	 */
	private IWorkspace getWorkspace(String workspace) {
		return workspacesCoreService.getWorkspace(workspace);
	}

	/**
	 * Gets the project.
	 *
	 * @param workspaceApi the workspace api
	 * @param project the project
	 * @return the project
	 */
	private IProject getProject(IWorkspace workspaceApi, String project) {
		return workspaceApi.getProject(project);
	}
	
	/**
	 * Import snapshot.
	 *
	 * @param content the content
	 */
	public void importSnapshot(byte[] content) {
		getRepository().importZip(content, IRepositoryStructure.SEPARATOR, true, false, null);
	}

	/**
	 * Export snapshot.
	 *
	 * @return the byte[]
	 */
	public byte[] exportSnapshot() {
		return getRepository().exportZip(IRepositoryStructure.SEPARATOR, true);
	}
	
	/**
	 * Import files to folder.
	 *
	 * @param workspaceName the workspace name
	 * @param projectName the project name
	 * @param pathInProject the path in project
	 * @param content the content
	 */
	public void importFileToPath(String workspaceName, String projectName, String pathInProject, byte[] content) {
		IWorkspace workspace = getWorkspace(workspaceName);
		String projectPath = workspace.getProject(projectName).getPath();
		String importPath = projectPath + IRepositoryStructure.SEPARATOR  + pathInProject;
		if (getRepository().hasResource(importPath)) {
			getRepository().getResource(importPath).setContent(content);
		} else {
			getRepository().createResource(importPath, content);
		}
	}

	
}
