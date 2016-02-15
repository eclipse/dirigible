package org.eclipse.dirigible.runtime.flow;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Flow Execution Service
 */
public class FlowRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Flow Execution";
	private final String description = "Flow Execution Service triggers the execution of a specified Flow declaration.";
	private final String endpoint = "/flow";
	private final String documentation = "http://www.dirigible.io/help/service_flow.html";

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
