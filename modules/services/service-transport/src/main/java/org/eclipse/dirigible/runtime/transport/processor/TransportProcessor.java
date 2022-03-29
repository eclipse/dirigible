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
package org.eclipse.dirigible.runtime.transport.processor;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.eclipse.dirigible.api.v3.utils.UrlFacade;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * Processing the Transport Service incoming requests.
 */
public class TransportProcessor {

	private WorkspacesCoreService workspacesCoreService = new WorkspacesCoreService();
	
	private IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

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
	 * Import files from zip to folder
	 *
	 * @param workspaceName
	 * @param projectName
	 * @param pathInProject
	 * @param content
	 */
	public void importZipToPath(String workspaceName, String projectName, String pathInProject, byte[] content, Boolean override) {
		if (override == null) override = true;
		IWorkspace workspace = getWorkspace(workspaceName);
		String projectPath = workspace.getProject(projectName).getPath();
		String importPath = projectPath + IRepositoryStructure.SEPARATOR  + pathInProject;
		repository.importZip(content, importPath, override, false, null);
	}
  
	public void importProjectInPath(String path, InputStream content) {
		ICollection importedZipFolder = getOrCreateCollection(path);
		String importedZipFolderPath = importedZipFolder.getPath();
		importProject(importedZipFolderPath, content);
	}

	private void importProject(String path, InputStream content) {
		ZipInputStream str = new ZipInputStream(content);
		repository.importZip(str, path, true);
	}

	private ICollection getOrCreateCollection(String path) {
		ICollection repositoryRootCollection = repository.getRoot();
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
		return repository.exportZip(projectApi.getPath(), true);
	}
	
	/**
	 * Export workspace.
	 *
	 * @param workspace the workspace
	 * @return the byte[]
	 */
	public byte[] exportWorkspace(String workspace) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		return repository.exportZip(workspaceApi.getPath(), false);
	}

	/**
	 * Export folder.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param folder the project
	 * @return the byte[]
	 */
	public byte[] exportFolder(String workspace, String project, String folder) throws  UnsupportedEncodingException, DecoderException {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject projectApi = getProject(workspaceApi, project);
		UrlFacade decodedFolder = new UrlFacade();
		String decodedPath = decodedFolder.decode(folder, null);
		return repository.exportZip(projectApi.getPath() + IRepositoryStructure.SEPARATOR + decodedPath, true);
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
		repository.importZip(content, IRepositoryStructure.SEPARATOR, true, false, null);
	}

	/**
	 * Export snapshot.
	 *
	 * @return the byte[]
	 */
	public byte[] exportSnapshot() {
		return repository.exportZip(IRepositoryStructure.SEPARATOR, true);
	}

	
}
