/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.js;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.ext.debug.DebugManager;
import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.js.debug.WebSocketDebugSessionServletInternal;
import org.eclipse.dirigible.runtime.scripting.AbstractScriptingServlet;
import org.mozilla.javascript.Undefined;

/**
 * Servlet for JavaScript scripts execution
 */
public class JavaScriptServlet extends AbstractScriptingServlet {

	private static final long serialVersionUID = -9115022531455267478L;

	private static final Logger logger = Logger.getLogger(JavaScriptServlet.class.getCanonicalName());

	@Override
	protected void doExecution(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String module = request.getPathInfo();

		JavaScriptExecutor executor = createExecutor(request);
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		try {
			Object result = executor.executeServiceModule(request, response, module, executionContext);
			
			if ((result != null) && !(result instanceof Undefined)) {
				response.getWriter().println(result);
			}
			
			postExecution(request);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}

	protected void postExecution(HttpServletRequest request) {
		//
	}

	public JavaScriptExecutor createExecutor(HttpServletRequest request) throws IOException {
		JavaScriptExecutor executor = new JavaScriptExecutor(getRepository(request), getScriptingRegistryPath(request),
				REGISTRY_SCRIPTING_DEPLOY_PATH);
		return executor;
	}

}
