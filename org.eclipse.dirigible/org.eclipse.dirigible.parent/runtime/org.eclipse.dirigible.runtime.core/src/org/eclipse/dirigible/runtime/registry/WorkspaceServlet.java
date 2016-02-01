package org.eclipse.dirigible.runtime.registry;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.logging.Logger;

public class WorkspaceServlet extends RepositoryServlet {

	private static final Logger logger = Logger.getLogger(WorkspaceServlet.class);

	@Override
	protected String getRepositoryPathPrefix(HttpServletRequest req) {
		return PathUtils.getWorkspacePrefix(req);
	}

}
