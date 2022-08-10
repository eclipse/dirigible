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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.api.v3.job.JobFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.helpers.NameValuePair;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.JobEmailDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.JobLogDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.JobParameterDefinition;
import org.quartz.JobExecutionException;

/**
 * The Class JobsProcessor.
 */
public class JobsProcessor {
	
	/** The scheduler core service. */
	private SchedulerCoreService schedulerCoreService = new SchedulerCoreService();
	
	/**
	 * List.
	 *
	 * @return the string
	 * @throws SchedulerException the scheduler exception
	 */
	public String list() throws SchedulerException {
		
		List<JobDefinition> jobs = schedulerCoreService.getJobs();
		
        return GsonHelper.GSON.toJson(jobs);
	}
	
	/**
	 * Enable.
	 *
	 * @param name the name
	 * @return the string
	 * @throws SchedulerException the scheduler exception
	 */
	public String enable(String name) throws SchedulerException {
        return JobFacade.enable(name);
	}
	
	/**
	 * Disable.
	 *
	 * @param name the name
	 * @return the string
	 * @throws SchedulerException the scheduler exception
	 */
	public String disable(String name) throws SchedulerException {
		return JobFacade.disable(name);
	}

	/**
	 * Logs.
	 *
	 * @param name the name
	 * @return the string
	 * @throws SchedulerException the scheduler exception
	 */
	public String logs(String name) throws SchedulerException {
		List<JobLogDefinition> jobLogs = schedulerCoreService.getJobLogs(name);
		
        return GsonHelper.GSON.toJson(jobLogs);
	}
	
	/**
	 * Clear.
	 *
	 * @param name the name
	 * @throws SchedulerException the scheduler exception
	 */
	public void clear(String name) throws SchedulerException {
		schedulerCoreService.clearJobLogs(name);
	}
	
	/**
	 * Parameters.
	 *
	 * @param name the name
	 * @return the string
	 * @throws SchedulerException the scheduler exception
	 */
	public String parameters(String name) throws SchedulerException {
		
		List<JobParameterDefinition> parameters = schedulerCoreService.getJobParameters(name);
		for (JobParameterDefinition parameter : parameters) {
			parameter.setValue(Configuration.get(parameter.getName(), parameter.getDefaultValue()));
		}
		
        return GsonHelper.GSON.toJson(parameters);
	}
	
	/**
	 * Trigger.
	 *
	 * @param name the name
	 * @param parameters the parameters
	 * @return true, if successful
	 * @throws JobExecutionException the job execution exception
	 * @throws SchedulerException the scheduler exception
	 */
	public boolean trigger(String name, List<NameValuePair> parameters) throws JobExecutionException, SchedulerException {
		
		Map<String, String> parametersMap = new HashMap<String, String>();
		
		for (NameValuePair pair : parameters) {
			parametersMap.put(pair.getName(), pair.getValue());
		}
		
		return JobFacade.trigger(name, GsonHelper.GSON.toJson(parametersMap));
	}

	/**
	 * Emails.
	 *
	 * @param name the name
	 * @return the string
	 * @throws SchedulerException the scheduler exception
	 */
	public String emails(String name) throws SchedulerException {
		List<JobEmailDefinition> jobEmails = schedulerCoreService.getJobEmails(name);
		return GsonHelper.GSON.toJson(jobEmails);
	}

	/**
	 * Adds the email.
	 *
	 * @param name the name
	 * @param email the email
	 * @throws SchedulerException the scheduler exception
	 */
	public void addEmail(String name, String email) throws SchedulerException {
		schedulerCoreService.addJobEmail(name, email);
	}

	/**
	 * Removes the email.
	 *
	 * @param id the id
	 * @throws SchedulerException the scheduler exception
	 */
	public void removeEmail(Long id) throws SchedulerException {
		schedulerCoreService.removeJobEmail(id);
	}

}
