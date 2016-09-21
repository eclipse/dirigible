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
import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Abstract Servlet for Template Generation Services
 */
public abstract class AbstractGenerationServlet extends HttpServlet {

	private static final long serialVersionUID = -9115022531455267478L;

	private static final Logger logger = Logger.getLogger(AbstractGenerationServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.service(request, response);
	}

	protected IRepository getRepository(HttpServletRequest request) throws ServletException {
		return GenerationUtils.getRepository(request);
	}

	protected IWorkspace getWorkspace(HttpServletRequest request) throws ServletException {
		return GenerationUtils.getWorkspace(request);
	}

	protected abstract String doGeneration(String parameters, HttpServletRequest request) throws GenerationException;

	protected abstract String enumerateTemplates(HttpServletRequest req) throws GenerationException;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String parameters = IOUtils.toString(req.getInputStream());
			String result = doGeneration(parameters, req);
			printResult(resp, result);
		} catch (GenerationException e) {
			logger.error(e.getMessage(), e);
			throw new ServletException(e);
		}
	}

	private void printResult(HttpServletResponse resp, String result) throws IOException {
		resp.getWriter().print(result);
		resp.getWriter().flush();
		resp.getWriter().close();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String result = enumerateTemplates(req);
			printResult(resp, result);
		} catch (GenerationException e) {
			logger.error(e.getMessage(), e);
			throw new ServletException(e);
		}
	}

}
