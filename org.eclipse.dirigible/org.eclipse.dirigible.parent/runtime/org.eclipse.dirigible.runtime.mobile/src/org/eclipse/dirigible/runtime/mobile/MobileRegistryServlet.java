/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.mobile;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.runtime.filter.SandboxFilter;
import org.eclipse.dirigible.runtime.registry.PathUtils;
import org.eclipse.dirigible.runtime.registry.RegistryServlet;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;

public class MobileRegistryServlet extends RegistryServlet {

	private static final long serialVersionUID = -491670839750494628L;

	private static final String CONTENT_FOLDER = "/" + ICommonConstants.ARTIFACT_TYPE.MOBILE_APPLICATIONS; //$NON-NLS-1$

	@Override
	protected String extractRepositoryPath(HttpServletRequest request) throws IllegalArgumentException {
		String requestPath = PathUtils.extractPath(request);
		if ((request.getAttribute(SandboxFilter.SANDBOX_CONTEXT) != null)
				&& (Boolean) request.getAttribute(SandboxFilter.SANDBOX_CONTEXT)) {
			return IRepositoryPaths.SANDBOX_DEPLOY_PATH + ICommonConstants.SEPARATOR + RepositoryFacade.getUser(request)
					+ getContentFolder() + requestPath;
		}
		return IRepositoryPaths.REGISTRY_DEPLOY_PATH + getContentFolder() + requestPath;
	}

	protected String getWebRegistryPath(HttpServletRequest request) throws IllegalArgumentException {
		if ((request.getAttribute(SandboxFilter.SANDBOX_CONTEXT) != null)
				&& (Boolean) request.getAttribute(SandboxFilter.SANDBOX_CONTEXT)) {
			return IRepositoryPaths.SANDBOX_DEPLOY_PATH + ICommonConstants.SEPARATOR + RepositoryFacade.getUser(request)
					+ getContentFolder();
		}
		return IRepositoryPaths.REGISTRY_DEPLOY_PATH + getContentFolder();
	}

	protected String getContentFolder() {
		return CONTENT_FOLDER;
	}

	protected byte[] preprocessContent(byte[] rawContent, IEntity entity) throws IOException {
		return rawContent;
	}

	public IScriptExecutor createExecutor(HttpServletRequest request) throws IOException {
		return new MobileExecutor(getRepository(request), getWebRegistryPath(request),
				IRepositoryPaths.REGISTRY_DEPLOY_PATH + getContentFolder());
	}
}
