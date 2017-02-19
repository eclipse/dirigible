package org.eclipse.dirigible.runtime.scripting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.repository.logging.Logger;

public class CustomInjectedObjectsFactoryNonOSGi {

	private static final Logger logger = Logger.getLogger(CustomInjectedObjectsFactoryNonOSGi.class);

	static List<IContextService> contextServiceProviders = new ArrayList<IContextService>();

	static String wikiContextServiceProvider = "org.eclipse.dirigible.runtime.wiki.WikiContextService";

	static {
		contextServiceProviders.add(createContextServiceProvider(wikiContextServiceProvider));
	}

	public static void registerCustomObjects(IBaseScriptExecutor executor, Map<Object, Object> executionContext, Object scope,
			InjectedAPIBuilder apiBuilder) {
		for (IContextService provider : contextServiceProviders) {
			try {
				executor.registerDefaultVariableInContextAndScope(executionContext, scope, provider.getName(), provider);
				apiBuilder.set(provider.getName(), provider);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
		}
	}

	private static IContextService createContextServiceProvider(String clazz) {
		try {
			return (IContextService) Class.forName(clazz).newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
