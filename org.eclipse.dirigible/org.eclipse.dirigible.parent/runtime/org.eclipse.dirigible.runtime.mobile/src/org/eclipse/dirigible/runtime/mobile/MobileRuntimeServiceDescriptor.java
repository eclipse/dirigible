package org.eclipse.dirigible.runtime.mobile;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Mobile Execution Service
 */
public class MobileRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Mobile App Provisioning"; //$NON-NLS-1$
	private static final String description = "Mobile App Provisioning Service provides the code for the application written in JavaScript for Mobile.";
	private static final String endpoint = "/mobile"; //$NON-NLS-1$
	private static final String documentation = "http://www.dirigible.io/help/service_mobile.html"; //$NON-NLS-1$

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
