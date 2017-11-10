/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.extensions.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;

public interface IExtensionsCoreService extends ICoreService {
	
	public static final String FILE_EXTENSION_EXTENSIONPOINT = ".extensionpoint";
	
	public static final String FILE_EXTENSION_EXTENSION = ".extension";
	
	
	// Extension Points
	
	public ExtensionPointDefinition createExtensionPoint(String location, String name, String description) throws ExtensionsException;

	public ExtensionPointDefinition getExtensionPoint(String location) throws ExtensionsException;
	
	public ExtensionPointDefinition getExtensionPointByName(String name) throws ExtensionsException;
	
	public boolean existsExtensionPoint(String location) throws ExtensionsException;

	public void removeExtensionPoint(String location) throws ExtensionsException;

	public void updateExtensionPoint(String location, String name, String description) throws ExtensionsException;

	public List<ExtensionPointDefinition> getExtensionPoints() throws ExtensionsException;
	
	public ExtensionPointDefinition parseExtensionPoint(String json);
	
	public ExtensionPointDefinition parseExtensionPoint(byte[] json);
	
	public String serializeExtensionPoint(ExtensionPointDefinition extensionPointDefinition);
	
	// Extensions

	public ExtensionDefinition createExtension(String location, String module, String extensionPoint, String description)
			throws ExtensionsException;

	public ExtensionDefinition getExtension(String location) throws ExtensionsException;
	
	public boolean existsExtension(String location) throws ExtensionsException;

	public void removeExtension(String location) throws ExtensionsException;

	public void updateExtension(String location, String module, String extensionPoint, String description) throws ExtensionsException;

	public List<ExtensionDefinition> getExtensions() throws ExtensionsException;

	public List<ExtensionDefinition> getExtensionsByExtensionPoint(String extensionPoint) throws ExtensionsException;
	
	public ExtensionDefinition parseExtension(String json);
	
	public ExtensionDefinition parseExtension(byte[] json);
	
	public String serializeExtension(ExtensionDefinition extensionDefinition);

}
