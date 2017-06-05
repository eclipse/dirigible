package org.eclipse.dirigible.engine.js.rhino.processor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.ScriptingDependencyException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.api.AbstractScriptExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.mozilla.javascript.Context;
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

public class RhinoJavascriptEngineExecutor extends AbstractScriptExecutor implements IJavascriptEngineExecutor {

	private static final Logger logger = LoggerFactory.getLogger(RhinoJavascriptEngineExecutor.class);
	
	@Override
	public void executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {

		logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module=" + module); //$NON-NLS-1$

		if (module == null) {
			throw new ScriptingException("JavaScript module name cannot be null");
		}

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

			topLevelScope.put(IJavascriptEngineExecutor.JS_ENGINE_TYPE, topLevelScope, IJavascriptEngineExecutor.JS_TYPE_RHINO);

			try {
				ModuleSource moduleSource = sourceProvider.loadSource(module, null, null);
				try {
					context.evaluateReader(topLevelScope, moduleSource.getReader(), module, 0, null);
				} catch (EcmaError e) {
					logger.error(e.getMessage());
					if ((e.getMessage() != null) && e.getMessage().contains("\"exports\" is not defined")) {
						throw new ScriptingDependencyException("Requested endpoint is not a service, but rather a library.");
					} else {
						throw new ScriptingException(e);
					}
				}
			} catch (URISyntaxException | IOException e) {
				throw new ScriptingException(e);
			}
		} finally {
			Context.exit();
		}

		logger.debug("exiting: executeServiceModule()");
	}
	
	private RhinoRepositoryModuleSourceProvider createRepositoryModuleSourceProvider() {
		RhinoRepositoryModuleSourceProvider repositoryModuleSourceProvider = null;
		repositoryModuleSourceProvider = new RhinoRepositoryModuleSourceProvider(this, IRepositoryStructure.REGISTRY_PUBLIC);
		return repositoryModuleSourceProvider;
	}

}
