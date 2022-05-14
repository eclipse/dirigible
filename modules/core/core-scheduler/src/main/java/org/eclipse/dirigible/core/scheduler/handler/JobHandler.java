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
package org.eclipse.dirigible.core.scheduler.handler;

import java.util.Date;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerManager;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobLogDefinition;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The built-in scripting service job handler
 */
public class JobHandler implements Job {
	
	private static final Logger logger = LoggerFactory.getLogger(JobHandler.class);
	
	private ISchedulerCoreService schedulerCoreService = new SchedulerCoreService();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String name = context.getJobDetail().getKey().getName();
		String module = (String) context.getJobDetail().getJobDataMap().get(ISchedulerCoreService.JOB_PARAMETER_HANDLER);
		String type = (String) context.getJobDetail().getJobDataMap().get(ISchedulerCoreService.JOB_PARAMETER_ENGINE);
		
		JobLogDefinition triggered = null;
		
		triggered = registerTriggered(name, module, triggered);
		
		try {
			if (type == null) {
				type = "javascript";
			}
			
			ScriptEngineExecutorsManager.executeServiceModule(type, module, null);
		} catch (ScriptingException e) {
			registeredFailed(name, module, triggered, e);
			
			throw new JobExecutionException(e);
		}
		
		registeredFinished(name, module, triggered);
	}

	private JobLogDefinition registerTriggered(String name, String module, JobLogDefinition triggered) {
		try {
			triggered = schedulerCoreService.jobTriggered(name, module);
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
		return triggered;
	}
	
	private void registeredFailed(String name, String module, JobLogDefinition triggered, ScriptingException e) {
		try {
			schedulerCoreService.jobFailed(name, module, triggered.getId(), new Date(triggered.getTriggeredAt().getTime()), e.getMessage());
		} catch (SchedulerException se) {
			logger.error(se.getMessage(), se);
		}
	}
	
	private void registeredFinished(String name, String module, JobLogDefinition triggered) {
		try {
			schedulerCoreService.jobFinished(name, module, triggered.getId(), new Date(triggered.getTriggeredAt().getTime()));
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
