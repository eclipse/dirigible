/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.js.debug;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.runtime.filter.SandboxFilter;
import org.eclipse.dirigible.runtime.registry.PathUtils;
import org.eclipse.dirigible.runtime.registry.RegistryServlet;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class JavaScriptSourceRegistryServlet extends RegistryServlet {

	private static final String SCRIPTING_CONTENT = IRepositoryPaths.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;

	@Override
	protected String extractRepositoryPath(HttpServletRequest request) throws IllegalArgumentException {
		String requestPath = PathUtils.extractPath(request);
		if ((request.getAttribute(SandboxFilter.SANDBOX_CONTEXT) != null) && (Boolean) request.getAttribute(SandboxFilter.SANDBOX_CONTEXT)) {
			return IRepositoryPaths.SANDBOX_DEPLOY_PATH + ICommonConstants.SEPARATOR + RepositoryFacade.getUser(request) + getContentFolder()
					+ requestPath;
		}
		return IRepositoryPaths.REGISTRY_DEPLOY_PATH + getContentFolder() + requestPath;
	}

	protected String getContentFolder() {
		return SCRIPTING_CONTENT;
	}

	@Override
	protected byte[] buildResourceData(IEntity entity, HttpServletRequest request, HttpServletResponse response) throws IOException {

		byte[] rawContent = super.buildResourceData(entity, request, response);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// put the content
		outputStream.write(preprocessContent(rawContent, entity));

		outputStream.flush();
		return outputStream.toByteArray();
	}

	protected byte[] preprocessContent(byte[] rawContent, IEntity entity) throws IOException {
		return rawContent;
	}

}
