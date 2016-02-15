package org.eclipse.dirigible.runtime.js;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Test Registry Service
 */
public class TestRegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Test Registry";
	private final String description = "Test Registry Service lists all the unit tests written in JavaScript.";
	private final String endpoint = "/registry-test";
	private final String documentation = "http://www.dirigible.io/help/service_registry_test.html";

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
