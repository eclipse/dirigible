/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.jgit.command;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.project.ProjectMetadata;
import org.eclipse.dirigible.repository.project.ProjectMetadataDependency;
import org.eclipse.dirigible.repository.project.ProjectMetadataRepository;
import org.eclipse.dirigible.repository.project.ProjectMetadataUtils;

public class ProjectMetadataManager {

	public static void ensureProjectMetadata(String projectName, String repositoryURI, String branch) throws CoreException {

		IProject[] projects = WorkspaceLocator.getWorkspace(CommonIDEParameters.getRequest()).getRoot().getProjects();
		for (IProject project : projects) {
			if (project.getName().equals(projectName)) {
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
					ByteArrayInputStream in = new ByteArrayInputStream(projectMetadataJson.getBytes(ICommonConstants.UTF8));
					projectFile.create(in, true, new NullProgressMonitor());

				} else {
					// TODO update branch info?
				}
			}
		}
	}

	public static String getBranch(IProject selectedProject) throws IOException, CoreException {
		IFile projectFile = selectedProject.getFile(ProjectMetadata.PROJECT_METADATA_FILE_NAME);
		String content = IOUtils.toString(projectFile.getContents());
		ProjectMetadata projectMetadata = ProjectMetadataUtils.fromJson(content);
		ProjectMetadataRepository repository = projectMetadata.getRepository();
		String branch = "master";
		if (repository != null) {
			branch = repository.getBranch();
		}
		return branch;
	}

	public static ProjectMetadataDependency[] getDependencies(IProject selectedProject) throws IOException, CoreException {
		IFile projectFile = selectedProject.getFile(ProjectMetadata.PROJECT_METADATA_FILE_NAME);
		if (!projectFile.exists()) {
			return null;
		}
		String content = IOUtils.toString(projectFile.getContents());
		ProjectMetadata projectMetadata = ProjectMetadataUtils.fromJson(content);
		ProjectMetadataDependency[] dependencies = projectMetadata.getDependencies();
		return dependencies;
	}

}
