package org.eclipse.dirigible.repository.ext.extensions;

public interface IExtensionService {
	
	public String[] getExtensions(String extensionPoint) throws EExtensionException;

	public ExtensionDefinition getExtension(String extension, String extensionPoint) throws EExtensionException;
	
	public ExtensionPointDefinition getExtensionPoint(String extensionPoint) throws EExtensionException;
	
	public String[] getExtensionPoints() throws EExtensionException;
	
	public void createExtension(String extension,
			String extensionPoint, String description) throws EExtensionException;
	
	public void updateExtension(String extension,
			String extensionPoint, String description) throws EExtensionException;

	public void createExtensionPoint(String extensionPoint, String description) throws EExtensionException;
	
	public void updateExtensionPoint(String extensionPoint, String description) throws EExtensionException;
	
	public void removeExtension(String extension, String extensionPoint) throws EExtensionException;	
	
	public void removeExtensionPoint(String extensionPoint) throws EExtensionException;	

}
