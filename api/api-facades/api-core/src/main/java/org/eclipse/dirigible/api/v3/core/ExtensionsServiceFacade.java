/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.core;

import java.util.List;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ExtensionsServiceFacade.
 */
public class ExtensionsServiceFacade implements IScriptingFacade {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ExtensionsServiceFacade.class);

	/** The extensions core service. */
	private static IExtensionsCoreService extensionsCoreService = StaticInjector.getInjector().getInstance(ExtensionsCoreService.class);

	/**
	 * Gets the extensions.
	 *
	 * @param extensionPointName the extension point name
	 * @return the extensions
	 * @throws ExtensionsException the extensions exception
	 */
	public static final String[] getExtensions(String extensionPointName) throws ExtensionsException {
		logger.trace("API - ExtensionsServiceFacade.getExtensions() -> begin");
		List<ExtensionDefinition> extensionDefinitions = extensionsCoreService.getExtensionsByExtensionPoint(extensionPointName);
		String[] extensions = new String[extensionDefinitions.size()];
		int i = 0;
		for (ExtensionDefinition extensionDefinition : extensionDefinitions) {
			extensions[i++] = extensionDefinition.getModule();
		}
		logger.trace("API - ExtensionsServiceFacade.getExtensions() -> end");
		return extensions;
	}

	/**
	 * Gets the extension points.
	 *
	 * @return the extension points
	 * @throws ExtensionsException the extensions exception
	 */
	public static final String[] getExtensionPoints() throws ExtensionsException {
		logger.trace("API - ExtensionsServiceFacade.getExtensionPoints() -> begin");
		List<ExtensionPointDefinition> extensionPointDefinitions = extensionsCoreService.getExtensionPoints();
		String[] extensionPoints = new String[extensionPointDefinitions.size()];
		int i = 0;
		for (ExtensionPointDefinition extensionPointDefinition : extensionPointDefinitions) {
			extensionPoints[i++] = extensionPointDefinition.getName();
		}
		logger.trace("API - ExtensionsServiceFacade.getExtensionPoints() -> end");
		return extensionPoints;
	}

}
