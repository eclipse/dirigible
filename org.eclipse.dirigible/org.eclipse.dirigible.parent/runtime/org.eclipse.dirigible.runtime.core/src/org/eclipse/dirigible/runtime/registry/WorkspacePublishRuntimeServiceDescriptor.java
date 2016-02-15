package org.eclipse.dirigible.runtime.registry;

/**
 * Descriptor for the Workspace Publish Service
 */
public class WorkspacePublishRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Publish";
	private final String description = "Publish service performs publication of a single artifact from the User's workspace to the Registry";
	private final String endpoint = "/publish";
	private final String documentation = "http://www.dirigible.io/help/service_publish.html";

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
