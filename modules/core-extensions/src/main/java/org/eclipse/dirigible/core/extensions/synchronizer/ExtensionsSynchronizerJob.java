package org.eclipse.dirigible.core.extensions.synchronizer;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizer;

public class ExtensionsSynchronizerJob extends AbstractSynchronizerJob {
	
	private ExtensionsSynchronizer extensionsSynchronizer = StaticInjector.getInjector().getInstance(ExtensionsSynchronizer.class);
	
	@Override
	public ISynchronizer getSynchronizer() {
		return extensionsSynchronizer;
	}

}
