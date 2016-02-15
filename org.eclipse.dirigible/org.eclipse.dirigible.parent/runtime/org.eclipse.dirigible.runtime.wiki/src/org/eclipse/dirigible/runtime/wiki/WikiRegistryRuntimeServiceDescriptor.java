package org.eclipse.dirigible.runtime.wiki;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Wiki Pages Registry Service
 */
public class WikiRegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Wiki Pages Registry";
	private final String description = "Wiki Pages Registry Service lists all the pages under the Wiki space";
	private final String endpoint = "/registry-wiki";
	private final String documentation = "http://www.dirigible.io/help/service_registry_wiki.html";

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
