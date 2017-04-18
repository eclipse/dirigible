package org.eclipse.dirigible.runtime.flow;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Flow Registry Service
 */
public class FlowRegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Flow Registry"; //$NON-NLS-1$
	private final String description = "Flow Registry Service lists all the Flow declarations.";
	private final String endpoint = "/registry-flow"; //$NON-NLS-1$
	private final String documentation = "http://www.dirigible.io/help/service_registry_flow.html"; //$NON-NLS-1$

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
