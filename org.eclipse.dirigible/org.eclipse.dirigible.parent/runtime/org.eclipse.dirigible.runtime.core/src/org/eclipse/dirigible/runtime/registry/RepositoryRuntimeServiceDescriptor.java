package org.eclipse.dirigible.runtime.registry;

/**
 * Descriptor for the Repository Service
 */
public class RepositoryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Repository";
	private static final String description = "Repository Service gives full access to the Dirigible Repository API.";
	private static final String endpoint = "/repository";
	private static final String documentation = "http://www.dirigible.io/help/service_repository.html";

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
