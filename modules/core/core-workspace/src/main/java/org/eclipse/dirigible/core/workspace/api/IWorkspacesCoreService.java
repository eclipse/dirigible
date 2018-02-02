/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.workspace.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;

/**
 * The Workspaces Core Service interface.
 */
public interface IWorkspacesCoreService extends ICoreService {

	/**
	 * Creates the workspace.
	 *
	 * @param name
	 *            the name
	 * @return the i workspace
	 */
	public IWorkspace createWorkspace(String name);

	/**
	 * Gets the workspace.
	 *
	 * @param name
	 *            the name
	 * @return the workspace
	 */
	public IWorkspace getWorkspace(String name);

	/**
	 * Gets the workspaces.
	 *
	 * @return the workspaces
	 */
	public List<IWorkspace> getWorkspaces();

	/**
	 * Delete workspace.
	 *
	 * @param name
	 *            the name
	 */
	public void deleteWorkspace(String name);

}
