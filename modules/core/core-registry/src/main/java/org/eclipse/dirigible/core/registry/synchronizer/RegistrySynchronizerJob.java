package org.eclipse.dirigible.core.registry.synchronizer;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizer;

public class RegistrySynchronizerJob extends AbstractSynchronizerJob {

	private RegistrySynchronizer synchronizer = StaticInjector.getInjector().getInstance(RegistrySynchronizer.class);

	@Override
	protected ISynchronizer getSynchronizer() {
		return synchronizer;
	}

}
