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

package org.eclipse.dirigible.runtime.scripting;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.runtime.filter.SandboxFilter;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

/**
 * Abstract Servlet for Scripting Engines
 */
public abstract class AbstractScriptingServlet extends HttpServlet {

	private static final String COULD_NOT_INITIALIZE_REPOSITORY = Messages.getString("AbstractScriptingServlet.COULD_NOT_INITIALIZE_REPOSITORY"); //$NON-NLS-1$

	private static final long serialVersionUID = -9115022531455267478L;

	public static final String REGISTRY_SCRIPTING_DEPLOY_PATH = IRepositoryPaths.REGISTRY_DEPLOY_PATH
			+ ICommonConstants.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES; //$NON-NLS-1$
	
	public static final String REGISTRY_INTEGRATION_DEPLOY_PATH = IRepositoryPaths.REGISTRY_DEPLOY_PATH
			+ ICommonConstants.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES; //$NON-NLS-1$
	
	public static String getSandboxScripting(HttpServletRequest request) {
		return IRepositoryPaths.SANDBOX_DEPLOY_PATH + ICommonConstants.SEPARATOR 
				+ RepositoryFacade.getUser(request) + ICommonConstants.SEPARATOR
				+ ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES; //$NON-NLS-1$
	}

	public static final String REPOSITORY_ATTRIBUTE = "org.eclipse.dirigible.services.scripting.repository"; //$NON-NLS-1$

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		initRepository(request);
//		new ContentInitializerServlet().initDefaultContent(request);
		super.service(request, response);
	}

	private IRepository initRepository(HttpServletRequest request)
			throws ServletException {
		try {
			final IRepository repository = RepositoryFacade.getInstance()
					.getRepository(request);
			if (request != null) {
				request.getSession().setAttribute(REPOSITORY_ATTRIBUTE, repository);
			}
			return repository;
//			getServletContext().setAttribute(REPOSITORY_ATTRIBUTE, repository);
		} catch (Exception ex) {
			throw new ServletException(COULD_NOT_INITIALIZE_REPOSITORY, ex);
		}
	}

	protected abstract void doExecution(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	protected String getScriptingRegistryPath(HttpServletRequest request) {
		if (request != null 
			&& (request.getAttribute(SandboxFilter.SANDBOX_CONTEXT) != null
				&& (Boolean) request.getAttribute(SandboxFilter.SANDBOX_CONTEXT)
				|| (request.getAttribute(SandboxFilter.DEBUG_CONTEXT) != null
				&& (Boolean) request.getAttribute(SandboxFilter.DEBUG_CONTEXT)))) {
			return getSandboxScripting(request);
		}
		return REGISTRY_SCRIPTING_DEPLOY_PATH;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doExecution(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doExecution(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doExecution(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doExecution(req, resp);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doExecution(req, resp);
	}

	protected IRepository getRepository(HttpServletRequest req)
			throws IOException {
		IRepository repository = null; 
		if (req != null) {
			repository = (IRepository) req.getSession().getAttribute(REPOSITORY_ATTRIBUTE);
		}
		if (repository == null) {
			try {
				repository = initRepository(req);
			} catch (Exception e) {
				log(e.getMessage(), e);
				throw new IOException(e);
			}
		}
		return repository;
	}

}
