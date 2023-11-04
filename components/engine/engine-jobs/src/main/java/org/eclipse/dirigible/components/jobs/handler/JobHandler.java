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
package org.eclipse.dirigible.components.jobs.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.components.jobs.domain.JobLog;
import org.eclipse.dirigible.components.jobs.service.JobLogService;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The built-in scripting service job handler.
 */
public class JobHandler implements Job {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(JobHandler.class);
	
	/**  The handler parameter. */
	public static String JOB_PARAMETER_HANDLER = "dirigible-job-handler";

	/**  The engine type. */
	public static String JOB_PARAMETER_ENGINE = "dirigible-engine-type";
	
    /** The job log service. */
    @Autowired
    private JobLogService jobLogService;
    
    /** The javascript service. */
    @Autowired
    private JavascriptService javascriptService;
	
	/**
	 * Execute.
	 *
	 * @param context the context
	 * @throws JobExecutionException the job execution exception
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String name = context.getJobDetail().getKey().getName();
		String handler = (String) context.getJobDetail().getJobDataMap().get(JOB_PARAMETER_HANDLER);
		String type = (String) context.getJobDetail().getJobDataMap().get(JOB_PARAMETER_ENGINE);
		
		JobLog triggered = registerTriggered(name, handler);
		if (triggered != null) {
			try {
				if (type == null) {
					type = "javascript";
				}
				Map<Object, Object> internal = new HashMap<>();
		    	context.put("handler", handler);
		    	RepositoryPath path = new RepositoryPath(handler);
				javascriptService.handleRequest(path.getSegments()[0], path.constructPathFrom(1), null, internal, false);
			} catch (Exception e) {
				registeredFailed(name, handler, triggered, e);
				
				throw new JobExecutionException(e);
			}
			
			registeredFinished(name, handler, triggered);
		}
	}

	/**
	 * Register triggered.
	 *
	 * @param name the name
	 * @param module the module
	 * @return the job log definition
	 */
	private JobLog registerTriggered(String name, String module) {
		JobLog triggered = null;
		try {
			triggered = jobLogService.jobTriggered(name, module);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}
		return triggered;
	}
	
	/**
	 * Registered failed.
	 *
	 * @param name the name
	 * @param module the module
	 * @param triggered the triggered
	 * @param e the e
	 */
	private void registeredFailed(String name, String module, JobLog triggered, Exception e) {
		try {
			jobLogService.jobFailed(name, module, triggered.getId(), new Date(triggered.getTriggeredAt().getTime()), e.getMessage());
		} catch (Exception se) {
			if (logger.isErrorEnabled()) {logger.error(se.getMessage(), se);}
		}
	}
	
	/**
	 * Registered finished.
	 *
	 * @param name the name
	 * @param module the module
	 * @param triggered the triggered
	 */
	private void registeredFinished(String name, String module, JobLog triggered) {
		try {
			jobLogService.jobFinished(name, module, triggered.getId(), new Date(triggered.getTriggeredAt().getTime()));
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}
	}
	
}
