package org.eclipse.dirigible.runtime.git.module;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;

public class GitModule extends AbstractDirigibleModule {

	private static final String MODULE_NAME = "Git Module";

	@Override
	protected void configure() {
		Configuration.load("/dirigible-git.properties");
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

}
