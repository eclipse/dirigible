package org.eclipse.dirigible.core.extensions;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;

public interface IExtensionsCoreService extends ICoreService {
	
	public ExtensionPointDefinition createExtensionPoint(String extensionPoint, String description) throws ExtensionsException;

	public ExtensionPointDefinition getExtensionPoint(String extensionPoint) throws ExtensionsException;

	public void removeExtensionPoint(String extensionPoint) throws ExtensionsException;

	public void updateExtensionPoint(String extensionPoint, String description) throws ExtensionsException;

	public List<ExtensionPointDefinition> getExtensionPoints() throws ExtensionsException;

	public ExtensionDefinition createExtension(String extension, String extensionPoint, String description)
			throws ExtensionsException;

	public ExtensionDefinition getExtension(String extension) throws ExtensionsException;

	public void removeExtension(String extension) throws ExtensionsException;

	public void updateExtension(String extension, String extensionPoint, String description) throws ExtensionsException;

	public List<ExtensionDefinition> getExtensions() throws ExtensionsException;

	public List<ExtensionDefinition> getExtensionsByExtensionPoint(String extensionPoint) throws ExtensionsException;

}