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
package org.eclipse.dirigible.core.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.dirigible.core.git.command.StatusCommand;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.workspace.api.IProjectStatusProvider;
import org.eclipse.dirigible.core.workspace.api.ProjectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GitProjectStatusProvider.
 */
public class GitProjectStatusProvider implements IProjectStatusProvider {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(GitProjectStatusProvider.class);

	/**
	 * Gets the project status.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the project status
	 */
	@Override
	public ProjectStatus getProjectStatus(String workspace, String project) {
		StatusCommand statusCommand = new StatusCommand();
		try {
			return statusCommand.execute(workspace, project);
		} catch (GitConnectorException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}
		return null;
	}

	@Override
	public String getProjectGitFolder(String workspace, String project) throws IOException {
		File gitDirectory = GitFileUtils.getGitDirectoryByRepositoryName(workspace, project).getCanonicalFile();
		String git = gitDirectory.getCanonicalPath() + File.separator;
		return git;
	}

}
