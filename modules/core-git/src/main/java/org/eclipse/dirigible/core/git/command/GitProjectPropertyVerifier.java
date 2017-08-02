/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.core.git.command;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.auth.UserFacade;
import org.eclipse.dirigible.core.git.utils.GitProjectProperties;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.repository.api.IRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitProjectPropertyVerifier {

	private static final Logger logger = LoggerFactory.getLogger(GitProjectProperties.class);

	@Inject
	private IRepository repository;

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
