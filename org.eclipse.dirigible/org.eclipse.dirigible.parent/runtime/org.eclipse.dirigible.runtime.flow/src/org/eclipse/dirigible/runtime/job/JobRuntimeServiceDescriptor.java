package org.eclipse.dirigible.runtime.job;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Job Execution Service
 */
public class JobRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Job Execution"; //$NON-NLS-1$
	private static final String description = "Job Execution Service triggers the forced execution of a specified Job declaration";
	private static final String endpoint = "/job"; //$NON-NLS-1$
	private static final String documentation = "http://www.dirigible.io/help/service_job.html"; //$NON-NLS-1$

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
