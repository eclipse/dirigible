/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.v8.processor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.engine.js.api.ResourcePath;
import org.eclipse.dirigible.engine.js.v8.callbacks.JavaV8CallInstance;
import org.eclipse.dirigible.engine.js.v8.callbacks.JavaV8CallStatic;
import org.eclipse.dirigible.engine.js.v8.callbacks.JavaV8NewInstance;
import org.eclipse.dirigible.engine.js.v8.callbacks.Require;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

/**
 * The V8 Javascript Engine Executor.
 */
public class V8JavascriptEngineExecutor extends AbstractJavascriptExecutor {

	private static final String J2V8_CALL_STATIC_FUNCTION_NAME = "j2v8call";

	private static final String J2V8_NEW_INSTANCE_FUNCTION_NAME = "j2v8instantiate";

	private static final String J2V8_CALL_INSTANCE_FUNCTION_NAME = "j2v8invoke";

	private static final Logger logger = LoggerFactory.getLogger(V8JavascriptEngineExecutor.class);

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceModule(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
		return executeService(module, executionContext, true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceCode(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
		return executeService(code, executionContext, false);
	}

	/**
	 * Execute service.
	 *
	 * @param moduleOrCode
	 *            the module or code
	 * @param executionContext
	 *            the execution context
	 * @param isModule
	 *            the is module
	 * @return the object
	 * @throws ScriptingException
	 *             the scripting exception
	 */
	public Object executeService(String moduleOrCode, Map<Object, Object> executionContext, boolean isModule) throws ScriptingException {

		logger.trace("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.trace("module or code=" + moduleOrCode); //$NON-NLS-1$

		if (moduleOrCode == null) {
			throw new ScriptingException("JavaScript module name cannot be null");
		}

		if (isModule) {
			ResourcePath resourcePath = getResourcePath(moduleOrCode, MODULE_EXT_JS, MODULE_EXT_V8);
			moduleOrCode = resourcePath.getModule();
			if (HttpRequestFacade.isValid()) {
				HttpRequestFacade.setAttribute(HttpRequestFacade.ATTRIBUTE_REST_RESOURCE_PATH, resourcePath.getPath());
			}
		}

		Object result = null;

		V8RepositoryModuleSourceProvider sourceProvider = createRepositoryModuleSourceProvider();
		V8 v8 = V8.createV8Runtime();
		try {
			v8.add("engine", IJavascriptEngineExecutor.JAVASCRIPT_TYPE_V8);
			v8.registerJavaMethod(new JavaV8CallStatic(), J2V8_CALL_STATIC_FUNCTION_NAME);
			v8.registerJavaMethod(new JavaV8NewInstance(), J2V8_NEW_INSTANCE_FUNCTION_NAME);
			v8.registerJavaMethod(new JavaV8CallInstance(), J2V8_CALL_INSTANCE_FUNCTION_NAME);
			v8.registerJavaMethod(new JavaCallback() {

				@Override
				public Object invoke(V8Object receiver, V8Array parameters) {
					Object modulename = parameters.get(0);
					try {
						return sourceProvider.loadSource((String) modulename);
					} catch (IOException | URISyntaxException e) {
						throw new RuntimeException(e);
					}
				}
			}, "_j2v8loadSource");
			v8.executeScript(Require.CODE);
			v8.executeScript("var console = require('core/v3/console')");
			String source = (isModule ? sourceProvider.loadSource(moduleOrCode) : moduleOrCode);
			if (isModule) {
				result = v8.executeScript(source);
			} else {
				result = v8.executeScript(source, moduleOrCode, 0);
			}
			forceFlush();
		} catch (Exception e) {
			throw new ScriptingException(e);
		} finally {
			v8.release();
			if (result instanceof Releasable) {
				((Releasable) result).release();
			}
		}

		logger.trace("exiting: executeServiceModule()");

		return result;
	}

	/**
	 * Creates the repository module source provider.
	 *
	 * @return the v 8 repository module source provider
	 */
	private V8RepositoryModuleSourceProvider createRepositoryModuleSourceProvider() {
		V8RepositoryModuleSourceProvider repositoryModuleSourceProvider = null;
		repositoryModuleSourceProvider = new V8RepositoryModuleSourceProvider(this, IRepositoryStructure.PATH_REGISTRY_PUBLIC);
		return repositoryModuleSourceProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#getType()
	 */
	@Override
	public String getType() {
		return JAVASCRIPT_TYPE_V8;
	}
}
