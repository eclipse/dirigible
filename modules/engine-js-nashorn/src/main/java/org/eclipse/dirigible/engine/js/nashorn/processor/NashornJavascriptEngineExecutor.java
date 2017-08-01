package org.eclipse.dirigible.engine.js.nashorn.processor;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.dirigible.api.v3.core.ConsoleFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingDependencyException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NashornJavascriptEngineExecutor extends AbstractJavascriptExecutor {

	private static final Logger logger = LoggerFactory.getLogger(NashornJavascriptEngineExecutor.class);

	@Override
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {

		logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module=" + module); //$NON-NLS-1$

		if (module == null) {
			throw new ScriptingException("JavaScript module name cannot be null");
		}

		Object result = null;

		ScriptEngineManager engineManager = new ScriptEngineManager();
		// NashornScriptEngine engine = (NashornScriptEngine) engineManager.getEngineByName("nashorn");
		ScriptEngine engine = engineManager.getEngineByName("nashorn");

		try {
			NashornRepositoryModuleSourceProvider sourceProvider = createRepositoryModuleSourceProvider();
			Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("SourceProvider", sourceProvider);

			bindings.put(IJavascriptEngineExecutor.JS_ENGINE_TYPE, IJavascriptEngineExecutor.JS_TYPE_NASHORN);
			bindings.put(IJavascriptEngineExecutor.CONSOLE, ConsoleFacade.getConsole());

			String code = sourceProvider.loadSource(module);

			try {
				engine.eval(Require.CODE);
				engine.eval("load(\"nashorn:mozilla_compat.js\");");
				result = engine.eval(code);
			} catch (ScriptException e) {
				if ((e.getMessage() != null) && e.getMessage().contains("\"exports\" is not defined")) {
					throw new ScriptingDependencyException("Requested endpoint is not a service, but rather a library.");
				}
				throw new ScriptingException(e);
			}
		} catch (Throwable e) {
			throw new ScriptingException(e);
		}

		logger.debug("exiting: executeServiceModule()");

		return result;

	}

	private NashornRepositoryModuleSourceProvider createRepositoryModuleSourceProvider() {
		NashornRepositoryModuleSourceProvider repositoryModuleSourceProvider = null;
		repositoryModuleSourceProvider = new NashornRepositoryModuleSourceProvider(this, IRepositoryStructure.PATH_REGISTRY_PUBLIC);
		return repositoryModuleSourceProvider;
	}

	@Override
	public String getType() {
		return JS_TYPE_NASHORN;
	}
}
