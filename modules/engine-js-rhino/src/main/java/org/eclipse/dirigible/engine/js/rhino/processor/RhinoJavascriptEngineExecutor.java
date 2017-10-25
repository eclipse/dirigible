package org.eclipse.dirigible.engine.js.rhino.processor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.dirigible.api.v3.core.ConsoleFacade;
import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingDependencyException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.engine.js.api.ResourcePath;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RhinoJavascriptEngineExecutor extends AbstractJavascriptExecutor {

	private static final Logger logger = LoggerFactory.getLogger(RhinoJavascriptEngineExecutor.class);

	static {
		ContextFactory.initGlobal(new ContextFactory() {
			@Override
			protected boolean hasFeature(Context cx, int featureIndex) {
				if (featureIndex == Context.FEATURE_LOCATION_INFORMATION_IN_ERROR) {
					return true;
				}
				return super.hasFeature(cx, featureIndex);
			}
		});
		// RhinoException.setStackStyle(StackStyle.V8);
	}

	@Override
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
		return executeService(module, executionContext, true);
	}

	@Override
	public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
		return executeService(code, executionContext, false);
	}

	public Object executeService(String moduleOrCode, Map<Object, Object> executionContext, boolean isModule) throws ScriptingException {

		logger.trace("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module or code=" + moduleOrCode); //$NON-NLS-1$

		if (moduleOrCode == null) {
			throw new ScriptingException("JavaScript module name cannot be null");
		}

		if (isModule) {
			ResourcePath resourcePath = getResourcePath(moduleOrCode, MODULE_EXT_JS, MODULE_EXT_RHINO);
			moduleOrCode = resourcePath.getModule();
			if (HttpRequestFacade.isValid()) {
				HttpRequestFacade.setAttribute(HttpRequestFacade.ATTRIBUTE_REST_RESOURCE_PATH, resourcePath.getPath());
			}
		}

		Object result = null;

		ModuleSourceProvider sourceProvider = createRepositoryModuleSourceProvider();
		ModuleScriptProvider scriptProvider = new SoftCachingModuleScriptProvider(sourceProvider);
		RequireBuilder builder = new RequireBuilder();
		builder.setModuleScriptProvider(scriptProvider);
		builder.setSandboxed(false);

		Context context = Context.enter();
		try {
			context.setLanguageVersion(Context.VERSION_ES6);
			context.getWrapFactory().setJavaPrimitiveWrap(false);
			Scriptable topLevelScope = context.initStandardObjects();
			Require require = builder.createRequire(context, topLevelScope);

			require.install(topLevelScope);

			topLevelScope.put(IJavascriptEngineExecutor.JAVASCRIPT_ENGINE_TYPE, topLevelScope, IJavascriptEngineExecutor.JAVASCRIPT_TYPE_RHINO);
			topLevelScope.put(IJavascriptEngineExecutor.CONSOLE, topLevelScope, ConsoleFacade.getConsole());

			try {
				ModuleSource moduleSource = (isModule ? sourceProvider.loadSource(moduleOrCode, null, null) : null);
				try {
					if (moduleSource != null) {
						result = context.evaluateReader(topLevelScope, moduleSource.getReader(), moduleOrCode, 0, null);
					} else {
						result = context.evaluateString(topLevelScope, moduleOrCode, "dynamic", 0, null);
					}
					forceFlush();
				} catch (EcmaError e) {
					logger.error(e.getMessage());
					if ((e.getMessage() != null) && e.getMessage().contains("\"exports\" is not defined")) {
						throw new ScriptingDependencyException("Requested endpoint is not a service, but rather a library.");
					}
					throw new ScriptingException(e);
				}
			} catch (URISyntaxException | IOException e) {
				throw new ScriptingException(e);
			}
		} finally {
			Context.exit();
		}

		logger.trace("exiting: executeServiceModule()");

		return result;
	}

	private RhinoRepositoryModuleSourceProvider createRepositoryModuleSourceProvider() {
		RhinoRepositoryModuleSourceProvider repositoryModuleSourceProvider = null;
		repositoryModuleSourceProvider = new RhinoRepositoryModuleSourceProvider(this, IRepositoryStructure.PATH_REGISTRY_PUBLIC);
		return repositoryModuleSourceProvider;
	}

	@Override
	public String getType() {
		return JAVASCRIPT_TYPE_RHINO;
	}
}
