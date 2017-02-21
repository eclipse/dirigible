/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.filter.SandboxFilter;
import org.eclipse.dirigible.runtime.registry.PathUtils;
import org.eclipse.dirigible.runtime.registry.RegistryServlet;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;

public class WebRegistryServlet extends RegistryServlet {

	private static final String WEB_CONTENT = IRepositoryPaths.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;
	protected static final String PARAMETER_LIST = "list"; //$NON-NLS-1$

	private static final long serialVersionUID = -1484072696377972535L;

	@Override
	protected String extractRepositoryPath(HttpServletRequest request) throws IllegalArgumentException {
		String requestPath = PathUtils.extractPath(request);
		if ((request.getAttribute(SandboxFilter.SANDBOX_CONTEXT) != null) && (Boolean) request.getAttribute(SandboxFilter.SANDBOX_CONTEXT)) {
			return IRepositoryPaths.SANDBOX_DEPLOY_PATH + ICommonConstants.SEPARATOR + RepositoryFacade.getUser(request) + getContentFolder()
					+ requestPath;
		}
		return IRepositoryPaths.REGISTRY_DEPLOY_PATH + getContentFolder() + requestPath;
	}

	protected String getWebRegistryPath(HttpServletRequest request) throws IllegalArgumentException {
		if ((request.getAttribute(SandboxFilter.SANDBOX_CONTEXT) != null) && (Boolean) request.getAttribute(SandboxFilter.SANDBOX_CONTEXT)) {
			return IRepositoryPaths.SANDBOX_DEPLOY_PATH + ICommonConstants.SEPARATOR + RepositoryFacade.getUser(request) + getContentFolder();
		}
		return IRepositoryPaths.REGISTRY_DEPLOY_PATH + getContentFolder();
	}

	protected String getContentFolder() {
		return WEB_CONTENT;
	}

	@Override
	protected byte[] buildResourceData(IEntity entity, HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] rawContent = retrieveResourceData(entity, request, response);
		boolean list = (request.getParameter(PARAMETER_LIST) != null);

		if (list) {
			// list parameter is present - return JSON formatted content
			return super.buildResourceData(entity, request, response);
		}

		// it is *.html and it is NOT index.html
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// put the content
		outputStream.write(preprocessContent(rawContent, entity));

		outputStream.flush();
		return outputStream.toByteArray();
	}

	protected byte[] preprocessContent(byte[] rawContent, IEntity entity) throws IOException {
		return rawContent;
	}

	@Override
	protected byte[] buildCollectionData(boolean deep, IEntity entity, String collectionPath) throws IOException {
		byte[] data;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(baos);
		// lookup for index.html
		IResource index = entity.getParent().getResource(INDEX_HTML);
		if (index.exists()) {
			// start with index
			writer.print(new String(index.getContent()));
		} else {
			return super.buildCollectionData(deep, entity, collectionPath);
		}
		writer.flush();
		data = baos.toByteArray();
		return preprocessContent(data, entity);
	}

	public IScriptExecutor createExecutor(HttpServletRequest request) throws IOException {
		WebExecutor executor = new WebExecutor(getRepository(request), getWebRegistryPath(request),
				IRepositoryPaths.REGISTRY_DEPLOY_PATH + getContentFolder());
		return executor;
	}
}
