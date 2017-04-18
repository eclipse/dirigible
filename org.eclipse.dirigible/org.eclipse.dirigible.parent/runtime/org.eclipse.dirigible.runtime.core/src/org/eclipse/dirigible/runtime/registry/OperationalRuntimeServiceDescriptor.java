package org.eclipse.dirigible.runtime.registry;

/**
 * Descriptor for the Operational Service
 */
public class OperationalRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Operational"; //$NON-NLS-1$
	private final String description = "Operational Service exposes some utility functions.";
	private final String endpoint = "/op"; //$NON-NLS-1$
	private final String documentation = "http://www.dirigible.io/help/service_operational.html"; //$NON-NLS-1$

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
