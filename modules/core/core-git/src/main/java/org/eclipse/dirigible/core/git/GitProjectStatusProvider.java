package org.eclipse.dirigible.core.git;

import org.eclipse.dirigible.core.git.command.StatusCommand;
import org.eclipse.dirigible.core.workspace.api.IProjectStatusProvider;
import org.eclipse.dirigible.core.workspace.api.ProjectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitProjectStatusProvider implements IProjectStatusProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(GitProjectStatusProvider.class);

	@Override
	public ProjectStatus getProjectStatus(String workspace, String project) {
		StatusCommand statusCommand = new StatusCommand();
		try {
			return statusCommand.execute(workspace, project);
		} catch (GitConnectorException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
