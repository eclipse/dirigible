package org.eclipse.dirigible.runtime.registry;

/**
 * Descriptor for the Registry Service
 */
public class RegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Registry";
	private final String description = "Registry Service gives read-only access to the Dirigible public Registry content.";
	private final String endpoint = "/registry";
	private final String documentation = "http://www.dirigible.io/help/service_registry.html";

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
