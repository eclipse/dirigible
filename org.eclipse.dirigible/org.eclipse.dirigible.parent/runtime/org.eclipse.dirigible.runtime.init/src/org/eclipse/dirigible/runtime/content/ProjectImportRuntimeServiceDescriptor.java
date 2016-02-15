package org.eclipse.dirigible.runtime.content;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Project Import Service
 */
public class ProjectImportRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Project Import";
	private final String description = "Project Import Service provides capability to include the design-time content of a Project to the public Registry of the current instance";
	private final String endpoint = "/project-import";
	private final String documentation = "http://www.dirigible.io/help/service_project_import.html";

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
