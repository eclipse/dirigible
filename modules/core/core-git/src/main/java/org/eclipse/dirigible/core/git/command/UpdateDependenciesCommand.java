/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.git.command;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
	 */
	public void execute(final IWorkspace workspace, final IProject[] projects, String username, String password, boolean publishAfterClone) {
		for (IProject selectedProject : projects) {
			try {
				Set<String> clonedProjects = new HashSet<String>();
				cloneDependencies(username, password, workspace, clonedProjects, selectedProject.getName());
				if (publishAfterClone) {
					publishProjects(workspace, clonedProjects);
				}
				logger.info(String.format("Project's [%s] dependencies has been updated successfully.", selectedProject.getName()));
			} catch (IOException e) {
				logger.error(String.format("Error occured while updating dependencies of the project [%s]", selectedProject.getName()), e);
			} catch (GitConnectorException e) {
				logger.error(String.format("Error occured while updating dependencies of the project [%s]", selectedProject.getName()), e);
			}
		}
	}

}
