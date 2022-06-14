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
package org.eclipse.dirigible.core.git.command;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Update project's dependencies.
 */
public class UpdateDependenciesCommand extends CloneCommand {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(UpdateDependenciesCommand.class);

	/**
	 * Execute Update Dependencies Command.
	 *
	 * @param workspace
	 *            the workspace
	 * @param projects
	 *            the projects
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param publishAfterClone
	 *            the publish after clone
	 * @throws GitConnectorException in case of exception
	 */
	public void execute(final IWorkspace workspace, final IProject[] projects, String username, String password, boolean publishAfterClone) throws GitConnectorException {
		for (IProject selectedProject : projects) {
			String user = UserFacade.getName();
			try {
				Set<String> clonedProjects = new HashSet<String>();
				cloneDependencies(user, username, password, workspace, clonedProjects, selectedProject.getName());
				if (publishAfterClone) {
					publishProjects(workspace, clonedProjects);
				}
				logger.info(String.format("Project's [%s] dependencies has been updated successfully.", selectedProject.getName()));
			} catch (IOException | GitConnectorException e) {
				String errorMessage = String.format("Error occured while updating dependencies of the project [%s]", selectedProject.getName());
				logger.error(errorMessage, e);
				throw new GitConnectorException(errorMessage, e);
			}
		}
	}

}
