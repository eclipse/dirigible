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
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.AbstractScriptExecutor;

public class JavaScriptExecutor extends AbstractScriptExecutor {

	private static final String JAVA_SCRIPT_MODULE_NAME_CANNOT_BE_NULL = Messages
			.getString("JavaScriptExecutor.JAVA_SCRIPT_MODULE_NAME_CANNOT_BE_NULL"); //$NON-NLS-1$
	
	private static final Logger logger = Logger.getLogger(JavaScriptExecutor.class);

	private IRepository repository;
	private String[] rootPaths;

	public JavaScriptExecutor(IRepository repository, String... rootPaths) {
		super();
		logger.debug("entering: constructor()");
		this.repository = repository;
		this.rootPaths = rootPaths;
		if (this.rootPaths == null || this.rootPaths.length == 0) {
			this.rootPaths = new String[] { null, null };
		}
		logger.debug("exiting: constructor()");
	}

	@Override
	public Object executeServiceModule(HttpServletRequest request, HttpServletResponse response,
			Object input, String module, Map<Object, Object> executionContext) throws IOException {

		logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module=" + module); //$NON-NLS-1$
		
		if (module == null) {
			throw new IOException(JAVA_SCRIPT_MODULE_NAME_CANNOT_BE_NULL);
		}

		ModuleSourceProvider sourceProvider = createRepositoryModuleSourceProvider();
		ModuleScriptProvider scriptProvider = new SoftCachingModuleScriptProvider(sourceProvider);
		RequireBuilder builder = new RequireBuilder();
		builder.setModuleScriptProvider(scriptProvider);
		builder.setSandboxed(false);

		Object result = null;

		Context context = Context.enter();
		try {
			context.setLanguageVersion(Context.VERSION_1_2);
			context.getWrapFactory().setJavaPrimitiveWrap(false);
			Scriptable topLevelScope = context.initStandardObjects();
			Require require = builder.createRequire(context, topLevelScope);

			require.install(topLevelScope);

			registerDefaultVariables(request, response, input, executionContext, repository,
					topLevelScope);

			beforeExecution(request, response, module, context);

			try {
				ModuleSource moduleSource = sourceProvider.loadSource(module, null, null);
				result = context.evaluateReader(topLevelScope, moduleSource.getReader(), module, 0,
						null);
			} catch (URISyntaxException e) {
				throw new IOException(e.getMessage(), e);
			}

		} finally {
			Context.exit();
		}
		
		logger.debug("exiting: executeServiceModule()");
		return result;
	}

	protected void beforeExecution(HttpServletRequest request, HttpServletResponse response,
			String module, Context context) {
	}

	private RepositoryModuleSourceProvider createRepositoryModuleSourceProvider() {
		RepositoryModuleSourceProvider repositoryModuleSourceProvider = null;
		repositoryModuleSourceProvider = new RepositoryModuleSourceProvider(this, repository,
				rootPaths);
		return repositoryModuleSourceProvider;
	}

	@Override
	protected void registerDefaultVariable(Object scope, String name, Object value) {
		if (scope instanceof ScriptableObject) {
			ScriptableObject local = (ScriptableObject) scope;
			local.put(name, local, value);
		}
	}
	
	@Override
	protected String getModuleType(String path) {
		return ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;
	}

}
