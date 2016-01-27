package org.eclipse.dirigible.runtime.registry;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;

public class WorkspaceServlet extends RepositoryServlet {

	@Override
	protected String getRepositoryPathPrefix(HttpServletRequest req) {
		return IRepositoryPaths.DB_DIRIGIBLE_USERS + RequestUtils.getUser(req) + IRepository.SEPARATOR + IRepositoryPaths.WORKSPACE_FOLDER_NAME;
	}

}
