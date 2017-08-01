/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.core.git.project;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;

public class ProjectMetadataManager {

	@Inject
	private WorkspacesCoreService workspacesCoreService;

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

	public static String getBranch(IProject selectedProject) throws IOException {
		IFile projectFile = selectedProject.getFile(ProjectMetadata.PROJECT_METADATA_FILE_NAME);
		String content = IOUtils.toString(projectFile.getContent());
		ProjectMetadata projectMetadata = ProjectMetadataUtils.fromJson(content);
		ProjectMetadataRepository repository = projectMetadata.getRepository();
		String branch = "master";
		if (repository != null) {
			branch = repository.getBranch();
		}
		return branch;
	}

	public static ProjectMetadataDependency[] getDependencies(IProject selectedProject) throws IOException {
		IFile projectFile = selectedProject.getFile(ProjectMetadata.PROJECT_METADATA_FILE_NAME);
		if (!projectFile.exists()) {
			return null;
		}
		String content = IOUtils.toString(projectFile.getContent());
		ProjectMetadata projectMetadata = ProjectMetadataUtils.fromJson(content);
		ProjectMetadataDependency[] dependencies = projectMetadata.getDependencies();
		return dependencies;
	}

}
