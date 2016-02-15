package org.eclipse.dirigible.runtime.web;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Web Content Registry Service
 */
public class WebRegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Web Content Registry";
	private final String description = "Web Content Registry Service lists all the artifacts under the Web Content space";
	private final String endpoint = "/registry-web";
	private final String documentation = "http://www.dirigible.io/help/service_registry_web.html";

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
