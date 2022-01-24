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

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

/**
 * Processing the Transport Service incoming requests.
 */
public class TransportProcessor {

	private WorkspacesCoreService workspacesCoreService = new WorkspacesCoreService();
	
	private IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

	/**
	 * Import project.
	 *
	 * @param workspace the workspace
	 * @param content the content
	 */
	public void importProject(String workspace, byte[] content) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		repository.importZip(content, workspaceApi.getPath(), true, false, null);
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
