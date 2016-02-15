package org.eclipse.dirigible.runtime.job;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Job Registry Service
 */
public class JobRegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Job Registry";
	private final String description = "Job Registry Service lists all the Job declarations.";
	private final String endpoint = "/registry-job";
	private final String documentation = "http://www.dirigible.io/help/service_registry_job.html";

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
