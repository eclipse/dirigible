/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

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
