package org.eclipse.dirigible.core.workspace.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;

public interface IWorkspacesCoreService extends ICoreService {

	public IWorkspace createWorkspace(String name);

	public IWorkspace getWorkspace(String name);

	public List<IWorkspace> getWorkspaces();

	public void deleteWorkspace(String name);

}
