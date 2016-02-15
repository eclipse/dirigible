package org.eclipse.dirigible.runtime.mobile;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Mobile Apps Registry Service
 */
public class MobileRegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Mobile Apps Registry";
	private final String description = "Mobile Apps Registry Service lists all the services written in JavaScript for Mobile.";
	private final String endpoint = "/registry-mobile";
	private final String documentation = "http://www.dirigible.io/help/service_registry_mobile.html";

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
