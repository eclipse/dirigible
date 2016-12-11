package org.eclipse.dirigible.runtime.content;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Data Export Service
 */
public class DataExportRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Data Export"; //$NON-NLS-1$
	private static final String description = "Data Export service helps in exporting the data of a table in delimiter separated values file (*.dsv).";
	private static final String endpoint = "/data-export"; //$NON-NLS-1$
	private static final String documentation = "http://www.dirigible.io/help/service_data_export.html"; //$NON-NLS-1$

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
