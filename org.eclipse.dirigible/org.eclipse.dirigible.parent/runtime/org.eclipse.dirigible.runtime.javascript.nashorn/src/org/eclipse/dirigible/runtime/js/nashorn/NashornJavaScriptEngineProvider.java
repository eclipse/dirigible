package org.eclipse.dirigible.runtime.js.nashorn;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineProvider;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptExecutor;

public class NashornJavaScriptEngineProvider implements IJavaScriptEngineProvider {

	private static final Logger logger = Logger.getLogger(NashornJavaScriptEngineProvider.class);

	@Override
	public String getType() {
		return "nashorn";
	}

	@Override
	public IJavaScriptEngineExecutor create(IJavaScriptExecutor javaScriptExecutor) {
		return new NashornJavaScriptEngineExecutor(javaScriptExecutor);
	}

}
