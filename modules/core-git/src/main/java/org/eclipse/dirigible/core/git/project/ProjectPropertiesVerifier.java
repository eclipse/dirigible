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

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.core.git.utils.GitProjectProperties;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.repository.api.IRepository;

// TODO: Auto-generated Javadoc
/**
 * The Class ProjectPropertiesVerifier.
 */
public class ProjectPropertiesVerifier {

	/** The repository. */
	@Inject
	private IRepository repository;

	/**
	 * Verify.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return true, if successful
	 */
	public boolean verify(IWorkspace workspace, IProject project) {
		boolean result = false;
		String user = UserFacade.getName();
		String workspaceName = workspace.getName();
		String projectName = project.getName();
		String gitFilePath = String.format(GitProjectProperties.GIT_PROPERTY_FILE_LOCATION, user, workspaceName, projectName);
		result = repository.hasResource(gitFilePath);
		return result;
	}

}
