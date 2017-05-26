package org.eclipse.dirigible.commons.api;

import java.util.ServiceLoader;

public class DirigibleModule extends AbstractDirigibleModule {

	private static final ServiceLoader<AbstractDirigibleModule> DIRIGIBLE_MODULES = ServiceLoader.load(AbstractDirigibleModule.class);

	@Override
	protected void configure() {
		for (AbstractDirigibleModule next : DIRIGIBLE_MODULES) {
			install(next);
		}
	}

}
