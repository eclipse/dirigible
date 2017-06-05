package org.eclipse.dirigible.engine.js.nashorn.processor;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.dirigible.commons.api.scripting.ScriptingDependencyException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.api.AbstractScriptExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.nashorn.api.scripting.NashornScriptEngine;

public class NashornJavascriptEngineExecutor extends AbstractScriptExecutor implements IJavascriptEngineExecutor {

	private static final Logger logger = LoggerFactory.getLogger(NashornJavascriptEngineExecutor.class);
	
	@Override
	public void executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {

		logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module=" + module); //$NON-NLS-1$

		if (module == null) {
			throw new ScriptingException("JavaScript module name cannot be null");
		}

		Object result = null;

		ScriptEngineManager engineManager = new ScriptEngineManager();
		NashornScriptEngine engine = (NashornScriptEngine) engineManager.getEngineByName("nashorn");

		try {
			NashornRepositoryModuleSourceProvider sourceProvider = createRepositoryModuleSourceProvider();
			Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("SourceProvider", sourceProvider);

			bindings.put(IJavascriptEngineExecutor.JS_ENGINE_TYPE, IJavascriptEngineExecutor.JS_TYPE_NASHORN);

			String code = sourceProvider.loadSource(module);

			try {
				engine.eval(Require.CODE);
				engine.eval("load(\"nashorn:mozilla_compat.js\");");
				result = engine.eval(code);
			} catch (ScriptException e) {
				if ((e.getMessage() != null) && e.getMessage().contains("\"exports\" is not defined")) {
					throw new ScriptingDependencyException("Requested endpoint is not a service, but rather a library.");
				} else {
					throw new ScriptingException(e);
				}
			}
		} catch (Throwable e) {
			throw new ScriptingException(e);
		}

		logger.debug("exiting: executeServiceModule()");

	}
	
	private NashornRepositoryModuleSourceProvider createRepositoryModuleSourceProvider() {
		NashornRepositoryModuleSourceProvider repositoryModuleSourceProvider = null;
		repositoryModuleSourceProvider = new NashornRepositoryModuleSourceProvider(this, IRepositoryStructure.REGISTRY_PUBLIC);
		return repositoryModuleSourceProvider;
	}

}
