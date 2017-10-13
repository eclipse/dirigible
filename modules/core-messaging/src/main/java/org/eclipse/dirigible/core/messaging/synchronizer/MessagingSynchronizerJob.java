package org.eclipse.dirigible.core.messaging.synchronizer;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizer;

public class MessagingSynchronizerJob extends AbstractSynchronizerJob {
	
	private MessagingSynchronizer messagingSynchronizer = StaticInjector.getInjector().getInstance(MessagingSynchronizer.class);
	
	@Override
	public ISynchronizer getSynchronizer() {
		return messagingSynchronizer;
	}

}
