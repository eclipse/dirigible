package org.eclipse.dirigible.runtime.search;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.runtime.registry.PathUtils;

public class WorkspaceSearchServlet extends SearchServlet {

	@Override
	protected String getContentLocationPrefix() {
		final String collectionPath = "../" + IRepositoryPaths.WORKSPACE_FOLDER_NAME; //$NON-NLS-1$
		return collectionPath;
	}

	@Override
	protected String getContentDeployPrefix(HttpServletRequest req) {
		return PathUtils.getWorkspacePrefix(req);
	}

}
