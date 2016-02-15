package org.eclipse.dirigible.runtime.content;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Clone Export Service
 */
public class CloneExportRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Clone Export";
	private final String description = "Clone Export Service provides capability to download the full Repository content as a ZIP archive.";
	private final String endpoint = "/clone-export";
	private final String documentation = "http://www.dirigible.io/help/service_clone_export.html";

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
