package org.eclipse.dirigible.runtime.extensions.processor;

import java.util.List;

import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.DataStructureTableModel;

public class ExtensionPoint {
	
	private DataStructureTableModel extensionPoint;
	
	private List<ExtensionDefinition> extensions;

	public ExtensionPoint(DataStructureTableModel extensionPoint, List<ExtensionDefinition> extensions) {
		super();
		this.extensionPoint = extensionPoint;
		this.extensions = extensions;
	}
	
	public DataStructureTableModel getExtensionPoint() {
		return extensionPoint;
	}
	
	public List<ExtensionDefinition> getExtensions() {
		return extensions;
	}
	
}
