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
import java.util.Map;

import javax.script.Bindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.AbstractScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptExecutor;
import org.mozilla.javascript.ScriptableObject;

public class JavaScriptExecutor extends AbstractScriptExecutor implements IJavaScriptExecutor {

	private static final Logger logger = Logger.getLogger(JavaScriptExecutor.class);

	private IRepository repository;
	private String[] rootPaths;

	public JavaScriptExecutor(IRepository repository, String... rootPaths) {
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
	public IRepository getRepository() {
		return repository;
	}

	@Override
	public String[] getRootPaths() {
		return rootPaths;
	}

	@Override
	public Object executeServiceModule(HttpServletRequest request, HttpServletResponse response, Object input, String module,
			Map<Object, Object> executionContext) throws IOException {

		IJavaScriptEngineExecutor javascriptEngineExecutor = null;
		try {
			javascriptEngineExecutor = JavaScriptActivator.createExecutor("rhino", this);
		} catch (Throwable t) {
			logger.error(t.getMessage());
		}
		if (javascriptEngineExecutor == null) {
			javascriptEngineExecutor = (IJavaScriptEngineExecutor) request.getAttribute("IJavaScriptEngineExecutor");
		}
		return javascriptEngineExecutor.executeServiceModule(request, response, input, module, executionContext);

	}

	@Override
	public void beforeExecution(HttpServletRequest request, HttpServletResponse response, String module, Object context) {
	}

	@Override
	protected void registerDefaultVariable(Object scope, String name, Object value) {
		if (scope instanceof ScriptableObject) {
			ScriptableObject local = (ScriptableObject) scope;
			local.put(name, local, value);
		} else if (scope instanceof Bindings) {
			Bindings local = (Bindings) scope;
			local.put(name, value);
		}
	}

	@Override
	protected String getModuleType(String path) {
		return ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;
	}

}
