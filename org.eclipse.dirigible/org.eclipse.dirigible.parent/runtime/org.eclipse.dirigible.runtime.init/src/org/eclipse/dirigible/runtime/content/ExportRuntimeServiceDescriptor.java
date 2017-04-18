package org.eclipse.dirigible.runtime.content;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Export Service
 */
public class ExportRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Export"; //$NON-NLS-1$
	private final String description = "Export Service provides capability to download the whole Registry content as a ZIP archive.";
	private final String endpoint = "/export"; //$NON-NLS-1$
	private final String documentation = "http://www.dirigible.io/help/service_export.html"; //$NON-NLS-1$

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
