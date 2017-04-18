package org.eclipse.dirigible.runtime.listener;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Listener Registry Service
 */
public class ListenerRegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Listener Registry"; //$NON-NLS-1$
	private final String description = "Listener Registry Service lists all the Listener declarations.";
	private final String endpoint = "/registry-listener"; //$NON-NLS-1$
	private final String documentation = "http://www.dirigible.io/help/service_registry_listener.html"; //$NON-NLS-1$

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
