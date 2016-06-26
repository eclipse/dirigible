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
import org.eclipse.dirigible.runtime.js.nashorn.commonjs.ISourceProvider;
import org.eclipse.dirigible.runtime.js.nashorn.commonjs.Require;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptExecutor;

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
			ISourceProvider provider = createRepositoryModuleSourceProvider();
			Require.enable(engine, provider);

			Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

			this.javaScriptExecutor.registerDefaultVariables(request, response, input, executionContext, this.javaScriptExecutor.getRepository(),
					bindings);

			this.javaScriptExecutor.beforeExecution(request, response, module, bindings);

			String code = provider.loadSource(module);

			try {
				result = engine.eval(code);
			} catch (ScriptException e) {
				if ((e.getMessage() != null) && e.getMessage().contains(IJavaScriptExecutor.EXPORTS_ERR)) {
					result = IJavaScriptExecutor.REQUESTED_ENDPOINT_IS_NOT_A_SERVICE_BUT_RATHER_A_LIBRARY;
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

	private NashornRepositoryModuleSourceProvider createRepositoryModuleSourceProvider() {
		NashornRepositoryModuleSourceProvider repositoryModuleSourceProvider = null;
		repositoryModuleSourceProvider = new NashornRepositoryModuleSourceProvider(this.javaScriptExecutor, this.javaScriptExecutor.getRepository(),
				this.javaScriptExecutor.getRootPaths());
		return repositoryModuleSourceProvider;
	}

}
