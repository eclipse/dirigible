/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.js.nashorn;

import java.io.IOException;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.ISourceProvider;
import org.eclipse.dirigible.runtime.scripting.RepositoryModuleSourceProvider;

import jdk.nashorn.api.scripting.NashornScriptEngine;

public class NashornJavaScriptEngineExecutor implements IJavaScriptEngineExecutor {

	private static final Logger logger = Logger.getLogger(NashornJavaScriptEngineExecutor.class);

	private IJavaScriptExecutor javaScriptExecutor;

	public NashornJavaScriptEngineExecutor(IJavaScriptExecutor javaScriptExecutor) {
		this.javaScriptExecutor = javaScriptExecutor;
	}

	@Override
	public Object executeServiceModule(HttpServletRequest request, HttpServletResponse response, Object input, String module,
			Map<Object, Object> executionContext) throws IOException {

		logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module=" + module); //$NON-NLS-1$

		if (module == null) {
			throw new IOException(IJavaScriptExecutor.JAVA_SCRIPT_MODULE_NAME_CANNOT_BE_NULL);
		}

		Object result = null;

		ScriptEngineManager engineManager = new ScriptEngineManager();
		NashornScriptEngine engine = (NashornScriptEngine) engineManager.getEngineByName("nashorn");

		try {
			ISourceProvider sourceProvider = createRepositoryModuleSourceProvider();
			Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("SourceProvider", sourceProvider);

			bindings.put(IJavaScriptEngineExecutor.JS_ENGINE_TYPE, IJavaScriptEngineExecutor.JS_TYPE_NASHORN);

			this.javaScriptExecutor.registerDefaultVariables(request, response, input, executionContext, this.javaScriptExecutor.getRepository(),
					bindings);

			this.javaScriptExecutor.beforeExecution(request, response, module, bindings);

			String code = sourceProvider.loadSource(module);

			try {
				engine.eval(Require.CODE);
				engine.eval("load(\"nashorn:mozilla_compat.js\");");
				result = engine.eval(code);
			} catch (ScriptException e) {
				if ((e.getMessage() != null) && e.getMessage().contains(IJavaScriptExecutor.EXPORTS_ERR)) {
					result = IJavaScriptExecutor.REQUESTED_ENDPOINT_IS_NOT_A_SERVICE_BUT_RATHER_A_LIBRARY;
					logger.error(e.getMessage());
				} else {
					logger.error(e.getMessage(), e);
				}
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("exiting: executeServiceModule()");
		return result;

	}

	private RepositoryModuleSourceProvider createRepositoryModuleSourceProvider() {
		RepositoryModuleSourceProvider repositoryModuleSourceProvider = null;
		repositoryModuleSourceProvider = new RepositoryModuleSourceProvider(this.javaScriptExecutor, this.javaScriptExecutor.getRepository(),
				this.javaScriptExecutor.getRootPaths());
		return repositoryModuleSourceProvider;
	}

}
