/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.workspace.impl.Workspace;

/**
 * This class is similar to the
 * <code>org.eclipse.core.resources.ResourcesPlugin</code> class. It mimics most
 * of the methods.
 * <p>
 * What differs this class from the original is that it provides a unique
 * workspace for each of the users. This is required because of the milti-user
 * architecture of RAP.
 */
public class RemoteResourcesPlugin {

	public static final String PLUGIN_ID = "org.eclipse.dirigible.ide.workspace"; //$NON-NLS-1$

	/**
	 * Returns the workspace corresponding to this user session.
	 * <p>
	 * Once obtained, the {@link IWorkspace} instance can be used to create
	 * projects, directories, files, etc.
	 * <p>
	 * <strong>Node:</strong>Currently, the returned implementation will not
	 * support most of the methods and will throw an
	 * {@link UnsupportedOperationException} exception when that is the case.
	 *
	 * @see UnsupportedOperationException
	 * @see IWorkspace
	 * @return an {@link IWorkspace} instance.
	 */
	public static IWorkspace getWorkspace() {
		String user = getUserName();
		return getWorkspace(user);
	}

	public static String getUserName() {
		return CommonIDEParameters.getUserName();
	}

	public static IWorkspace getWorkspace(HttpServletRequest request) {
		String user = getUserName(request);
		return getWorkspace(user, request);
	}

	public static String getUserName(HttpServletRequest request) {
		return CommonIDEParameters.getUserName(request);
	}

	/**
	 * Returns a {@link Workspace} instance for the specified user name.
	 * <p>
	 * <strong>Node:</strong>Currently, the returned implementation will not
	 * support most of the methods and will throw an
	 * {@link UnsupportedOperationException} exception when that is the case.
	 *
	 * @see UnsupportedOperationException
	 * @see IWorkspace
	 * @param user
	 *            the user for which a workspace must be returned.
	 * @return a {@link Workspace} instance.
	 */
	public static IWorkspace getWorkspace(String user, HttpServletRequest request) {
		// CommonIDEParameters.initSystemParameters();
		Workspace workspace = (Workspace) CommonIDEParameters.getObject(Workspace.class.getCanonicalName(), request);
		if (workspace == null) {
			workspace = new Workspace(request);
		}
		workspace.initialize(user);
		return workspace;
	}

	public static IWorkspace getWorkspace(String user) {
		// CommonIDEParameters.initSystemParameters();
		Workspace workspace = (Workspace) CommonIDEParameters.getObject(Workspace.class.getCanonicalName());
		if (workspace == null) {
			workspace = new Workspace();
		}
		workspace.initialize(user);
		return workspace;
	}

	/**
	 * Always returns the shared workspace corresponding to the GUEST user.
	 * <p>
	 * Once obtained, the {@link IWorkspace} instance can be used to create
	 * projects, directories, files, etc.
	 * <p>
	 * <strong>Node:</strong>Currently, the returned implementation will not
	 * support most of the methods and will throw an
	 * {@link UnsupportedOperationException} exception when that is the case.
	 *
	 * @see UnsupportedOperationException
	 * @see IWorkspace
	 * @return an {@link IWorkspace} instance.
	 */
	public static IWorkspace getSharedWorkspace() {
		return getWorkspace(CommonIDEParameters.getUserName());
	}

}
