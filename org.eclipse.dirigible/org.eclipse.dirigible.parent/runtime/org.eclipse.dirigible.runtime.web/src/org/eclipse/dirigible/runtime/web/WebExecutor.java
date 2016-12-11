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

import java.io.IOException;
import java.util.Map;

import javax.naming.Context;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.AbstractScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.Module;

public class WebExecutor extends AbstractScriptExecutor {

	private static final Logger logger = Logger.getLogger(WebExecutor.class);

	private IRepository repository;
	private String[] rootPaths;

	public WebExecutor(IRepository repository, String... rootPaths) {
		super();
		logger.debug("entering: constructor()");
		this.repository = repository;
		this.rootPaths = rootPaths;
		if ((this.rootPaths == null) || (this.rootPaths.length == 0)) {
			this.rootPaths = new String[] { null, null };
		}
		logger.debug("exiting: constructor()");
	}

	@Override
	public Object executeServiceModule(HttpServletRequest request, HttpServletResponse response, Object input, String module,
			Map<Object, Object> executionContext) throws IOException {

		logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module=" + module); //$NON-NLS-1$

		if (module == null) {
			throw new IOException("Web module cannot be null");
		}

		Module scriptingModule = retrieveModule(this.repository, module, null, this.rootPaths);
		byte[] result = scriptingModule.getContent();

		result = preprocessContent(result, getResource(repository, scriptingModule.getPath()));

		response.getWriter().print(new String(result, ICommonConstants.UTF8));
		response.getWriter().flush();
		logger.debug("exiting: executeServiceModule()");
		return result;
	}

	protected byte[] preprocessContent(byte[] rawContent, IEntity entity) throws IOException {
		return rawContent;
	}

	protected byte[] buildResourceData(final IEntity entity, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		byte[] data = null;
		data = readResourceData((IResource) entity);
		return data;
	}

	protected byte[] readResourceData(IResource resource) throws IOException {
		return resource.getContent();
	}

	protected void beforeExecution(HttpServletRequest request, HttpServletResponse response, String module, Context context) {
	}

	@Override
	protected void registerDefaultVariable(Object scope, String name, Object value) {
		//
	}

	@Override
	protected String getModuleType(String path) {
		return ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;
	}

}
