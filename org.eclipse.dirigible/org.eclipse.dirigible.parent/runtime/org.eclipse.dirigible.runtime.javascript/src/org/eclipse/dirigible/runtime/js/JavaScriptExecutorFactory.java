package org.eclipse.dirigible.runtime.js;

import java.io.IOException;

import org.eclipse.dirigible.repository.ext.utils.OSGiUtils;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;

public class JavaScriptExecutorFactory {

	public static IJavaScriptEngineExecutor createExecutor(String type, JavaScriptExecutor javaScriptExecutor) throws IOException {
		if (OSGiUtils.isOSGiEnvironment()) {
			return JavaScriptExecutorFactoryOSGi.createExecutor(type, javaScriptExecutor);
		}
		return JavaScriptExecutorFactoryNonOSGi.createExecutor(type, javaScriptExecutor);
	}

}
