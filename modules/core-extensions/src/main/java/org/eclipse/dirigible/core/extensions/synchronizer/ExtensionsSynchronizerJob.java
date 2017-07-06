package org.eclipse.dirigible.core.extensions.synchronizer;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ExtensionsSynchronizerJob implements Job {
	
	private ExtensionsSynchronizer extensionsSynchronizer = StaticInjector.getInjector().getInstance(ExtensionsSynchronizer.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		extensionsSynchronizer.synchronizeAll();
	}

}
