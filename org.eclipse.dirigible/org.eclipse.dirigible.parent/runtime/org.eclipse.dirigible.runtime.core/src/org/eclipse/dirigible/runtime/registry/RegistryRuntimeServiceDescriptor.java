package org.eclipse.dirigible.runtime.registry;

/**
 * Descriptor for the Registry Service
 */
public class RegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Registry"; //$NON-NLS-1$
	private static final String description = "Registry Service gives read-only access to the Dirigible public Registry content.";
	private static final String endpoint = "/registry"; //$NON-NLS-1$
	private static final String documentation = "http://www.dirigible.io/help/service_registry.html"; //$NON-NLS-1$

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
