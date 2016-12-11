package org.eclipse.dirigible.runtime.flow;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Flow Execution Service
 */
public class FlowRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Flow Execution"; //$NON-NLS-1$
	private static final String description = "Flow Execution Service triggers the execution of a specified Flow declaration.";
	private static final String endpoint = "/flow"; //$NON-NLS-1$
	private static final String documentation = "http://www.dirigible.io/help/service_flow.html"; //$NON-NLS-1$

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
