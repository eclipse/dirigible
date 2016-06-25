package org.eclipse.dirigible.runtime.scripting;

public interface IJavaScriptEngineProvider {

	public String getType();

	public IJavaScriptEngineExecutor create(IJavaScriptExecutor javaScriptExecutor);

}
