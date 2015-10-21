/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.sql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.AbstractScriptingServlet;

public class SQLServlet extends AbstractScriptingServlet {

	private static final long serialVersionUID = -2029496922201773270L;

	private static final Logger logger = Logger.getLogger(SQLServlet.class);

	private File libDirectory;

	private static String classpath;

	@Override
	protected void doExecution(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String module = request.getPathInfo();

		SQLExecutor executor = createExecutor(request);
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		try {
			response.setContentType("application/json;charset=UTF-8");
			String result = (String) executor.executeServiceModule(request, response, module, executionContext);
			response.getWriter().println(result);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	public SQLExecutor createExecutor(HttpServletRequest request) throws IOException {
		SQLExecutor executor = new SQLExecutor(getRepository(request), getScriptingRegistryPath(request), REGISTRY_SCRIPTING_DEPLOY_PATH);
		return executor;
	}
}
