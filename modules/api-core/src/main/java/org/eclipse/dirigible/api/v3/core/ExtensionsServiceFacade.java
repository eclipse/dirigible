package org.eclipse.dirigible.api.v3.core;

import java.util.List;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.ISecurityCoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsServiceFacade implements IScriptingFacade {
	
	private static final Logger logger = LoggerFactory.getLogger(ExtensionsServiceFacade.class);
	
	private static ISecurityCoreService extensionsCoreService = StaticInjector.getInjector().getInstance(ExtensionsCoreService.class);
	
	public static final String[] getExtensions(String extensionPointName) throws ExtensionsException {
		logger.debug("API - ExtensionsServiceFacade.getExtensions() -> begin");
		List<ExtensionDefinition> extensionDefinitions = extensionsCoreService.getExtensionsByExtensionPoint(extensionPointName);
		String[] extensions = new String[extensionDefinitions.size()];
		int i = 0;
		for (ExtensionDefinition extensionDefinition : extensionDefinitions) {
			extensions[i++] = extensionDefinition.getModule();
		}
		logger.debug("API - ExtensionsServiceFacade.getExtensions() -> end");
		return extensions;
	}
	
	public static final String[] getExtensionPoints() throws ExtensionsException {
		logger.debug("API - ExtensionsServiceFacade.getExtensionPoints() -> begin");
		List<ExtensionPointDefinition> extensionPointDefinitions = extensionsCoreService.getExtensionPoints();
		String[] extensionPoints = new String[extensionPointDefinitions.size()];
		int i = 0;
		for (ExtensionPointDefinition extensionPointDefinition : extensionPointDefinitions) {
			extensionPoints[i++] = extensionPointDefinition.getName();
		}
		logger.debug("API - ExtensionsServiceFacade.getExtensionPoints() -> end");
		return extensionPoints;
	}
	
}
