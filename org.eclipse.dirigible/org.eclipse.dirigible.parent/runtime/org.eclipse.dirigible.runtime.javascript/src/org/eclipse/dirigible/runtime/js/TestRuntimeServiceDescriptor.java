package org.eclipse.dirigible.runtime.js;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Test Execution Service
 */
public class TestRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Test Execution";
	private final String description = "Test Execution Service triggers the execution of a specified unit test written in JavaScript.";
	private final String endpoint = "/test";
	private final String documentation = "http://www.dirigible.io/help/service_test.html";

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
