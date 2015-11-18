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

package org.eclipse.dirigible.runtime.js;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.runtime.filter.SandboxFilter;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class TestCasesServlet extends JavaScriptServlet {

	private static final long serialVersionUID = 1274655492926255449L;

	public static final String TEST_CASES = "/TestCases"; //$NON-NLS-1$  

	public static final String REGISTRY_TESTS_DEPLOY_PATH = IRepositoryPaths.REGISTRY_DEPLOY_PATH
			+ TEST_CASES; //$NON-NLS-1$

	@Override
	protected String getScriptingRegistryPath(HttpServletRequest request) {
		if (request.getAttribute(SandboxFilter.SANDBOX_CONTEXT) != null
				&& (Boolean) request.getAttribute(SandboxFilter.SANDBOX_CONTEXT)) {
			return IRepositoryPaths.SANDBOX_DEPLOY_PATH + ICommonConstants.SEPARATOR
					+ RepositoryFacade.getUser(request) + TEST_CASES;
		}
		return REGISTRY_TESTS_DEPLOY_PATH;
	}

	@Override
	public JavaScriptExecutor createExecutor(HttpServletRequest request) throws IOException {
		JavaScriptExecutor executor = new JavaScriptExecutor(getRepository(request),
				getScriptingRegistryPath(request), super.getScriptingRegistryPath(request),
				REGISTRY_SCRIPTING_DEPLOY_PATH, REGISTRY_TESTS_DEPLOY_PATH);
		return executor;
	}

}
