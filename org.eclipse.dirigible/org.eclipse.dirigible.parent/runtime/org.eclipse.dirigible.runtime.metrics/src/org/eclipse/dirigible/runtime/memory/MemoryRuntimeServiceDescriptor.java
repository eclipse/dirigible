package org.eclipse.dirigible.runtime.memory;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Memory Service
 */
public class MemoryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Memory"; //$NON-NLS-1$
	private final String description = "Memory Service gives the current values of the memory related metrics";
	private final String endpoint = "/memory"; //$NON-NLS-1$
	private final String documentation = "http://www.dirigible.io/help/service_memory.html"; //$NON-NLS-1$

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
