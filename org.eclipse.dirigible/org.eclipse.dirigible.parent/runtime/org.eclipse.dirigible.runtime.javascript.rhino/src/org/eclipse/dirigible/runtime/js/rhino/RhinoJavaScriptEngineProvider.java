package org.eclipse.dirigible.runtime.js.rhino;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineProvider;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptExecutor;

public class RhinoJavaScriptEngineProvider implements IJavaScriptEngineProvider {

	private static final Logger logger = Logger.getLogger(RhinoJavaScriptEngineProvider.class);

	@Override
	public String getType() {
		return "rhino";
	}

	@Override
	public IJavaScriptEngineExecutor create(IJavaScriptExecutor javaScriptExecutor) {
		return new RhinoJavaScriptEngineExecutor(javaScriptExecutor);
	}

}
