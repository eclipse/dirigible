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
package org.eclipse.dirigible.core.extensions.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;

/**
 * The Interface IExtensionsCoreService.
 */
public interface IExtensionsCoreService extends ICoreService {

	/** The Constant FILE_EXTENSION_EXTENSIONPOINT. */
	public static final String FILE_EXTENSION_EXTENSIONPOINT = ".extensionpoint";

	/** The Constant FILE_EXTENSION_EXTENSION. */
	public static final String FILE_EXTENSION_EXTENSION = ".extension";

	// Extension Points

	/**
	 * Creates the extension point.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @return the extension point definition
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public ExtensionPointDefinition createExtensionPoint(String location, String name, String description) throws ExtensionsException;

	/**
	 * Gets the extension point.
	 *
	 * @param location
	 *            the location
	 * @return the extension point
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public ExtensionPointDefinition getExtensionPoint(String location) throws ExtensionsException;

	/**
	 * Gets the extension point by name.
	 *
	 * @param name
	 *            the name
	 * @return the extension point by name
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public ExtensionPointDefinition getExtensionPointByName(String name) throws ExtensionsException;

	/**
	 * Exists extension point.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public boolean existsExtensionPoint(String location) throws ExtensionsException;

	/**
	 * Removes the extension point.
	 *
	 * @param location
	 *            the location
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public void removeExtensionPoint(String location) throws ExtensionsException;

	/**
	 * Update extension point.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public void updateExtensionPoint(String location, String name, String description) throws ExtensionsException;

	/**
	 * Gets the extension points.
	 *
	 * @return the extension points
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public List<ExtensionPointDefinition> getExtensionPoints() throws ExtensionsException;

	/**
	 * Parses the extension point.
	 *
	 * @param json
	 *            the json
	 * @return the extension point definition
	 */
	public ExtensionPointDefinition parseExtensionPoint(String json);

	/**
	 * Parses the extension point.
	 *
	 * @param json
	 *            the json
	 * @return the extension point definition
	 */
	public ExtensionPointDefinition parseExtensionPoint(byte[] json);

	/**
	 * Serialize extension point.
	 *
	 * @param extensionPointDefinition
	 *            the extension point definition
	 * @return the string
	 */
	public String serializeExtensionPoint(ExtensionPointDefinition extensionPointDefinition);

	// Extensions

	/**
	 * Creates the extension.
	 *
	 * @param location
	 *            the location
	 * @param module
	 *            the module
	 * @param extensionPoint
	 *            the extension point
	 * @param description
	 *            the description
	 * @return the extension definition
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public ExtensionDefinition createExtension(String location, String module, String extensionPoint, String description) throws ExtensionsException;

	/**
	 * Gets the extension.
	 *
	 * @param location
	 *            the location
	 * @return the extension
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public ExtensionDefinition getExtension(String location) throws ExtensionsException;

	/**
	 * Exists extension.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public boolean existsExtension(String location) throws ExtensionsException;

	/**
	 * Removes the extension.
	 *
	 * @param location
	 *            the location
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public void removeExtension(String location) throws ExtensionsException;

	/**
	 * Update extension.
	 *
	 * @param location
	 *            the location
	 * @param module
	 *            the module
	 * @param extensionPoint
	 *            the extension point
	 * @param description
	 *            the description
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public void updateExtension(String location, String module, String extensionPoint, String description) throws ExtensionsException;

	/**
	 * Gets the extensions.
	 *
	 * @return the extensions
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public List<ExtensionDefinition> getExtensions() throws ExtensionsException;

	/**
	 * Gets the extensions by extension point.
	 *
	 * @param extensionPoint
	 *            the extension point
	 * @return the extensions by extension point
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	public List<ExtensionDefinition> getExtensionsByExtensionPoint(String extensionPoint) throws ExtensionsException;

	/**
	 * Parses the extension.
	 *
	 * @param json
	 *            the json
	 * @return the extension definition
	 */
	public ExtensionDefinition parseExtension(String json);

	/**
	 * Parses the extension.
	 *
	 * @param json
	 *            the json
	 * @return the extension definition
	 */
	public ExtensionDefinition parseExtension(byte[] json);

	/**
	 * Serialize extension.
	 *
	 * @param extensionDefinition
	 *            the extension definition
	 * @return the string
	 */
	public String serializeExtension(ExtensionDefinition extensionDefinition);

}
