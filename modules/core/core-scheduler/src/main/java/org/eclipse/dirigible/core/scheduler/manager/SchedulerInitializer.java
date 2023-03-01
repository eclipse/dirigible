/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
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

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SchedulerInitializer.class);

	/** The scheduler core service. */
	private SchedulerCoreService schedulerCoreService = new SchedulerCoreService();

	/** The quartz database layout initializer. */
	private QuartzDatabaseLayoutInitializer quartzDatabaseLayoutInitializer = new QuartzDatabaseLayoutInitializer();
	
	/** The synchronizer database layout initializer. */
	private SynchronizerDatabaseLayoutInitializer synchronizerDatabaseLayoutInitializer = new SynchronizerDatabaseLayoutInitializer();
	
	/** The master to repository initializer. */
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

		if (logger.isTraceEnabled()) {logger.trace("Initializing Job Scheduler Service...");}

		initializeScheduler();

		// schedule the System Job
		scheduleSystemJob();

		// enumerate the Internal Jobs
		scheduleInternalJobs();

		startScheduler();

		if (logger.isTraceEnabled()) {logger.trace("Done initializing Job Scheduler Service.");}
	}

	/**
	 * Schedule internal jobs.
	 */
	private void scheduleInternalJobs() {
		if (logger.isTraceEnabled()) {logger.trace("Initializing the Internal Jobs...");}
		ServiceLoader<IJobDefinitionProvider> jobDefinitionProviders = ServiceLoader.load(IJobDefinitionProvider.class);
		for (IJobDefinitionProvider next : jobDefinitionProviders) {
			HealthStatus.getInstance().getJobs().setStatus(next.getJobDefinition().getDescription(), JobStatus.Running);
		}
		for (IJobDefinitionProvider next : jobDefinitionProviders) {
			JobDefinition jobDefinition = next.getJobDefinition();
			if (logger.isTraceEnabled()) {logger.trace(format("Initializing the Internal Job [{0}] in group [{1}]...", jobDefinition.getDescription(), jobDefinition.getGroup()));}
			try {
				JobDefinition found = schedulerCoreService.getJob(jobDefinition.getDescription());
				if (found == null) {
					schedulerCoreService.createOrUpdateJob(jobDefinition);
					scheduleJob(jobDefinition);
				}
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {logger.error(format("Failed installing Internal Job [{0}] in group [{1}].", jobDefinition.getDescription(), jobDefinition.getGroup()), e);}
			}
			if (logger.isTraceEnabled()) {logger.trace(format("Done installing Internal Job [{0}] in group [{1}].", jobDefinition.getDescription(), jobDefinition.getGroup()));}
		}
		if (logger.isTraceEnabled()) {logger.trace("Done initializing the Internal Jobs.");}

	}

	/**
	 * Schedule system job.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	private void scheduleSystemJob() throws SchedulerException {
		if (logger.isInfoEnabled()) {logger.info(format("Initializing the System Job ..."));}
		JobDefinition systemJobDefinition = SystemJob.getSystemJobDefinition();
		scheduleJob(systemJobDefinition);
		if (logger.isInfoEnabled()) {logger.info(format("Done initializing the System Job."));}
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
		if (logger.isInfoEnabled()) {logger.info("Initializing the Scheduler...");}
		
		if (logger.isInfoEnabled()) {logger.info("Initializing the Quartz database...");}
		quartzDatabaseLayoutInitializer.initialize();
		if (logger.isInfoEnabled()) {logger.info("Initializing the Quartz database done.");}
		
		if (logger.isInfoEnabled()) {logger.info("Initializing the Synchronizer database...");}
		synchronizerDatabaseLayoutInitializer.initialize();
		if (logger.isInfoEnabled()) {logger.info("Initializing the Synchronizer database done.");}
		
		if (logger.isInfoEnabled()) {logger.info("Initializing the Repository from Master...");}
		masterToRepositoryInitializer.initialize();
		if (logger.isInfoEnabled()) {logger.info("Initializing the Repository from Master done.");}
		
		SchedulerManager.createScheduler();
		if (logger.isInfoEnabled()) {logger.info("Initializing the Scheduler done.");}
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
