package org.eclipse.dirigible.runtime.messaging;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Messaging Registry Service
 */
public class MessagingRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Messaging Registry"; //$NON-NLS-1$
	private static final String description = "Messaging Service provide a passive message hub functionality.";
	private static final String endpoint = "/message"; //$NON-NLS-1$
	private static final String documentation = "http://www.dirigible.io/help/service_messaging.html"; //$NON-NLS-1$

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
