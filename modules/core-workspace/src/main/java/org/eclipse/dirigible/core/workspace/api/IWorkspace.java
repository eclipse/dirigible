/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.workspace.api;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface IWorkspace.
 */
public interface IWorkspace extends IFolder {

	/**
	 * Creates the project.
	 *
	 * @param name the name
	 * @return the i project
	 */
	public IProject createProject(String name);

	/**
	 * Gets the project.
	 *
	 * @param name the name
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
	 * @param name the name
	 */
	public void deleteProject(String name);

	/**
	 * Copy project.
	 *
	 * @param sourceProject the source project
	 * @param targetProject the target project
	 */
	public void copyProject(String sourceProject, String targetProject);

	/**
	 * Copy folder.
	 *
	 * @param sourceProject the source project
	 * @param sourceFolderPath the source folder path
	 * @param targetProject the target project
	 * @param targetFolderPath the target folder path
	 */
	public void copyFolder(String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath);

	/**
	 * Copy file.
	 *
	 * @param sourceProject the source project
	 * @param sourceFilePath the source file path
	 * @param targetProject the target project
	 * @param targetFilePath the target file path
	 */
	public void copyFile(String sourceProject, String sourceFilePath, String targetProject, String targetFilePath);

	/**
	 * Move project.
	 *
	 * @param sourceProject the source project
	 * @param targetProject the target project
	 */
	public void moveProject(String sourceProject, String targetProject);

	/**
	 * Move folder.
	 *
	 * @param sourceProject the source project
	 * @param sourceFolderPath the source folder path
	 * @param targetProject the target project
	 * @param targetFolderPath the target folder path
	 */
	public void moveFolder(String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath);

	/**
	 * Move file.
	 *
	 * @param sourceProject the source project
	 * @param sourceFilePath the source file path
	 * @param targetProject the target project
	 * @param targetFilePath the target file path
	 */
	public void moveFile(String sourceProject, String sourceFilePath, String targetProject, String targetFilePath);

}
