package org.eclipse.dirigible.api.v3.core;

import java.util.List;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsServiceFacade implements IScriptingFacade {
	
	//private static final Logger logger = LoggerFactory.getLogger(ExtensionsServiceFacade.class);
	
	private static final IExtensionsCoreService extensionsCoreService = StaticInjector.getInjector().getInstance(ExtensionsCoreService.class);
	
	public static final String[] getExtensions(String extensionPoint) throws ExtensionsException {
		return extensionsCoreService.getExtensionsByExtensionPoint(extensionPoint).toArray(new String[]{});
	}
	
	public static final String[] getExtensionPoints() throws ExtensionsException {
		List<ExtensionPointDefinition> extensionPointDefinitions = extensionsCoreService.getExtensionPoints();
		String[] extensionPoints = new String[extensionPointDefinitions.size()];
		int i = 0;
		for (ExtensionPointDefinition extensionPointDefinition : extensionPointDefinitions) {
			extensionPoints[i++] = extensionPointDefinition.getName();
		}
		return extensionPoints;
	}
	
}
