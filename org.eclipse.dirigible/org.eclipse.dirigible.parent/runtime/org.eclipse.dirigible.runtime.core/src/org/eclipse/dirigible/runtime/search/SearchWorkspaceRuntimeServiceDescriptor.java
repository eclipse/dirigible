package org.eclipse.dirigible.runtime.search;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Search Workspace Service
 */
public class SearchWorkspaceRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Search Workspace";
	private final String description = "Search Workspace Service provides the access to the free text index created on the User's Workspace content.";
	private final String endpoint = "/searchw";
	private final String documentation = "http://www.dirigible.io/help/service_searchw.html";

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
