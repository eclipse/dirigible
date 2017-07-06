package org.eclipse.dirigible.core.scheduler.api;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class AbstractSynchronizerJob implements Job {
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		getSynchronizer().synchronize();
	}

	protected abstract ISynchronizer getSynchronizer();

}
