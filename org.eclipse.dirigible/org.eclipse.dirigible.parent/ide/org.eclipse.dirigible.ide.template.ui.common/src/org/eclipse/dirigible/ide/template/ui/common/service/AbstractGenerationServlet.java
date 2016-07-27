/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.common.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.repository.api.IRepository;

/**
 * Abstract Servlet for Template Generation Services
 */
public abstract class AbstractGenerationServlet extends HttpServlet {

	private static final long serialVersionUID = -9115022531455267478L;

	private static final String COULD_NOT_INITIALIZE_REPOSITORY = "Could not initialize Repository"; //$NON-NLS-1$

	private static final String COULD_NOT_INITIALIZE_WORKSPACE = "Could not initialize Workspace"; //$NON-NLS-1$

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.service(request, response);
	}

	private IRepository getRepository() throws ServletException {
		try {
			final IRepository repository = RepositoryFacade.getInstance().getRepository();
			return repository;
		} catch (Exception ex) {
			throw new ServletException(COULD_NOT_INITIALIZE_REPOSITORY, ex);
		}
	}

	private IWorkspace getWorkspace() throws ServletException {
		try {
			final IWorkspace workspace = WorkspaceLocator.getWorkspace();
			return workspace;
		} catch (Exception ex) {
			throw new ServletException(COULD_NOT_INITIALIZE_WORKSPACE, ex);
		}
	}

	protected abstract void doGeneration(String parameters, HttpServletRequest request) throws GenerationException;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String parameters = IOUtils.toString(req.getInputStream());
			doGeneration(parameters, req);
		} catch (GenerationException e) {
			throw new ServletException(e);
		}
	}

}
