package org.eclipse.dirigible.runtime.js.v8;

import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineProvider;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptExecutor;

public class V8JavaScriptEngineprovider implements IJavaScriptEngineProvider {

	@Override
	public String getType() {
		return "v8";
	}

	@Override
	public IJavaScriptEngineExecutor create(IJavaScriptExecutor javaScriptExecutor) {
		return new V8JavaScriptEngineExecutor(javaScriptExecutor);
	}
//
//	private Iterable<JsHandler> provideHandlers() {
//		List<JsHandler> handlers = new LinkedList<>();
//		handlers.add(new Console());
//		return Collections.unmodifiableList(handlers);
//	}
}
