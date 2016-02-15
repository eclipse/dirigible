package org.eclipse.dirigible.runtime.java;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Java Registry Service
 */
public class JavaRegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Java Registry";
	private final String description = "Java Registry Service lists all the services written in Java.";
	private final String endpoint = "/registry-java";
	private final String documentation = "http://www.dirigible.io/help/service_registry_java.html";

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
