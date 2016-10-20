package org.eclipse.dirigible.runtime.scripting.utils;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.runtime.scripting.IWorkspacesService;

public class WorkspacesServiceUtils implements IWorkspacesService {

	private IWorkspacesService workspacesService;

	public WorkspacesServiceUtils() throws Exception {
		Object injectedWorkspacesService = System.getProperties().get(ICommonConstants.WORKSPACES_SERVICE);
		if (injectedWorkspacesService != null) {
			this.workspacesService = (IWorkspacesService) injectedWorkspacesService;
		} else {
			throw new Exception("Workspaces Service doesn't exist or have not been injected.");
		}
	}

	@Override
	public Object getWorkspace(HttpServletRequest request) {
		return this.workspacesService.getWorkspace(request);
	}

	@Override
	public Object getUserWorkspace(String user, HttpServletRequest request) {
		return this.workspacesService.getUserWorkspace(user, request);
	}

}
