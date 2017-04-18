package org.eclipse.dirigible.runtime.content;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Clone Import Service
 */
public class CloneImportRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Clone Import"; //$NON-NLS-1$
	private final String description = "Clone Import Service provides capability to put the full Repository content to the current instance.";
	private final String endpoint = "/clone-import"; //$NON-NLS-1$
	private final String documentation = "http://www.dirigible.io/help/service_clone_import.html"; //$NON-NLS-1$

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
