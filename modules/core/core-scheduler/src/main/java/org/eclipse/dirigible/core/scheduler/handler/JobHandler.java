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

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * The built-in scripting service job handler
 */
public class JobHandler implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String module = (String) context.getJobDetail().getJobDataMap().get(ISchedulerCoreService.JOB_PARAMETER_HANDLER);
		String type = (String) context.getJobDetail().getJobDataMap().get(ISchedulerCoreService.JOB_PARAMETER_ENGINE);
		try {
			if (type == null) {
				type = "javascript";
			}
			ScriptEngineExecutorsManager.executeServiceModule(type, module, null);
		} catch (ScriptingException e) {
			throw new JobExecutionException(e);
		}
	}

}
