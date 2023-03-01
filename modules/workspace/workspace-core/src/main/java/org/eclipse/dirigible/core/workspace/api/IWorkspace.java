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
package org.eclipse.dirigible.core.workspace.api;

import java.io.IOException;
import java.util.List;

/**
 * The Workspace's Workspace interface.
 */
public interface IWorkspace extends IFolder {

	/**
	 * Creates the project.
	 *
	 * @param name
	 *            the name
	 * @return the i project
	 */
	public IProject createProject(String name);

	/**
	 * Gets the project.
	 *
	 * @param name
	 *            the name
	 * @return the project
	 */
	public IProject getProject(String name);

	/**
	 * Gets the projects.
	 *
	 * @return the projects
	 */
	public List<IProject> getProjects();

	/**
	 * Delete project.
	 *
	 * @param name
	 *            the name
	 */
	public void deleteProject(String name);

	/**
	 * Copy project.
	 *
	 * @param sourceProject
	 *            the source project
	 * @param targetProject
	 *            the target project
	 */
	public void copyProject(String sourceProject, String targetProject);

	/**
	 * Copy folder.
	 *
	 * @param sourceProject
	 *            the source project
	 * @param sourceFolderPath
	 *            the source folder path
	 * @param targetProject
	 *            the target project
	 * @param targetFolderPath
	 *            the target folder path
	 */
	public void copyFolder(String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath);

	/**
	 * Copy file.
	 *
	 * @param sourceProject
	 *            the source project
	 * @param sourceFilePath
	 *            the source file path
	 * @param targetProject
	 *            the target project
	 * @param targetFilePath
	 *            the target file path
	 */
	public void copyFile(String sourceProject, String sourceFilePath, String targetProject, String targetFilePath);

	/**
	 * Move project.
	 *
	 * @param sourceProject
	 *            the source project
	 * @param targetProject
	 *            the target project
	 */
	public void moveProject(String sourceProject, String targetProject);

	/**
	 * Move folder.
	 *
	 * @param sourceProject
	 *            the source project
	 * @param sourceFolderPath
	 *            the source folder path
	 * @param targetProject
	 *            the target project
	 * @param targetFolderPath
	 *            the target folder path
	 */
	public void moveFolder(String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath);

	/**
	 * Move file.
	 *
	 * @param sourceProject
	 *            the source project
	 * @param sourceFilePath
	 *            the source file path
	 * @param targetProject
	 *            the target project
	 * @param targetFilePath
	 *            the target file path
	 */
	public void moveFile(String sourceProject, String sourceFilePath, String targetProject, String targetFilePath);

	/**
	 * Link an external directory as a project.
	 *
	 * @param sourceProject the project
	 * @param targetPath the path to the directory
	 * @throws IOException in case of IO error
	 */
	public void linkProject(String sourceProject, String targetPath) throws IOException;

}
