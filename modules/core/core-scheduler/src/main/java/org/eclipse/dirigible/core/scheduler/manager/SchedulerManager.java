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

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.handler.JobHandler;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.utils.PoolingConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Scheduler Manager.
 */
public class SchedulerManager {

	/** The Constant QUARTZ_SIMPL_RAM_JOB_STORE. */
	private static final String QUARTZ_SIMPL_RAM_JOB_STORE = "org.quartz.simpl.RAMJobStore";
	
	/** The Constant PROPERTY_KEY_DATABASE_JOB_STORE. */
	private static final String PROPERTY_KEY_DATABASE_JOB_STORE = "org.quartz.jobStore.class";
	
	/** The Constant PROPERTY_KEY_DATABASE_DATA_SOURCE. */
	private static final String PROPERTY_KEY_DATABASE_DATA_SOURCE = "org.quartz.jobStore.dataSource";
	
	/** The Constant PROPERTY_KEY_DATABASE_DELEGATE. */
	private static final String PROPERTY_KEY_DATABASE_DELEGATE = "org.quartz.jobStore.driverDelegateClass";

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

	/** The scheduler factory. */
	private static SchedulerFactory schedulerFactory = null;

	/** The scheduler. */
	private static Scheduler scheduler = null;
	
	/** The Constant DIRIGIBLE_SCHEDULER_MEMORY_STORE. */
	public static final String DIRIGIBLE_SCHEDULER_MEMORY_STORE = "DIRIGIBLE_SCHEDULER_MEMORY_STORE";
	
	/** The Constant DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_TYPE. */
	public static final String DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_TYPE = "DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_TYPE";
	
	/** The Constant DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_NAME. */
	public static final String DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_NAME = "DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_NAME";

	/** The Constant DIRIGIBLE_SCHEDULER_DATABASE_DRIVER. */
	private static final String DIRIGIBLE_SCHEDULER_DATABASE_DRIVER = "DIRIGIBLE_SCHEDULER_DATABASE_DRIVER";
	
	/** The Constant DIRIGIBLE_SCHEDULER_DATABASE_URL. */
	private static final String DIRIGIBLE_SCHEDULER_DATABASE_URL = "DIRIGIBLE_SCHEDULER_DATABASE_URL";
	
	/** The Constant DIRIGIBLE_SCHEDULER_DATABASE_USER. */
	private static final String DIRIGIBLE_SCHEDULER_DATABASE_USER = "DIRIGIBLE_SCHEDULER_DATABASE_USER";
	
	/** The Constant DIRIGIBLE_SCHEDULER_DATABASE_PASSWORD. */
	private static final String DIRIGIBLE_SCHEDULER_DATABASE_PASSWORD = "DIRIGIBLE_SCHEDULER_DATABASE_PASSWORD";
	
	/** The Constant DIRIGIBLE_SCHEDULER_DATABASE_DELEGATE. */
	private static final String DIRIGIBLE_SCHEDULER_DATABASE_DELEGATE = "DIRIGIBLE_SCHEDULER_DATABASE_DELEGATE";

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
					Configuration.loadModuleConfig("/dirigible-scheduler.properties");
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
					setSchedulerConnectionProperties(quartzProperties);
					schedulerFactory = new StdSchedulerFactory(quartzProperties);
					scheduler = schedulerFactory.getScheduler();
					String message = "Scheduler has been created.";
					if (logger.isInfoEnabled()) {logger.info(message);}
				}
			}
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e);
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
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
				throw new SchedulerException("Scheduler has not been initialized and started.");
			}
			try {
				scheduler.shutdown(true);
				String message = "Scheduler has been shut down.";
				if (logger.isInfoEnabled()) {logger.info(message);}
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
				if (logger.isInfoEnabled()) {logger.info("Scheduler has been started.");}
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
		if (!jobDefinition.isEnabled()) {
			return;
		}
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

				Trigger trigger = null;
				if (!jobDefinition.getExpression().equals("")) {					
					trigger = newTrigger().withIdentity(triggerKey).withSchedule(cronSchedule(jobDefinition.getExpression())).build();
				} else {
					trigger = newTrigger().withIdentity(triggerKey).startNow().build();
				}
				scheduler.scheduleJob(job, trigger);

				if (logger.isInfoEnabled()) {logger.info("Scheduled Job: [{}] of group: [{}] at: [{}]", jobDefinition.getName(), jobDefinition.getGroup(),
						jobDefinition.getExpression());}
			}
		} catch (ObjectAlreadyExistsException e) {
			if (logger.isWarnEnabled()) {logger.warn(e.getMessage());}
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
				if (logger.isInfoEnabled()) {logger.info("Unscheduled Job: [{}] of group: [{}]", name, group);}
			}
		} catch (ObjectAlreadyExistsException e) {
			if (logger.isWarnEnabled()) {logger.warn(e.getMessage());}
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
	 * Checks whether the job with a given name is already scheduled.
	 *
	 * @param name            the name of the job
	 * @return true if registered
	 * @throws SchedulerException             in case of error
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

	/**
	 * Sets the scheduler connection properties.
	 *
	 * @param properties the new scheduler connection properties
	 */
	private static void setSchedulerConnectionProperties(Properties properties) {
		
		String memoryStore = Configuration.get(DIRIGIBLE_SCHEDULER_MEMORY_STORE); 
		if (memoryStore != null && Boolean.parseBoolean(memoryStore)) {
			String jobStorePrefix = "org.quartz.jobStore";
			Iterator iterator = properties.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = (Entry<String, String>) iterator.next();
				if (entry.getKey().startsWith(jobStorePrefix)) {
					iterator.remove();
				}
			}
			setProperty(properties, PROPERTY_KEY_DATABASE_JOB_STORE, QUARTZ_SIMPL_RAM_JOB_STORE);
			return;
		}
		
		String dataSourceName = Configuration.get(DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_NAME);
		if (dataSourceName != null) {
			setProperty(properties, PROPERTY_KEY_DATABASE_DATA_SOURCE, dataSourceName);
		}
		
		String driver = Configuration.get(DIRIGIBLE_SCHEDULER_DATABASE_DRIVER);
		String url = Configuration.get(DIRIGIBLE_SCHEDULER_DATABASE_URL);
		String user = Configuration.get(DIRIGIBLE_SCHEDULER_DATABASE_USER);
		String password = Configuration.get(DIRIGIBLE_SCHEDULER_DATABASE_PASSWORD);
		
		if (driver != null &&
				url != null &&
				user != null &&
				password != null) {
			if ("\"\"".contentEquals(password)) password = "";				
			setProperty(properties, PoolingConnectionProvider.DB_DRIVER, driver);
			setProperty(properties, PoolingConnectionProvider.DB_URL, url);
			setProperty(properties, PoolingConnectionProvider.DB_USER, user);
			setProperty(properties, PoolingConnectionProvider.DB_PASSWORD, password);
		}
		
		String delegate = Configuration.get(DIRIGIBLE_SCHEDULER_DATABASE_DELEGATE);
		if (delegate != null) {
			setProperty(properties, PROPERTY_KEY_DATABASE_DELEGATE, delegate);
		}
	}

	/**
	 * Sets the property.
	 *
	 * @param properties the properties
	 * @param key the key
	 * @param value the value
	 */
	private static void setProperty(Properties properties, String key, String value) {
		if (!StringUtils.isEmpty(value)) {
			properties.setProperty(key, value);
		}
	}
}
