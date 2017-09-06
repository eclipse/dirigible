package org.eclipse.dirigible.core.extensions.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.DataStructureTableModel;

public interface IExtensionsCoreService extends ICoreService {
	
	public static final String FILE_EXTENSION_EXTENSIONPOINT = ".extensionpoint";
	
	public static final String FILE_EXTENSION_EXTENSION = ".extension";
	
	
	// Extension Points
	
	public DataStructureTableModel createExtensionPoint(String location, String name, String description) throws ExtensionsException;

	public DataStructureTableModel getExtensionPoint(String location) throws ExtensionsException;
	
	public DataStructureTableModel getExtensionPointByName(String name) throws ExtensionsException;
	
	public boolean existsExtensionPoint(String location) throws ExtensionsException;

	public void removeExtensionPoint(String location) throws ExtensionsException;

	public void updateExtensionPoint(String location, String name, String description) throws ExtensionsException;

	public List<DataStructureTableModel> getExtensionPoints() throws ExtensionsException;
	
	public DataStructureTableModel parseExtensionPoint(String json);
	
	public DataStructureTableModel parseExtensionPoint(byte[] json);
	
	public String serializeExtensionPoint(DataStructureTableModel extensionPointDefinition);
	
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