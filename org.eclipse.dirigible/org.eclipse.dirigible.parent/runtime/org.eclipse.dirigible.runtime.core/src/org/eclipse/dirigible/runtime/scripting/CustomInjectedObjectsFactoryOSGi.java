package org.eclipse.dirigible.runtime.scripting;

import java.util.Collection;
import java.util.Map;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.RuntimeActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class CustomInjectedObjectsFactoryOSGi {

	private static final Logger logger = Logger.getLogger(CustomInjectedObjectsFactoryOSGi.class);

	public static void registerCustomObjects(IBaseScriptExecutor executor, Map<Object, Object> executionContext, Object scope,
			InjectedAPIBuilder apiBuilder) {
		try {
			BundleContext context = RuntimeActivator.getContext();
			if (context != null) {
				Collection<ServiceReference<IContextService>> serviceReferences = context.getServiceReferences(IContextService.class, null);
				for (ServiceReference<IContextService> serviceReference : serviceReferences) {
					try {
						IContextService contextService = context.getService(serviceReference);
						executor.registerDefaultVariableInContextAndScope(executionContext, scope, contextService.getName(),
								contextService.getInstance());
						apiBuilder.set(contextService.getName(), contextService.getInstance());
					} catch (Throwable t) {
						logger.error(t.getMessage(), t);
					}
				}
			}
		} catch (InvalidSyntaxException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
