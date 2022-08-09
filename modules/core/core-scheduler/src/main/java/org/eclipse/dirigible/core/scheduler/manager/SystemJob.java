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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

/**
 * The System Job.
 */
public class SystemJob implements Job {

	/** The Constant SYSTEM_JOB_NAME. */
	private static final String SYSTEM_JOB_NAME = "dirigible-system-job";

	/** The Constant SYSTEM_GROUP. */
	private static final String SYSTEM_GROUP = "dirigible-system";

	/** The scheduler core service. */
	private SchedulerCoreService schedulerCoreService = new SchedulerCoreService();

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
		try {
			// take all the jobs from the database and schedule them
			List<JobKey> jobs = new ArrayList<JobKey>();
			jobs.add(new JobKey(SYSTEM_JOB_NAME, SYSTEM_GROUP));

			List<JobDefinition> jobDefinitions = schedulerCoreService.getJobs();
			for (JobDefinition jobDefinition : jobDefinitions) {
				if (jobDefinition.isEnabled()) {
					SchedulerManager.scheduleJob(jobDefinition);
					JobKey jobKey = new JobKey(jobDefinition.getName(), jobDefinition.getGroup());
					jobs.add(jobKey);
				}
			}
			// remove all the already scheduled, but removed from the database
			Set<TriggerKey> triggers = SchedulerManager.listJobs();
			for (TriggerKey triggerKey : triggers) {
				JobKey jobKey = new JobKey(triggerKey.getName(), triggerKey.getGroup());
				if (!jobs.contains(jobKey)) {
					SchedulerManager.unscheduleJob(triggerKey.getName(), triggerKey.getGroup());
				}
			}
		} catch (SchedulerException e) {
			throw new JobExecutionException(e);
		}

	}

	/**
	 * Gets the system job definition.
	 *
	 * @return the system job definition
	 */
	public static JobDefinition getSystemJobDefinition() {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName(SYSTEM_JOB_NAME);
		jobDefinition.setGroup(SYSTEM_GROUP);
		jobDefinition.setClazz(SystemJob.class.getCanonicalName());
		jobDefinition.setDescription("System Job");
		jobDefinition.setExpression("0/10 * * * * ?");
		jobDefinition.setSingleton(false);
		return jobDefinition;
	}

}
