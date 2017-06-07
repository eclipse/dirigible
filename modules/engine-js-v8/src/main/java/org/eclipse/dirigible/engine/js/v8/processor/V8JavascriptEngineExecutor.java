package org.eclipse.dirigible.engine.js.v8.processor;

import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.api.AbstractScriptExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.v8.V8;

public class V8JavascriptEngineExecutor extends AbstractScriptExecutor implements IJavascriptEngineExecutor {

	private static final Logger logger = LoggerFactory.getLogger(V8JavascriptEngineExecutor.class);
	
	@Override
	public void executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {

		logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module=" + module); //$NON-NLS-1$

		if (module == null) {
			throw new ScriptingException("JavaScript module name cannot be null");
		}

//		ModuleSourceProvider sourceProvider = createRepositoryModuleSourceProvider();

		V8 v8 = V8.createV8Runtime();
		try {
			int x = (Integer) v8.executeScript("var func = x => x * x; func(5);");
			System.out.println(x);
		} finally {
			v8.release();
		}

		logger.debug("exiting: executeServiceModule()");
	}
	
	private V8RepositoryModuleSourceProvider createRepositoryModuleSourceProvider() {
		V8RepositoryModuleSourceProvider repositoryModuleSourceProvider = null;
		repositoryModuleSourceProvider = new V8RepositoryModuleSourceProvider(this, IRepositoryStructure.REGISTRY_PUBLIC);
		return repositoryModuleSourceProvider;
	}

}
