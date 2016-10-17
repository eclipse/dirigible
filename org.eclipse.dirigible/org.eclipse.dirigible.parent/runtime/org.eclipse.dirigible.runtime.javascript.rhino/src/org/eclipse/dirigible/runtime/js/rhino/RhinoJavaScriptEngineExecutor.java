package org.eclipse.dirigible.runtime.js.rhino;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptExecutor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;

public class RhinoJavaScriptEngineExecutor implements IJavaScriptEngineExecutor {

	private static final Logger logger = Logger.getLogger(RhinoJavaScriptEngineExecutor.class);

	private IJavaScriptExecutor javaScriptExecutor;

	public RhinoJavaScriptEngineExecutor(IJavaScriptExecutor javaScriptExecutor) {
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

			topLevelScope.put(IJavaScriptEngineExecutor.JS_ENGINE_TYPE, topLevelScope, IJavaScriptEngineExecutor.JS_TYPE_RHINO);

			this.javaScriptExecutor.registerDefaultVariables(request, response, input, executionContext, this.javaScriptExecutor.getRepository(),
					topLevelScope);

			this.javaScriptExecutor.beforeExecution(request, response, module, context);

			try {
				ModuleSource moduleSource = sourceProvider.loadSource(module, null, null);
				try {
					result = context.evaluateReader(topLevelScope, moduleSource.getReader(), module, 0, null);
				} catch (EcmaError e) {
					if ((e.getMessage() != null) && e.getMessage().contains(IJavaScriptExecutor.EXPORTS_ERR)) {
						result = IJavaScriptExecutor.REQUESTED_ENDPOINT_IS_NOT_A_SERVICE_BUT_RATHER_A_LIBRARY;
						logger.error(e.getMessage());

					} else {
						logger.error(e.getMessage(), e);
					}
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
				}
			} catch (URISyntaxException e) {
				throw new IOException(e.getMessage(), e);
			}

		} finally {
			Context.exit();
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
