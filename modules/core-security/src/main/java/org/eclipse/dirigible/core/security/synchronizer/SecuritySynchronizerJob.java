package org.eclipse.dirigible.core.security.synchronizer;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizer;

public class SecuritySynchronizerJob extends AbstractSynchronizerJob {
	
	private SecuritySynchronizer extensionsSynchronizer = StaticInjector.getInjector().getInstance(SecuritySynchronizer.class);
	
	@Override
	public ISynchronizer getSynchronizer() {
		return extensionsSynchronizer;
	}

}
