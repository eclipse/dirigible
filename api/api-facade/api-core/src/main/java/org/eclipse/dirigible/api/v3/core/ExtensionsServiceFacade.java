/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.core;

import java.util.List;

import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ExtensionsServiceFacade expose the information about the current extension points and extensions.
 */
public class ExtensionsServiceFacade implements IScriptingFacade {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ExtensionsServiceFacade.class);

	/** The extensions core service. */
	private static IExtensionsCoreService extensionsCoreService = new ExtensionsCoreService();

	/**
	 * Gets the extensions per extension point.
	 *
	 * @param extensionPointName
	 *            the extension point name
	 * @return the extensions
	 * @throws ExtensionsException
	 *             the extensions exception
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
	 * Gets all the extension points.
	 *
	 * @return the extension points
	 * @throws ExtensionsException
	 *             the extensions exception
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
