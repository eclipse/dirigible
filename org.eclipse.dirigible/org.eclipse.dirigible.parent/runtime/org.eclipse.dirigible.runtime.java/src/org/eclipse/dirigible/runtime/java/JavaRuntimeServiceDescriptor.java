package org.eclipse.dirigible.runtime.java;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Java Execution Service
 */
public class JavaRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Java Execution";
	private final String description = "Java Execution Service triggers the execution of a specified service written in Java.";
	private final String endpoint = "/java";
	private final String documentation = "http://www.dirigible.io/help/service_java.html";

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
