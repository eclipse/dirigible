package org.eclipse.dirigible.runtime.scripting;

import java.util.Map;

import org.eclipse.dirigible.repository.ext.utils.OSGiUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class CustomInjectedObjectsFactory {

	private static final Logger logger = Logger.getLogger(CustomInjectedObjectsFactory.class);

	public static void registerCustomObjects(IBaseScriptExecutor executor, Map<Object, Object> executionContext, Object scope,
			InjectedAPIBuilder apiBuilder) {
		if (OSGiUtils.isOSGiEnvironment()) {
			CustomInjectedObjectsFactoryOSGi.registerCustomObjects(executor, executionContext, scope, apiBuilder);
		}
		CustomInjectedObjectsFactoryNonOSGi.registerCustomObjects(executor, executionContext, scope, apiBuilder);
	}

}
