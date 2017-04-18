package org.eclipse.dirigible.runtime.metrics;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Access Log Service
 */
public class AccessLogRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Access Log"; //$NON-NLS-1$
	private final String description = "Access Log Service provides management and monitoring capabilities for the access log";
	private final String endpoint = "/acclog"; //$NON-NLS-1$
	private final String documentation = "http://www.dirigible.io/help/service_accesslog.html"; //$NON-NLS-1$

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
