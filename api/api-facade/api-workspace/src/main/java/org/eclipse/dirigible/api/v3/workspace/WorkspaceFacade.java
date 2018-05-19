package org.eclipse.dirigible.api.v3.workspace;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;

/**
 * The Workspace Facade
 */
public class WorkspaceFacade {
	
	private static WorkspacesCoreService workspacesCoreService = StaticInjector.getInjector().getInstance(WorkspacesCoreService.class);
	
	/**
	 * Creates the workspace.
	 *
	 * @param name
	 *            the name
	 * @return the i workspace
	 */
	public static IWorkspace createWorkspace(String name) {
		return workspacesCoreService.createWorkspace(name);
	}
	
	/**
	 * Gets the workspace.
	 *
	 * @param name the workspace name
	 * @return the workspace
	 */
	public static IWorkspace getWorkspace(String name) {
		return workspacesCoreService.getWorkspace(name);
	}
	
	/**
	 * Gets the workspaces names.
	 *
	 * @return the workspaces
	 */
	public static String getWorkspacesNames() {
		List<String> names = new ArrayList<String>();
		for (IWorkspace workspace : workspacesCoreService.getWorkspaces()) {
			names.add(workspace.getName());
		}
		return GsonHelper.GSON.toJson(names);
	}

	/**
	 * Delete workspace.
	 *
	 * @param name
	 *            the name
	 */
	public static void deleteWorkspace(String name) {
		workspacesCoreService.deleteWorkspace(name);
	}
	
	/**
	 * Get the file content
	 *
	 * @param file the file
	 * @return the content
	 */
	public static final byte[] getContent(IFile file) {
		return file.getContent();
	}
	
	/**
	 * Set the file content.
	 *
	 * @param file the file
	 * @param input
	 *            the input
	 */
	public static final void setContent(IFile file, String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		file.setContent(bytes);
	}

	/**
	 * Set the file content.
	 *
	 * @param file the file
	 * @param input
	 *            the input
	 */
	public static final void setContent(IFile file, byte[] input) {
		file.setContent(input);
	}

}
