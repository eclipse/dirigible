package org.eclipse.dirigible.runtime.registry;

/**
 * Descriptor for the Workspace Publish Service
 */
public class WorkspaceRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Workspace";
	private final String description = "Workspace service gives full access for management of projects artifacts within the User's workspace.";
	private final String endpoint = "/workspace";
	private final String documentation = "http://www.dirigible.io/help/service_workspace.html";

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
