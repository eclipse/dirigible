package org.eclipse.dirigible.core.git.command;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateDependenciesCommand extends CloneCommand {

	private static final Logger logger = LoggerFactory.getLogger(UpdateDependenciesCommand.class);

	public void execute(final IWorkspace workspace, final IProject[] projects, String repositoryUri, String repositoryBranch, String username,
			String password, boolean publishAfterClone) {

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
			break;
		}
	}

}
