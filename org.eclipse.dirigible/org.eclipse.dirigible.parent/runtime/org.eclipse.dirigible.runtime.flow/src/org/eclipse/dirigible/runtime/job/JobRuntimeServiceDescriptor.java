package org.eclipse.dirigible.runtime.job;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Job Execution Service
 */
public class JobRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Job Execution";
	private final String description = "Job Execution Service triggers the forced execution of a specified Job declaration";
	private final String endpoint = "/job";
	private final String documentation = "http://www.dirigible.io/help/service_job.html";

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
