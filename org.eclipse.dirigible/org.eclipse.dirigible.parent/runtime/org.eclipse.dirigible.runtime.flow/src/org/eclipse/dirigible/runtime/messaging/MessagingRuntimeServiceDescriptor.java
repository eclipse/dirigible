package org.eclipse.dirigible.runtime.messaging;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Messaging Registry Service
 */
public class MessagingRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Messaging Registry";
	private final String description = "Messaging Service provide a passive message hub functionality.";
	private final String endpoint = "/message";
	private final String documentation = "http://www.dirigible.io/help/service_messaging.html";

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
