package org.eclipse.dirigible.runtime.js;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the JavaScript Registry Service
 */
public class JavaScriptRegistryRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "JavaScript Registry";
	private final String description = "JavaScript Registry Service lists all the services written in JavaScript.";
	private final String endpoint = "/registry-js";
	private final String documentation = "http://www.dirigible.io/help/service_registry_js.html";

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
