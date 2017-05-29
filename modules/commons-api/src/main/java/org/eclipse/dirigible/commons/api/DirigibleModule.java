package org.eclipse.dirigible.commons.api;

import java.util.ServiceLoader;

import com.google.inject.AbstractModule;

public class DirigibleModule extends AbstractModule {

	private static final ServiceLoader<AbstractDirigibleModule> DIRIGIBLE_MODULES = ServiceLoader.load(AbstractDirigibleModule.class);

	@Override
	protected void configure() {
		for (AbstractDirigibleModule next : DIRIGIBLE_MODULES) {
			install(next);
		}
	}

}
