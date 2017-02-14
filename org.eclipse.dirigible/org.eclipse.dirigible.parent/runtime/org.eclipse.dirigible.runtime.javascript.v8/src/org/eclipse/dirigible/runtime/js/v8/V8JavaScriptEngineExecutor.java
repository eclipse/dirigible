package org.eclipse.dirigible.runtime.js.v8;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.js.v8.functions.Require;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.ISourceProvider;
import org.eclipse.dirigible.runtime.scripting.RepositoryModuleSourceProvider;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;
import com.eclipsesource.v8.utils.MemoryManager;

public class V8JavaScriptEngineExecutor implements IJavaScriptEngineExecutor {

	private static final Logger logger = Logger.getLogger(V8JavaScriptEngineExecutor.class);

	private IJavaScriptExecutor javaScriptExecutor;
	private ISourceProvider sourceProvider;
//	private Iterable<JsHandler> jsHandlers;

	public V8JavaScriptEngineExecutor(IJavaScriptExecutor javaScriptExecutor) {
		this.javaScriptExecutor = javaScriptExecutor;
		this.sourceProvider = createRepositoryModuleSourceProvider();
//		this.jsHandlers = jsHandlers;
	}

	@Override
	public Object executeServiceModule(HttpServletRequest request, HttpServletResponse response, Object input,
			String module, Map<Object, Object> executionContext) throws IOException {
		logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module=" + module); //$NON-NLS-1$

		if (module == null) {
			throw new IOException(IJavaScriptExecutor.JAVA_SCRIPT_MODULE_NAME_CANNOT_BE_NULL);
		}

		Object result = null;

		V8 v8 = V8.createV8Runtime();
		V8Array parameters = null;
		MemoryManager manager = new MemoryManager(v8);
//		registerHandlers(v8);
		try {
			Bindings bindings = new SimpleBindings();
			bindings.put(IJavaScriptEngineExecutor.JS_ENGINE_TYPE, IJavaScriptEngineExecutor.JS_TYPE_V8);
			this.javaScriptExecutor.registerDefaultVariables(request, response, input, executionContext,
					this.javaScriptExecutor.getRepository(), bindings);
			
//			v8.registerJavaMethod(new Require(sourceProvider), "require");
			this.javaScriptExecutor.beforeExecution(request, response, module, bindings);

			v8.registerJavaMethod(new Require(sourceProvider), "require");
			parameters = new V8Array(v8);
			registerBindings(v8, bindings);
//			Object response = v8.get("response");
			String script = sourceProvider.loadSource(module);

			result = v8.executeScript(script);
			System.out.println(v8.getKeys());
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		} finally {
			manager.release();
		}

		logger.debug("exiting: executeServiceModule()");
		return result;
	}

	private void registerBindings(V8 v8, Bindings bindings) {
		V8JavaManager javaManager = new V8JavaManager(v8);
		for (Map.Entry<String, Object> e : bindings.entrySet()) {
			String key = e.getKey();
			Object processor = e.getValue();
			javaManager.registerFunctionsForJsObject(key, processor);
		}
	}

//	private void registerHandlers(V8 v8) {
//		V8JavaManager javaManager = new V8JavaManager(v8);
//		jsHandlers.forEach(handler -> javaManager.registerFunctionsForJsObject(handler));
//	}

	private ISourceProvider createRepositoryModuleSourceProvider() {
		return new RepositoryModuleSourceProvider(this.javaScriptExecutor, this.javaScriptExecutor.getRepository(),
				this.javaScriptExecutor.getRootPaths());
	}
}