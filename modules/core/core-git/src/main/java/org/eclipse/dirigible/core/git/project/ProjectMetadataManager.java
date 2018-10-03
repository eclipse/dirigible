/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.git.project;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

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
	
	/** The workspaces core service. */
	@Inject
	private WorkspacesCoreService workspacesCoreService;

	/**
	 * Ensure project metadata.
	 *
	 * @param workspace
	 *            the workspace
	 * @param projectName
	 *            the project name
	 * @param repositoryURI
	 *            the repository URI
	 * @param branch
	 *            the branch
	 */
	public void ensureProjectMetadata(IWorkspace workspace, String projectName, String repositoryURI, String branch) {
		IProject project = workspace.getProject(projectName);
		IFile projectFile = project.getFile(ProjectMetadata.PROJECT_METADATA_FILE_NAME);
		if (!projectFile.exists()) {
			ProjectMetadata projectMetadata = new ProjectMetadata();
			projectMetadata.setGuid(project.getName());
			ProjectMetadataRepository projectMetadataRepository = new ProjectMetadataRepository();
			projectMetadataRepository.setType(ProjectMetadataRepository.GIT);
			projectMetadataRepository.setUrl(repositoryURI);
			projectMetadataRepository.setBranch(branch);
			projectMetadata.setRepository(projectMetadataRepository);
			String projectMetadataJson = ProjectMetadataUtils.toJson(projectMetadata);
			project.createFile(ProjectMetadata.PROJECT_METADATA_FILE_NAME, projectMetadataJson.getBytes(StandardCharsets.UTF_8));
		} else {
			// TODO update branch info?
		}

	}

	/**
	 * Gets the branch.
	 *
	 * @param selectedProject
	 *            the selected project
	 * @return the branch
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String getBranch(IProject selectedProject) throws IOException {
		ProjectMetadata projectMetadata = getProjectMetadata(selectedProject);
		ProjectMetadataRepository repository = projectMetadata.getRepository();
		return repository != null ? repository.getBranch() : BRANCH_MASTER;
	}

	/**
	 * Gets the repository uri.
	 *
	 * @param selectedProject
	 *            the selected project
	 * @return the repository uri
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String getRepositoryUri(IProject selectedProject) throws IOException {
		ProjectMetadata projectMetadata = getProjectMetadata(selectedProject);
		if (projectMetadata == null) {
			return null;
		}
		return projectMetadata.getRepository() != null ? projectMetadata.getRepository().getUrl() : null;
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
