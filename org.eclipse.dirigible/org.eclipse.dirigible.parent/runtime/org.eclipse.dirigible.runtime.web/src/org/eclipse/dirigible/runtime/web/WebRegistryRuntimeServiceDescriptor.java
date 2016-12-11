package org.eclipse.dirigible.runtime.web;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Web Content Registry Service
 */
public class WebRegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Web Content Registry"; //$NON-NLS-1$
	private static final String description = "Web Content Registry Service lists all the artifacts under the Web Content space.";
	private static final String endpoint = "/registry-web"; //$NON-NLS-1$
	private static final String documentation = "http://www.dirigible.io/help/service_registry_web.html"; //$NON-NLS-1$

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
