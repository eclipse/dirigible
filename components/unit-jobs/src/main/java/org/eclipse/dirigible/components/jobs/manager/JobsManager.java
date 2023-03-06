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
package org.eclipse.dirigible.components.jobs.manager;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Set;

import org.eclipse.dirigible.components.jobs.handler.JobHandler;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Scheduler Manager.
 */
@Component
public class JobsManager {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(JobsManager.class);

	/** The scheduler. */
	@Autowired
	private Scheduler scheduler;
	
	/**  The internal jobs. */
	public static String JOB_GROUP_INTERNAL = "dirigible-internal";

	/**  The user defined jobs. */
	public static String JOB_GROUP_DEFINED = "dirigible-defined";

	/**
	 * Schedule a job.
	 *
	 * @param jobDefinition the job definition
	 * @throws Exception the exception
	 */
	public void scheduleJob(org.eclipse.dirigible.components.jobs.domain.Job jobDefinition) throws Exception {
		if (!jobDefinition.isEnabled()) {
			return;
		}
		try {
			JobKey jobKey = new JobKey(jobDefinition.getName(), jobDefinition.getGroup());
			TriggerKey triggerKey = new TriggerKey(jobDefinition.getName(), jobDefinition.getGroup());
			if (!scheduler.checkExists(jobKey) && (!scheduler.checkExists(triggerKey))) {
				JobDetail job;
				if (!JOB_GROUP_DEFINED.equals(jobDefinition.getGroup())) {
					// internal jobs
					Class<Job> jobClass = (Class<Job>) Class.forName(jobDefinition.getClazz());
					job = newJob(jobClass).withIdentity(jobKey).withDescription(jobDefinition.getDescription()).build();
				} else {
					// user defined jobs
					job = newJob(JobHandler.class).withIdentity(jobKey).withDescription(jobDefinition.getDescription()).build();
					job.getJobDataMap().put(JobHandler.JOB_PARAMETER_HANDLER, jobDefinition.getHandler());
					job.getJobDataMap().put(JobHandler.JOB_PARAMETER_ENGINE, jobDefinition.getEngine());
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
			throw new Exception("Invalid class name for the job", e);
		} catch (org.quartz.SchedulerException e) {
			throw new Exception(e);
		}
	}

	/**
	 * Unschedule a job.
	 *
	 * @param name the name
	 * @param group the group
	 * @throws Exception the exception
	 */
	public void unscheduleJob(String name, String group) throws Exception {
		if (!JOB_GROUP_DEFINED.equals(group)) {
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
			throw new Exception(e);
		}
	}

	/**
	 * List all the jobs.
	 *
	 * @return the sets the
	 * @throws Exception the exception
	 */
	public Set<TriggerKey> listJobs() throws Exception {
		try {
			Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
			return triggerKeys;
		} catch (org.quartz.SchedulerException e) {
			throw new Exception(e);
		}
	}

	/**
	 * Checks whether the job with a given name is already scheduled.
	 *
	 * @param name            the name of the job
	 * @return true if registered
	 * @throws Exception the exception
	 */
	public boolean existsJob(String name) throws Exception {
		Set<TriggerKey> triggerKeys = listJobs();
		for (TriggerKey triggerKey : triggerKeys) {
			if (triggerKey.getName().equals(name) && JOB_GROUP_DEFINED.equals(triggerKey.getGroup())) {
				return true;
			}
		}
		return false;
	}

}
