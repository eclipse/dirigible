/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.job;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.utils.EngineUtils;

public class CronJob implements Job {
	
	private static final Logger logger = Logger.getLogger(CronJob.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.debug("Starting Job...");
		String instName = context.getJobDetail().getName();
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String jobType = dataMap.getString(JobParser.NODE_TYPE);
		String jobModule = dataMap.getString(JobParser.NODE_MODULE);
		logger.debug(String.format("Job processing name: %s, type: %s, module: %s ...", instName, jobType, jobModule));
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		Object inputOutput = null;
		try {
			inputOutput = executeByEngineType(null, null, jobModule, executionContext, instName, inputOutput, jobType);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug(String.format("Job name: %s, type: %s, module: %s finished.", instName, jobType, jobModule));
	}
	
	
	public static Object executeByEngineType(HttpServletRequest request,
			HttpServletResponse response, String module,
			Map<Object, Object> executionContext, String jobName,
			Object inputOutput, String scriptType) throws IOException {
		
		Set<String> types = EngineUtils.getTypes();
		for (String type : types) {
			if (type != null
					&& type.equalsIgnoreCase(scriptType)) {
				IScriptExecutor scriptExecutor = EngineUtils.createExecutor(type, request);
				scriptExecutor.executeServiceModule(request, response, module, executionContext);
			}
		}
		
		return inputOutput;
	}

}
