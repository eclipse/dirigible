package org.eclipse.dirigible.runtime.js;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.RuntimeActivator;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class JavaScriptActivator implements BundleActivator {

	private static final Logger logger = Logger.getLogger(JavaScriptActivator.class);

	private static Map<String, IJavaScriptEngineProvider> javaScriptEngineProviders = Collections
			.synchronizedMap(new HashMap<String, IJavaScriptEngineProvider>());

	private static boolean registered = false;

	static void registerJavaScriptEngineProviders() {

		synchronized (JavaScriptActivator.class) {
			if (registered) {
				return;
			}

			// register javascript engine providers
			try {
				BundleContext context = RuntimeActivator.getContext();
				Collection<ServiceReference<IJavaScriptEngineProvider>> serviceReferences = context
						.getServiceReferences(IJavaScriptEngineProvider.class, null);
				for (ServiceReference<IJavaScriptEngineProvider> serviceReference : serviceReferences) {
					IJavaScriptEngineProvider javaScriptEngineProvider = context.getService(serviceReference);
					javaScriptEngineProviders.put(javaScriptEngineProvider.getType(), javaScriptEngineProvider);
				}
			} catch (InvalidSyntaxException e) {
				logger.error(e.getMessage(), e);
			}
			registered = true;
		}
	}

	@Override
	public void start(BundleContext context) throws Exception {
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

	public static IJavaScriptEngineExecutor createExecutor(String type, JavaScriptExecutor javaScriptExecutor) throws IOException {
		registerJavaScriptEngineProviders();
		IJavaScriptEngineProvider javascriptEngineProvider = javaScriptEngineProviders.get(type);
		IJavaScriptEngineExecutor javacriptExecutor = javascriptEngineProvider.create(javaScriptExecutor);
		return javacriptExecutor;
	}

}
