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

package org.eclipse.dirigible.runtime.job;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.AbstractScriptingServlet;

/**
 * Servlet for JavaScript scripts execution
 */
public class JobSyncServlet extends AbstractScriptingServlet {

	private static final long serialVersionUID = -9115022531455267478L;

	private static final Logger logger = Logger.getLogger(JobSyncServlet.class
			.getCanonicalName());

	protected void doExecution(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String module = request.getPathInfo();

		JobSyncExecutor executor = createExecutor(request);
		try {
			Map<Object, Object> executionContext = new HashMap<Object, Object>();
			executor.executeServiceModule(request, response, module, executionContext);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
		}

	}

	public JobSyncExecutor createExecutor(HttpServletRequest request) throws IOException {
		JobSyncExecutor executor = new JobSyncExecutor(getRepository(request),
				getScriptingRegistryPath(request), REGISTRY_INTEGRATION_DEPLOY_PATH);
		return executor;
	}

}
