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
package org.eclipse.dirigible.core.git.project;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;

/**
 * The ProjectMetadataManager is the controller of reading and writing the project.json files.
 */
public class ProjectMetadataManager {

	/** The default branch */
	public static final String BRANCH_MASTER = "master"; //$NON-NLS-1$
	
	/**
	 * Ensure project metadata.
	 *
	 * @param workspace
	 *            the workspace
	 * @param projectName
	 *            the project name
	 */
	public void ensureProjectMetadata(IWorkspace workspace, String projectName) {
		IProject project = workspace.getProject(projectName);
		IFile projectFile = project.getFile(ProjectMetadata.PROJECT_METADATA_FILE_NAME);
		if (!projectFile.exists()) {
			ProjectMetadata projectMetadata = new ProjectMetadata();
			projectMetadata.setGuid(project.getName());
			String projectMetadataJson = ProjectMetadataUtils.toJson(projectMetadata);
			project.createFile(ProjectMetadata.PROJECT_METADATA_FILE_NAME, projectMetadataJson.getBytes(StandardCharsets.UTF_8));
		} else {
			// TODO update branch info?
		}

	}

	/**
	 * Gets the dependencies.
	 *
	 * @param selectedProject
	 *            the selected project
	 * @return the dependencies
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static ProjectMetadataDependency[] getDependencies(IProject selectedProject) throws IOException {
		ProjectMetadata projectMetadata = getProjectMetadata(selectedProject);
		if (projectMetadata == null) {
			return new ProjectMetadataDependency[] {};
		}
		return projectMetadata.getDependencies();
	}

	/**
	 * Gets the project metadata.
	 *
	 * @param selectedProject
	 *            the selected project
	 * @return the project metadata
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static ProjectMetadata getProjectMetadata(IProject selectedProject) throws IOException {
		IFile projectFile = selectedProject.getFile(ProjectMetadata.PROJECT_METADATA_FILE_NAME);
		if (!projectFile.exists()) {
			return null;
		}
		String content = IOUtils.toString(projectFile.getContent());
		return ProjectMetadataUtils.fromJson(content);
	}
}
