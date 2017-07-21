package org.eclipse.dirigible.engine.js.v8.processor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.engine.js.v8.callbacks.JavaV8Call;
import org.eclipse.dirigible.engine.js.v8.callbacks.Require;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

public class V8JavascriptEngineExecutor extends AbstractJavascriptExecutor {

	private static final String J2V8CALL_FUNCTION_NAME = "j2v8call";
	private static final Logger logger = LoggerFactory.getLogger(V8JavascriptEngineExecutor.class);

	@Override
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {

		logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module=" + module); //$NON-NLS-1$

		if (module == null) {
			throw new ScriptingException("JavaScript module name cannot be null");
		}

		Object result = null;

		V8RepositoryModuleSourceProvider sourceProvider = createRepositoryModuleSourceProvider();
		V8 v8 = V8.createV8Runtime();
		try {
			v8.add("engine", IJavascriptEngineExecutor.JS_TYPE_V8);
			v8.registerJavaMethod(new JavaV8Call(), J2V8CALL_FUNCTION_NAME);
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
			String source = sourceProvider.loadSource(module);
			result = v8.executeScript(source);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			v8.release();
			if(result instanceof Releasable){
				((Releasable) result).release();
			}
		}

		logger.debug("exiting: executeServiceModule()");

		return result;
	}

	private V8RepositoryModuleSourceProvider createRepositoryModuleSourceProvider() {
		V8RepositoryModuleSourceProvider repositoryModuleSourceProvider = null;
		repositoryModuleSourceProvider = new V8RepositoryModuleSourceProvider(this,
				IRepositoryStructure.REGISTRY_PUBLIC);
		return repositoryModuleSourceProvider;
	}

	@Override
	public String getType() {
		return JS_TYPE_V8;
	}
}
