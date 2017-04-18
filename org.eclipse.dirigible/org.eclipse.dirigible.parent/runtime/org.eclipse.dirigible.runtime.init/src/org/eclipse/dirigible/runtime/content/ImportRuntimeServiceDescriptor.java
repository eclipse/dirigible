package org.eclipse.dirigible.runtime.content;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Import Service
 */
public class ImportRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Import"; //$NON-NLS-1$
	private final String description = "Import Service provides capability to add additional content to the public Registry of the current instance.";
	private final String endpoint = "/import"; //$NON-NLS-1$
	private final String documentation = "http://www.dirigible.io/help/service_import.html"; //$NON-NLS-1$

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getEndpoint() {
		return endpoint;
	}

	@Override
	public String getDocumentation() {
		return documentation;
	}

}
