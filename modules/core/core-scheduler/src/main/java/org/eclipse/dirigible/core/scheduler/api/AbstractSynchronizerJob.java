/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.scheduler.api;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.dirigible.commons.health.HealthStatus;
import org.eclipse.dirigible.commons.health.HealthStatus.Jobs.JobStatus;
import org.eclipse.dirigible.commons.timeout.TimeLimited;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base class for all the Synchronizer Jobs.
 */
public abstract class AbstractSynchronizerJob implements Job, ISynchronizerJob {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(AbstractSynchronizerJob.class);

	/**
	 * Execute.
	 *
	 * @param context the context
	 * @throws JobExecutionException the job execution exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		final long startTime = System.currentTimeMillis();
		logger.trace("Synchronizer [{}] started execution at: {}...", getName(), new Date(startTime));
		try {
	      TimeLimited.runWithTimeout(new Runnable() {
	        @Override
	        public void run() {
	        	getSynchronizer().synchronize();
	      		HealthStatus.getInstance().getJobs().setStatus(getName(), JobStatus.Succeeded); // context.getJobDetail().getKey().getName()
	      		logger.trace("Synchronizer [{}] execution passed successfully for {} ms...", getName(), System.currentTimeMillis() - startTime);
	        }
	      }, getTimeout(), getTimeoutUnit());
	    } catch (TimeoutException e) {
	    	logger.error("Synchronizer [{}] got timeout during execution at: {}", getName(), new Date(System.currentTimeMillis()));
	    	logger.error(e.getMessage(), e);
	    	HealthStatus.getInstance().getJobs().setStatus(getName(), JobStatus.Failed);
	    } catch(Exception e) {
	    	logger.error("Synchronizer [{}] failed during execution at: {}", getName(), new Date(System.currentTimeMillis()));
	    	logger.error(e.getMessage(), e);
	    	HealthStatus.getInstance().getJobs().setStatus(getName(), JobStatus.Failed);
	    }
		logger.trace("Synchronizer [{}] ended execution for {} ms...", getName(), System.currentTimeMillis() - startTime);
	}

	/**
	 * Gets the timeout.
	 *
	 * @return the timeout
	 */
	protected int getTimeout() {
		return TimeLimited.getTimeout();
	}

	/**
	 * Gets the timeout unit.
	 *
	 * @return the timeout unit
	 */
	protected TimeUnit getTimeoutUnit() {
		return TimeUnit.MINUTES;
	}

}
