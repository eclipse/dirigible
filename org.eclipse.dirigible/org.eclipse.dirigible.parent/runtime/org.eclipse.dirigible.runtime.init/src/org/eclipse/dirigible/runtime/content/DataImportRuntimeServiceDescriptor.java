package org.eclipse.dirigible.runtime.content;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Data Import Service
 */
public class DataImportRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Data Import"; //$NON-NLS-1$
	private final String description = "Data Import service provide the end-point for importing data of a table in delimiter separated values file (*.dsv).";
	private final String endpoint = "/data-import"; //$NON-NLS-1$
	private final String documentation = "http://www.dirigible.io/help/service_data_import.html"; //$NON-NLS-1$

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
