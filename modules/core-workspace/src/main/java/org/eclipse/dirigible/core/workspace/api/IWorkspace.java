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

public interface IWorkspace extends IFolder {

	public IProject createProject(String name);

	public IProject getProject(String name);

	public List<IProject> getProjects();

	public void deleteProject(String name);

	public void copyProject(String sourceProject, String targetProject);

	public void copyFolder(String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath);

	public void copyFile(String sourceProject, String sourceFilePath, String targetProject, String targetFilePath);

	public void moveProject(String sourceProject, String targetProject);

	public void moveFolder(String sourceProject, String sourceFolderPath, String targetProject, String targetFolderPath);

	public void moveFile(String sourceProject, String sourceFilePath, String targetProject, String targetFilePath);

}
