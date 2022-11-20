/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.platform;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.BytesHelper;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;

/**
 * The Workspace Facade.
 */
public class WorkspaceFacade {
	
	/** The workspaces core service. */
	private static WorkspacesCoreService workspacesCoreService = new WorkspacesCoreService();
	
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
		return GsonHelper.toJson(names);
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
	 * Get the file content.
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
