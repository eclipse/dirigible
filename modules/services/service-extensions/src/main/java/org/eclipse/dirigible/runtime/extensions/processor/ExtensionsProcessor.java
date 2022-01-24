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
package org.eclipse.dirigible.runtime.extensions.processor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;

/**
 * Processing the Extensions Service incoming requests.
 */
public class ExtensionsProcessor {

	private ExtensionsCoreService extensionsCoreService = new ExtensionsCoreService();

	/**
	 * Render extension points.
	 *
	 * @return the string
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public String renderExtensionPoints() throws ExtensionsException {
		List<ExtensionPoint> bundles = new ArrayList<ExtensionPoint>();
		List<ExtensionPointDefinition> extensionPoints = extensionsCoreService.getExtensionPoints();
		for (ExtensionPointDefinition extensionPointDefinition : extensionPoints) {
			List<ExtensionDefinition> extensions = extensionsCoreService.getExtensionsByExtensionPoint(extensionPointDefinition.getName());
			ExtensionPoint bundle = new ExtensionPoint(extensionPointDefinition, extensions);
			bundles.add(bundle);
		}
		return GsonHelper.GSON.toJson(bundles);
	}

	/**
	 * Render extension point.
	 *
	 * @param name
	 *            the name
	 * @return the string
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public String renderExtensionPoint(String name) throws ExtensionsException {
		ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.getExtensionPointByName(name);
		if (extensionPointDefinition == null) {
			return null;
		}
		List<ExtensionDefinition> extensions = extensionsCoreService.getExtensionsByExtensionPoint(extensionPointDefinition.getName());
		ExtensionPoint bundle = new ExtensionPoint(extensionPointDefinition, extensions);
		return GsonHelper.GSON.toJson(bundle);
	}

}
