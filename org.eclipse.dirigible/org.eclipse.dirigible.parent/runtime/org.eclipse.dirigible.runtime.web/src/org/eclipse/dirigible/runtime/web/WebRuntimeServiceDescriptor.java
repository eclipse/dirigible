package org.eclipse.dirigible.runtime.web;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Web Content Provisioning Service
 */
public class WebRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Web Content Provisioning";
	private final String description = "Web Content Provisioning Service sends the requested Web Content artifact";
	private final String endpoint = "/web";
	private final String documentation = "http://www.dirigible.io/help/service_web.html";

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
