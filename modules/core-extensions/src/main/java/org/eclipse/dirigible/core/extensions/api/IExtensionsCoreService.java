package org.eclipse.dirigible.core.extensions.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;

public interface IExtensionsCoreService extends ICoreService {
	
	public ExtensionPointDefinition createExtensionPoint(String extensionPoint, String description) throws ExtensionsException;

	public ExtensionPointDefinition getExtensionPoint(String extensionPoint) throws ExtensionsException;
	
	public boolean existsExtensionPoint(String extensionPoint) throws ExtensionsException;

	public void removeExtensionPoint(String extensionPoint) throws ExtensionsException;

	public void updateExtensionPoint(String extensionPoint, String description) throws ExtensionsException;

	public List<ExtensionPointDefinition> getExtensionPoints() throws ExtensionsException;

	public ExtensionDefinition createExtension(String extension, String extensionPoint, String description)
			throws ExtensionsException;

	public ExtensionDefinition getExtension(String extension) throws ExtensionsException;
	
	public boolean existsExtension(String extension) throws ExtensionsException;

	public void removeExtension(String extension) throws ExtensionsException;

	public void updateExtension(String extension, String extensionPoint, String description) throws ExtensionsException;

	public List<ExtensionDefinition> getExtensions() throws ExtensionsException;

	public List<ExtensionDefinition> getExtensionsByExtensionPoint(String extensionPoint) throws ExtensionsException;
	
	public ExtensionPointDefinition parseExtensionPoint(String json);
	
	public ExtensionDefinition parseExtension(String json);
	
	public ExtensionPointDefinition parseExtensionPoint(byte[] json);
	
	public ExtensionDefinition parseExtension(byte[] json);
	
	public String serializeExtensionPoint(ExtensionPointDefinition extensionPointDefinition);
	
	public String serializeExtension(ExtensionDefinition extensionDefinition);

}