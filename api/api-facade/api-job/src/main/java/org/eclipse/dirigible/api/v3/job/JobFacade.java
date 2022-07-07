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
package org.eclipse.dirigible.api.v3.job;

import static java.text.MessageFormat.format;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerManager;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.quartz.JobExecutionException;

public class JobFacade {
	
	private static ISchedulerCoreService schedulerCoreService = new SchedulerCoreService();
	
	public static String getJobs() throws SchedulerException {
		return GsonHelper.GSON.toJson(schedulerCoreService.getJobs());
	}
	
	public static String getJob(String name) throws SchedulerException {
		return GsonHelper.GSON.toJson(schedulerCoreService.getJob(name));
	}
	
	public static String enable(String name) throws SchedulerException {
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
	
	public static String disable(String name) throws SchedulerException {
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
	
	public static boolean trigger(String name, String parameters) throws JobExecutionException, SchedulerException {
		Map<String, String> parametersMap = GsonHelper.GSON.fromJson(parameters, Map.class);
		JobDefinition job = schedulerCoreService.getJob(name);
		if (job != null) {
			Map<String, String> memento = new HashMap<String, String>();
			try {
				for (Map.Entry<String, String> entry : parametersMap.entrySet()) {
					memento.put(entry.getKey(), Configuration.get(entry.getKey()));
					Configuration.set(entry.getKey(), entry.getValue());
				}
				
				String engine = job.getEngine();
				String handler = job.getHandler();
				try {
					ScriptEngineExecutorsManager.executeServiceModule(engine, handler, null);
				} catch (ScriptingException e) {
					throw new JobExecutionException(e);
				}
			} finally {
				for (Map.Entry<String, String> entry : memento.entrySet()) {
					Configuration.set(entry.getKey(), entry.getValue());
				}
			}
		} else {
			String error = format("Job with name {0} does not exist, hence cannot be triggered", name);
			throw new JobExecutionException(error);
		}
		
        return true;
	}
	
	public static void log(String name, String message) throws SchedulerException {
		JobDefinition job = schedulerCoreService.getJob(name);
		if (job != null) {
			String handler = job.getHandler();
			schedulerCoreService.jobLogged(name, handler, message);
		} else {
			String error = format("Job with name {0} does not exist, hence cannot be used to log messages", name);
			throw new SchedulerException(error);
		}
	}
	
	public static void error(String name, String message) throws SchedulerException {
		JobDefinition job = schedulerCoreService.getJob(name);
		if (job != null) {
			String handler = job.getHandler();
			schedulerCoreService.jobLoggedError(name, handler, message);
		} else {
			String error = format("Job with name {0} does not exist, hence cannot be used to log messages", name);
			throw new SchedulerException(error);
		}
	}
	
	public static void warn(String name, String message) throws SchedulerException {
		JobDefinition job = schedulerCoreService.getJob(name);
		if (job != null) {
			String handler = job.getHandler();
			schedulerCoreService.jobLoggedWarning(name, handler, message);
		} else {
			String error = format("Job with name {0} does not exist, hence cannot be used to log messages", name);
			throw new SchedulerException(error);
		}
	}
	
	public static void info(String name, String message) throws SchedulerException {
		JobDefinition job = schedulerCoreService.getJob(name);
		if (job != null) {
			String handler = job.getHandler();
			schedulerCoreService.jobLoggedInfo(name, handler, message);
		} else {
			String error = format("Job with name {0} does not exist, hence cannot be used to log messages", name);
			throw new SchedulerException(error);
		}
	}
	
}
