/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.flow;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.AbstractScriptingServlet;

/**
 * Servlet for Flow process execution
 */
public class FlowServlet extends AbstractScriptingServlet {

	private static final long serialVersionUID = -9115022531455267478L;

	private static final Logger logger = Logger.getLogger(FlowServlet.class.getCanonicalName());

	@Override
	protected void doExecution(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String module = request.getPathInfo();

		FlowExecutor executor = createExecutor(request);
		try {
			Map<Object, Object> executionContext = new HashMap<Object, Object>();
			Object result = executor.executeServiceModule(request, response, module, executionContext);
			if ((result != null) && !response.isCommitted()) {
				response.getWriter().print(result.toString());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
		}

	}

	public FlowExecutor createExecutor(HttpServletRequest request) throws IOException {
		FlowExecutor executor = new FlowExecutor(getRepository(request), getScriptingRegistryPath(request), REGISTRY_INTEGRATION_DEPLOY_PATH);
		return executor;
	}

}
