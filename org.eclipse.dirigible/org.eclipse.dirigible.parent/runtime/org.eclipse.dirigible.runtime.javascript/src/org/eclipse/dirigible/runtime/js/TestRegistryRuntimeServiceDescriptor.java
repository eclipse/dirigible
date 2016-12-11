package org.eclipse.dirigible.runtime.js;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Test Registry Service
 */
public class TestRegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Test Registry"; //$NON-NLS-1$
	private static final String description = "Test Registry Service lists all the unit tests written in JavaScript.";
	private static final String endpoint = "/registry-test"; //$NON-NLS-1$
	private static final String documentation = "http://www.dirigible.io/help/service_registry_test.html"; //$NON-NLS-1$

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
