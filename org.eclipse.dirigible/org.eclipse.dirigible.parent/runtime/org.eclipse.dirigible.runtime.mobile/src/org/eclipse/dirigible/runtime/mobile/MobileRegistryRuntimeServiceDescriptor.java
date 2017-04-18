package org.eclipse.dirigible.runtime.mobile;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Mobile Apps Registry Service
 */
public class MobileRegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Mobile Apps Registry"; //$NON-NLS-1$
	private final String description = "Mobile Apps Registry Service lists all the services written in JavaScript for Mobile.";
	private final String endpoint = "/registry-mobile"; //$NON-NLS-1$
	private final String documentation = "http://www.dirigible.io/help/service_registry_mobile.html"; //$NON-NLS-1$

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
