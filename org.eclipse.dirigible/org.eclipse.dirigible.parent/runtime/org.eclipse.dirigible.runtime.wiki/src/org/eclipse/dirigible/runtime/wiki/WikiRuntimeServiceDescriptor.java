package org.eclipse.dirigible.runtime.wiki;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Wiki Pages Provisioning Service
 */
public class WikiRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Wiki Pages Provisioning"; //$NON-NLS-1$
	private final String description = "Wiki Pages Provisioning Service transforms and provides the requested Wiki page.";
	private final String endpoint = "/wiki"; //$NON-NLS-1$
	private final String documentation = "http://www.dirigible.io/help/service_wiki.html"; //$NON-NLS-1$

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
