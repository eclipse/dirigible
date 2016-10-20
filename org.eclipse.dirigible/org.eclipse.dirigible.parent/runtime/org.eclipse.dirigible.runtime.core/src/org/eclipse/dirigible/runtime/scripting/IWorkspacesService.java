package org.eclipse.dirigible.runtime.scripting;

import javax.servlet.http.HttpServletRequest;

public interface IWorkspacesService {

	public Object getWorkspace(HttpServletRequest request);

	public Object getUserWorkspace(String user, HttpServletRequest request);

}
