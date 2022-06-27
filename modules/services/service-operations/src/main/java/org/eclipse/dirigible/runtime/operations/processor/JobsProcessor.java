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
import org.eclipse.dirigible.core.scheduler.service.definition.JobLogDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.JobParameterDefinition;
import org.quartz.JobExecutionException;

public class JobsProcessor {
	
	private SchedulerCoreService schedulerCoreService = new SchedulerCoreService();
	
	public String list() throws SchedulerException {
		
		List<JobDefinition> jobs = schedulerCoreService.getJobs();
		
        return GsonHelper.GSON.toJson(jobs);
	}
	
	public String enable(String name) throws SchedulerException {
        return JobFacade.enable(name);
	}
	
	public String disable(String name) throws SchedulerException {
		return JobFacade.disable(name);
	}

	public String logs(String name) throws SchedulerException {
		List<JobLogDefinition> jobLogs = schedulerCoreService.getJobLogs(name);
		
        return GsonHelper.GSON.toJson(jobLogs);
	}
	
	public void clear(String name) throws SchedulerException {
		schedulerCoreService.clearJobLogs(name);
	}
	
	public String parameters(String name) throws SchedulerException {
		
		List<JobParameterDefinition> parameters = schedulerCoreService.getJobParameters(name);
		for (JobParameterDefinition parameter : parameters) {
			parameter.setValue(Configuration.get(parameter.getName(), parameter.getDefaultValue()));
		}
		
        return GsonHelper.GSON.toJson(parameters);
	}
	
	public boolean trigger(String name, List<NameValuePair> parameters) throws JobExecutionException, SchedulerException {
		
		Map<String, String> parametersMap = new HashMap<String, String>();
		
		for (NameValuePair pair : parameters) {
			parametersMap.put(pair.getName(), pair.getValue());
		}
		
		return JobFacade.trigger(name, GsonHelper.GSON.toJson(parametersMap));
	}

}
