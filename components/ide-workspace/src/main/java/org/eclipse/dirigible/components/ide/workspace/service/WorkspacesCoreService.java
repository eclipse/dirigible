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
package org.eclipse.dirigible.components.ide.workspace.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.components.api.security.UserFacade;
import org.eclipse.dirigible.components.ide.workspace.domain.Workspace;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Workspaces Core Service.
 */
@Service
public class WorkspacesCoreService {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(WorkspacesCoreService.class);

	/** The Constant DEFAULT_WORKSPACE_NAME. */
	private static final String DEFAULT_WORKSPACE_NAME = "workspace";

	/** The repository. */
	private final IRepository repository;
	
	@Autowired
	public WorkspacesCoreService(IRepository repository) {
		this.repository = repository;
	}
	
	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	protected IRepository getRepository() {
		return repository;
	}

	/**
	 * Creates the workspace.
	 *
	 * @param name the name
	 * @return the i workspace
	 */
	public Workspace createWorkspace(String name) {
		ICollection collection = getWorkspace(name);
		collection.create();
		if (logger.isInfoEnabled()) {logger.info("Workspace created [{}]", collection.getPath());}
		return new Workspace(collection);
	}

	/**
	 * Gets the workspace.
	 *
	 * @param name the name
	 * @return the workspace
	 */
	public Workspace getWorkspace(String name) {
		StringBuilder workspacePath = generateWorkspacePath(name, null, null);
		ICollection collection = getRepository().getCollection(workspacePath.toString());
		return new Workspace(collection);
	}

	/**
	 * Gets the workspaces.
	 *
	 * @return the workspaces
	 */
	public List<Workspace> getWorkspaces() {
		StringBuilder workspacePath = generateWorkspacePath(null, null, null);
		ICollection root = getRepository().getCollection(workspacePath.toString());
		List<Workspace> workspaces = new ArrayList<Workspace>();
		if (!root.exists()) {
			root.create();
		}
		List<ICollection> collections = root.getCollections();
		for (ICollection collection : collections) {
			workspaces.add(new Workspace(collection));
		}
		if (workspaces.isEmpty()) {
			ICollection collection = root.createCollection(DEFAULT_WORKSPACE_NAME);
			workspaces.add(new Workspace(collection));
		}
		return workspaces;
	}

	/**
	 * Delete workspace.
	 *
	 * @param name the name
	 */
	public void deleteWorkspace(String name) {
		ICollection collection = getWorkspace(name);
		collection.delete();
		if (logger.isInfoEnabled()) {logger.info("Workspace deleted [{}]", collection.getPath());}
	}

	/**
	 * Generate workspace path.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @return the string builder
	 */
	private StringBuilder generateWorkspacePath(String workspace, String project, String path) {
		StringBuilder relativePath = new StringBuilder(IRepositoryStructure.PATH_USERS).append(IRepositoryStructure.SEPARATOR)
				.append(UserFacade.getName());
		if (workspace != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(workspace);
		}
		if (project != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(project);
		}
		if (path != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(path);
		}
		return relativePath;
	}

}
