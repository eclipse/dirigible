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

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.handler.JobHandler;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Scheduler Manager.
 */
public class SchedulerManager {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

	private static SchedulerFactory schedulerFactory = null;

	private static Scheduler scheduler = null;

	/**
	 * Gets the scheduler.
	 *
	 * @return the scheduler
	 */
	public static Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * Creates the scheduler.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public static void createScheduler() throws SchedulerException {
		try {
			synchronized (SchedulerInitializer.class) {
				if (scheduler == null) {
					Configuration.load("/dirigible-scheduler.properties");
					Properties quartzProperties = new Properties();
					String quartzConfig = Configuration.get("DIRIGIBLE_SCHEDULER_QUARTZ_PROPERTIES");
					if ((quartzConfig != null) && "".equals(quartzConfig.trim()) && !quartzConfig.startsWith("classpath")) {
						FileReader reader = null;
						try {
							reader = new FileReader(quartzConfig);
							quartzProperties.load(reader);
						} finally {
							if (reader != null) {
								reader.close();
							}
						}
					} else {
						InputStream in = null;
						try {
							in = SchedulerManager.class.getResourceAsStream("/quartz.properties");
							quartzProperties.load(in);
						} finally {
							if (in != null) {
								in.close();
							}
						}
					}
					schedulerFactory = new StdSchedulerFactory(quartzProperties);
					scheduler = schedulerFactory.getScheduler();
					String message = "Scheduler has been created.";
					logger.info(message);
				}
			}
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Removes the scheduler.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public static void removeScheduler() throws SchedulerException {
		shutdownScheduler();
	}

	/**
	 * Shutdown the scheduler.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public static void shutdownScheduler() throws SchedulerException {
		synchronized (SchedulerInitializer.class) {
			if (scheduler == null) {
				throw new SchedulerException("Scheduler has not been initialized and strated.");
			}
			try {
				scheduler.shutdown(true);
				String message = "Scheduler has been shutted down.";
				logger.info(message);
				scheduler = null;
			} catch (org.quartz.SchedulerException e) {
				throw new SchedulerException(e);
			}
		}
	}

	/**
	 * Start the scheduler.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public static void startScheduler() throws SchedulerException {
		synchronized (SchedulerInitializer.class) {
			if (scheduler == null) {
				throw new SchedulerException("Scheduler has not been initialized.");
			}
			try {
				scheduler.start();
				logger.info("Scheduler has been started.");
			} catch (org.quartz.SchedulerException e) {
				throw new SchedulerException(e);
			}
		}
	}

	/**
	 * Schedule a job.
	 *
	 * @param jobDefinition
	 *            the job definition
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public static void scheduleJob(JobDefinition jobDefinition) throws SchedulerException {
		try {
			JobKey jobKey = new JobKey(jobDefinition.getName(), jobDefinition.getGroup());
			TriggerKey triggerKey = new TriggerKey(jobDefinition.getName(), jobDefinition.getGroup());
			if (!scheduler.checkExists(jobKey) && (!scheduler.checkExists(triggerKey))) {
				JobDetail job;
				if (!ISchedulerCoreService.JOB_GROUP_DEFINED.equals(jobDefinition.getGroup())) {
					// internal jobs
					Class<Job> jobClass = (Class<Job>) Class.forName(jobDefinition.getClazz());
					job = newJob(jobClass).withIdentity(jobKey).withDescription(jobDefinition.getDescription()).build();
				} else {
					// user defined jobs
					job = newJob(JobHandler.class).withIdentity(jobKey).withDescription(jobDefinition.getDescription()).build();
					job.getJobDataMap().put(ISchedulerCoreService.JOB_PARAMETER_HANDLER, jobDefinition.getHandler());
					job.getJobDataMap().put(ISchedulerCoreService.JOB_PARAMETER_ENGINE, jobDefinition.getEngine());
				}

				CronTrigger trigger = newTrigger().withIdentity(triggerKey).withSchedule(cronSchedule(jobDefinition.getExpression())).build();

				scheduler.scheduleJob(job, trigger);

				logger.info("Scheduled Job: [{}] of group: [{}] at: [{}]", jobDefinition.getName(), jobDefinition.getGroup(),
						jobDefinition.getExpression());
			}
		} catch (ObjectAlreadyExistsException e) {
			logger.warn(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new SchedulerException("Invalid class name for the job", e);
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e);
		}
	}

	/**
	 * Unschedule a job.
	 *
	 * @param name
	 *            the name
	 * @param group
	 *            the group
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public static void unscheduleJob(String name, String group) throws SchedulerException {
		if (!ISchedulerCoreService.JOB_GROUP_DEFINED.equals(group)) {
			return;
		}
		try {
			JobKey jobKey = new JobKey(name, group);
			TriggerKey triggerKey = new TriggerKey(name, group);
			if (scheduler.checkExists(triggerKey)) {
				scheduler.unscheduleJob(triggerKey);
				scheduler.deleteJob(jobKey);
				logger.info("Unscheduled Job: [{}] of group: [{}]", name, group);
			}
		} catch (ObjectAlreadyExistsException e) {
			logger.warn(e.getMessage());
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e);
		}
	}

	/**
	 * List all the jobs.
	 *
	 * @return the sets the
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public static Set<TriggerKey> listJobs() throws SchedulerException {
		try {
			Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
			return triggerKeys;
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e);
		}
	}

	/**
	 * Checks whether the job with a given name is already scheduled
	 *
	 * @param name
	 *            the name of the job
	 * @return true if registered
	 * @throws SchedulerException
	 *             in case of error
	 */
	public static boolean existsJob(String name) throws SchedulerException {
		Set<TriggerKey> triggerKeys = listJobs();
		for (TriggerKey triggerKey : triggerKeys) {
			if (triggerKey.getName().equals(name) && ISchedulerCoreService.JOB_GROUP_DEFINED.equals(triggerKey.getGroup())) {
				return true;
			}
		}
		return false;
	}

}
