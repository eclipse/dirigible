package org.eclipse.dirigible.runtime.js;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the JavaScript Execution Service
 */
public class JavaScriptRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "JavaScript Execution";
	private final String description = "JavaScript Execution Service triggers the execution of a specified service written in JavaScript.";
	private final String endpoint = "/js";
	private final String documentation = "http://www.dirigible.io/help/service_javascript.html";

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
