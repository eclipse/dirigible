package org.eclipse.dirigible.runtime.search;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Search Service
 */
public class SearchRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Search";
	private final String description = "Search Service provides the access to the free text index created on the whole Repository's content.";
	private final String endpoint = "/search";
	private final String documentation = "http://www.dirigible.io/help/service_search.html";

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
