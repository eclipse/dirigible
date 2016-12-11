package org.eclipse.dirigible.runtime.registry;

/**
 * Descriptor for the Workspace Service
 */
public class WorkspaceRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Workspace"; //$NON-NLS-1$
	private static final String description = "Workspace Service gives full access for management of projects artifacts within the User's workspace.";
	private static final String endpoint = "/workspace"; //$NON-NLS-1$
	private static final String documentation = "http://www.dirigible.io/help/service_workspace.html"; //$NON-NLS-1$

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
