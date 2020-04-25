/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.scheduler.api;

import org.eclipse.dirigible.commons.config.HealthStatus;
import org.eclipse.dirigible.commons.config.HealthStatus.Jobs.JobStatus;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * The base class for all the Synchronizer Jobs.
 */
public abstract class AbstractSynchronizerJob implements Job {

	/*
	 * (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		getSynchronizer().synchronize();
		HealthStatus.getInstance().getJobs().setStatus(context.getJobDetail().getKey().getName(), JobStatus.Succeeded);
	}

	/**
	 * Gets the synchronizer.
	 *
	 * @return the synchronizer
	 */
	protected abstract ISynchronizer getSynchronizer();

}
