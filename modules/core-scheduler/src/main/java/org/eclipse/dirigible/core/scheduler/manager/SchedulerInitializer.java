/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.scheduler.manager;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.quartz.QuartzDatabaseLayoutInitializer;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerInitializer {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerInitializer.class);

	@Inject
	private SchedulerCoreService schedulerCoreService;

	@Inject
	private QuartzDatabaseLayoutInitializer quartzDatabaseLayoutInitializer;

	public void initialize() throws SchedulerException, SQLException, IOException {

		logger.trace("Initializing Job Scheduler Service...");

		initializeScheduler();

		// schedule the System Job
		scheduleSystemJob();

		// enumerate the Internal Jobs
		scheduleInternalJobs();

		startScheduler();

		logger.trace("Done initializing Job Scheduler Service.");
	}

	private void scheduleInternalJobs() {
		logger.trace("Initializing the Internal Jobs...");
		ServiceLoader<IJobDefinitionProvider> jobDefinitionProviders = ServiceLoader.load(IJobDefinitionProvider.class);
		for (IJobDefinitionProvider next : jobDefinitionProviders) {
			JobDefinition jobDefinition = next.getJobDefinition();
			logger.trace(format("Initializing the Internal Job [{0}] in group [{1}]...", jobDefinition.getName(), jobDefinition.getGroup()));
			try {
				JobDefinition found = schedulerCoreService.getJob(jobDefinition.getName());
				if (found == null) {
					schedulerCoreService.createJob(jobDefinition);
					scheduleJob(jobDefinition);
				}
			} catch (Throwable e) {
				logger.error(format("Failed installing Internal Job [{0}] in group [{1}].", jobDefinition.getName(), jobDefinition.getGroup()), e);
			}
			logger.trace(format("Done installing Internal Job [{0}] in group [{1}].", jobDefinition.getName(), jobDefinition.getGroup()));
		}
		logger.trace("Done initializing the Internal Jobs.");

	}

	private void scheduleSystemJob() throws SchedulerException {
		logger.info(format("Initializing the System Job ..."));
		JobDefinition systemJobDefinition = SystemJob.getSystemJobDefinition();
		scheduleJob(systemJobDefinition);
		logger.info(format("Done initializing the System Job."));
	}

	private void scheduleJob(JobDefinition jobDefinition) throws SchedulerException {
		SchedulerManager.scheduleJob(jobDefinition);
	}

	private void startScheduler() throws SchedulerException {
		SchedulerManager.startScheduler();
	}

	private void initializeScheduler() throws SchedulerException, SQLException, IOException {
		quartzDatabaseLayoutInitializer.initialize();
		SchedulerManager.createScheduler();
	}

	public static void shutdown() throws SchedulerException {
		SchedulerManager.shutdownScheduler();
	}

}
