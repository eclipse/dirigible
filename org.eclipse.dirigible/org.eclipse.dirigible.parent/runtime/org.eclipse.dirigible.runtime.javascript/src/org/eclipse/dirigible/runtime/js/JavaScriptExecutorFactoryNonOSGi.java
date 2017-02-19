package org.eclipse.dirigible.runtime.js;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineProvider;

class JavaScriptExecutorFactoryNonOSGi {

	private static final Logger logger = Logger.getLogger(JavaScriptExecutorFactoryNonOSGi.class);

	private static Map<String, IJavaScriptEngineProvider> javaScriptEngineProviders = Collections
			.synchronizedMap(new HashMap<String, IJavaScriptEngineProvider>());

	private static boolean registered = false;

	static String rhinoJavaScriptEngineProvider = "org.eclipse.dirigible.runtime.js.rhino.RhinoJavaScriptEngineProvider";
	static String nashornJavaScriptEngineProvider = "org.eclipse.dirigible.runtime.js.nashorn.NashornJavaScriptEngineProvider";

	static {
		IJavaScriptEngineProvider rhino = createJavaScriptEngineProvider(rhinoJavaScriptEngineProvider);
		javaScriptEngineProviders.put(rhino.getType(), rhino);
		IJavaScriptEngineProvider nashorn = createJavaScriptEngineProvider(nashornJavaScriptEngineProvider);
		javaScriptEngineProviders.put(nashorn.getType(), nashorn);
	}

	public static IJavaScriptEngineExecutor createExecutor(String type, JavaScriptExecutor javaScriptExecutor) throws IOException {
		IJavaScriptEngineProvider javascriptEngineProvider = javaScriptEngineProviders.get(type);
		IJavaScriptEngineExecutor javacriptExecutor = javascriptEngineProvider.create(javaScriptExecutor);
		return javacriptExecutor;
	}

	private static IJavaScriptEngineProvider createJavaScriptEngineProvider(String clazz) {
		try {
			return (IJavaScriptEngineProvider) Class.forName(clazz).newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
