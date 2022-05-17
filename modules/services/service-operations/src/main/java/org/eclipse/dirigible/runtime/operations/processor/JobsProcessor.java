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
package org.eclipse.dirigible.runtime.operations.processor;

import static java.text.MessageFormat.format;

import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerManager;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.JobLogDefinition;

public class JobsProcessor {
	
	private SchedulerCoreService schedulerCoreService = new SchedulerCoreService();
	
	public String list() throws SchedulerException {
		
		List<JobDefinition> jobs = schedulerCoreService.getJobs();
		
        return GsonHelper.GSON.toJson(jobs);
	}
	
	public String enable(String name) throws SchedulerException {
		JobDefinition job = schedulerCoreService.getJob(name);
		if (job != null) {
			job.setEnabled(true);
			schedulerCoreService.createOrUpdateJob(job);
			SchedulerManager.scheduleJob(job);
		} else {
			String error = format("Job with name {0} does not exist, hence cannot be enabled", name);
			throw new SchedulerException(error);
		}
		
        return GsonHelper.GSON.toJson(job);
	}
	
	public String disable(String name) throws SchedulerException {
		JobDefinition job = schedulerCoreService.getJob(name);
		if (job != null) {
			job.setEnabled(false);
			schedulerCoreService.createOrUpdateJob(job);
			SchedulerManager.unscheduleJob(job.getName(), job.getGroup());
		} else {
			String error = format("Job with name {0} does not exist, hence cannot be disabled", name);
			throw new SchedulerException(error);
		}
		
        return GsonHelper.GSON.toJson(job);
	}

	public String logs(String name) throws SchedulerException {
		
		List<JobLogDefinition> jobLogs = schedulerCoreService.getJobLogs(name);
		
        return GsonHelper.GSON.toJson(jobLogs);
	}

}
