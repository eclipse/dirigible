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
package org.eclipse.dirigible.core.scheduler.manager;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ServiceLoader;

import org.eclipse.dirigible.commons.health.HealthStatus;
import org.eclipse.dirigible.commons.health.HealthStatus.Jobs.JobStatus;
import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.quartz.QuartzDatabaseLayoutInitializer;
import org.eclipse.dirigible.core.scheduler.quartz.SynchronizerDatabaseLayoutInitializer;
import org.eclipse.dirigible.core.scheduler.repository.MasterToRepositoryInitializer;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Scheduler Initializer.
 */
public class SchedulerInitializer {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerInitializer.class);

	private SchedulerCoreService schedulerCoreService = new SchedulerCoreService();

	private QuartzDatabaseLayoutInitializer quartzDatabaseLayoutInitializer = new QuartzDatabaseLayoutInitializer();
	
	private SynchronizerDatabaseLayoutInitializer synchronizerDatabaseLayoutInitializer = new SynchronizerDatabaseLayoutInitializer();
	
	private MasterToRepositoryInitializer masterToRepositoryInitializer = new MasterToRepositoryInitializer();

	/**
	 * Initialize the scheduler.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Schedule internal jobs.
	 */
	private void scheduleInternalJobs() {
		logger.trace("Initializing the Internal Jobs...");
		ServiceLoader<IJobDefinitionProvider> jobDefinitionProviders = ServiceLoader.load(IJobDefinitionProvider.class);
		for (IJobDefinitionProvider next : jobDefinitionProviders) {
			HealthStatus.getInstance().getJobs().setStatus(next.getJobDefinition().getDescription(), JobStatus.Running);
		}
		for (IJobDefinitionProvider next : jobDefinitionProviders) {
			JobDefinition jobDefinition = next.getJobDefinition();
			logger.trace(format("Initializing the Internal Job [{0}] in group [{1}]...", jobDefinition.getDescription(), jobDefinition.getGroup()));
			try {
				JobDefinition found = schedulerCoreService.getJob(jobDefinition.getDescription());
				if (found == null) {
					schedulerCoreService.createOrUpdateJob(jobDefinition);
					scheduleJob(jobDefinition);
				}
			} catch (Throwable e) {
				logger.error(format("Failed installing Internal Job [{0}] in group [{1}].", jobDefinition.getDescription(), jobDefinition.getGroup()), e);
			}
			logger.trace(format("Done installing Internal Job [{0}] in group [{1}].", jobDefinition.getDescription(), jobDefinition.getGroup()));
		}
		logger.trace("Done initializing the Internal Jobs.");

	}

	/**
	 * Schedule system job.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	private void scheduleSystemJob() throws SchedulerException {
		logger.info(format("Initializing the System Job ..."));
		JobDefinition systemJobDefinition = SystemJob.getSystemJobDefinition();
		scheduleJob(systemJobDefinition);
		logger.info(format("Done initializing the System Job."));
	}

	/**
	 * Schedule job.
	 *
	 * @param jobDefinition
	 *            the job definition
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	private void scheduleJob(JobDefinition jobDefinition) throws SchedulerException {
		SchedulerManager.scheduleJob(jobDefinition);
	}

	/**
	 * Start scheduler.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	private void startScheduler() throws SchedulerException {
		SchedulerManager.startScheduler();
	}

	/**
	 * Initialize scheduler.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void initializeScheduler() throws SchedulerException, SQLException, IOException {
		logger.info("Initializing the Scheduler...");
		
		logger.info("Initializing the Quartz database...");
		quartzDatabaseLayoutInitializer.initialize();
		logger.info("Initializing the Quartz database done.");
		
		logger.info("Initializing the Synchronizer database...");
		synchronizerDatabaseLayoutInitializer.initialize();
		logger.info("Initializing the Synchronizer database done.");
		
		logger.info("Initializing the Repository from Master...");
		masterToRepositoryInitializer.initialize();
		logger.info("Initializing the Repository from Master done.");
		
		SchedulerManager.createScheduler();
		logger.info("Initializing the Scheduler done.");
	}

	/**
	 * Shutdown the scheduler.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public static void shutdown() throws SchedulerException {
		SchedulerManager.shutdownScheduler();
	}

}
