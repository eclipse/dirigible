package org.eclipse.dirigible.core.publisher.synchronizer;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizer;

public class PublisherSynchronizerJob extends AbstractSynchronizerJob {
	
	private PublisherSynchronizer extensionsSynchronizer = StaticInjector.getInjector().getInstance(PublisherSynchronizer.class);
	
	@Override
	public ISynchronizer getSynchronizer() {
		return extensionsSynchronizer;
	}

}
