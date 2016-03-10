/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.content;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.ext.security.IRoles;
import org.eclipse.dirigible.repository.ext.utils.InstanceUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.PermissionsUtils;

/**
 * Exports the full content from the repository as 'clone'
 * Can be used for backups as well
 */
public class CloneExporterServlet extends ContentExporterServlet {

	private static final long serialVersionUID = -2906745438795974322L;

	private static final Logger logger = Logger.getLogger(CloneExporterServlet.class);

	static final String CLONE_PATH_FOR_EXPORT = IRepositoryPaths.DB_DIRIGIBLE_ROOT.substring(0, IRepositoryPaths.DB_DIRIGIBLE_ROOT.length() - 1);

	public static final String CLONE = "clone";

	@Override
	protected String getDefaultPathForExport() {
		return CLONE_PATH_FOR_EXPORT;
	}

	@Override
	protected String getExportFilePrefix() {
		StringBuilder buff = new StringBuilder();
		buff.append(CLONE).append(UNDERSCORE);
		buff.append(InstanceUtils.getInstanceName());
		return buff.toString();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (!PermissionsUtils.isUserInRole(request, IRoles.ROLE_OPERATOR)) {
			String err = String.format(PermissionsUtils.PERMISSION_ERR, "Export Cloned Content");
			logger.error(err);
			throw new ServletException(err);
		}

		super.doGet(request, response);
	}
}
