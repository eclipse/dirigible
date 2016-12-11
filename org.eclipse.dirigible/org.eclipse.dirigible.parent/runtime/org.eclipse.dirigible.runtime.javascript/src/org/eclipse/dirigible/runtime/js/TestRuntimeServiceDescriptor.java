package org.eclipse.dirigible.runtime.js;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Test Execution Service
 */
public class TestRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Test Execution"; //$NON-NLS-1$
	private static final String description = "Test Execution Service triggers the execution of a specified unit test written in JavaScript.";
	private static final String endpoint = "/test"; //$NON-NLS-1$
	private static final String documentation = "http://www.dirigible.io/help/service_test.html"; //$NON-NLS-1$

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
