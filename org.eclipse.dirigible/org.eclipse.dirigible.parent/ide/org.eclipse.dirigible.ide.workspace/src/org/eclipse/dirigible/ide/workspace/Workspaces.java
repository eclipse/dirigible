package org.eclipse.dirigible.ide.workspace;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.runtime.scripting.IWorkspacesService;

public class Workspaces implements IWorkspacesService {

	@Override
	public IWorkspace getWorkspace(HttpServletRequest request) {
		return RemoteResourcesPlugin.getWorkspace(request);
	}

	@Override
	public IWorkspace getUserWorkspace(String user, HttpServletRequest request) {
		return RemoteResourcesPlugin.getWorkspace(user, request);
	}

}
