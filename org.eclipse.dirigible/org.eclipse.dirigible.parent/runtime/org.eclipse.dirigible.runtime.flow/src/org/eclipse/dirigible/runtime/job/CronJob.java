/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.job;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.ext.utils.InstanceUtils;
import org.eclipse.dirigible.repository.ext.utils.JsonUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.job.log.JobLog;
import org.eclipse.dirigible.runtime.job.log.JobLogRecordDAO;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.utils.EngineUtils;
// import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.Gson;

public class CronJob implements org.quartz.Job {

	private static final Logger logger = Logger.getLogger(CronJob.class);

	private static Gson gson = new Gson();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("Starting Job...");
		String instName = context.getJobDetail().getName();
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String jobName = dataMap.getString(JobParser.NODE_NAME);
		String jobType = dataMap.getString(JobParser.NODE_TYPE);
		String jobDescription = dataMap.getString(JobParser.NODE_DESCRIPTION);
		String jobExpression = dataMap.getString(JobParser.NODE_EXPRESSION);
		String jobModule = dataMap.getString(JobParser.NODE_MODULE);

		Job job = new Job();
		job.setName(jobName);
		job.setDescription(jobDescription);
		job.setExpression(jobExpression);
		job.setModule(jobModule);
		job.setType(jobType);

		logger.debug(String.format("Job processing name: %s, type: %s, module: %s ...", instName, jobType, jobModule));
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		Object inputOutput = null;
		try {
			inputOutput = executeByEngineType(null, null, jobModule, executionContext, job, inputOutput);
		} catch (Throwable t) {
			logger.error(String.format("Error in job name: %s, type: %s, module: %s.", job.getName(), jobType, jobModule), t);
		}
		logger.debug(String.format("Job name: %s, type: %s, module: %s finished.", job.getName(), jobType, jobModule));
	}

	public static Object executeByEngineType(HttpServletRequest request, HttpServletResponse response, String module,
			Map<Object, Object> executionContext, Job job, Object inputOutput) {

		logJob(job, executionContext, JobLog.STATUS_STARTED, "");

		try {
			Set<String> types = EngineUtils.getTypes();
			for (String type : types) {
				if ((type != null) && type.equalsIgnoreCase(job.getType())) {
					IScriptExecutor scriptExecutor = EngineUtils.createExecutor(type, request);
					scriptExecutor.executeServiceModule(request, response, module, executionContext);
					break;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			logJob(job, executionContext, JobLog.STATUS_FAILED, e.getMessage());
		}

		logJob(job, executionContext, JobLog.STATUS_COMPLETED, "");

		return inputOutput;
	}

	private static void logJob(Job job, Map<Object, Object> executionContext, int status, String message) {
		JobLog jobLog = new JobLog();
		jobLog.setInstance(InstanceUtils.getInstanceName());
		jobLog.setJobName(job.getName());
		jobLog.setJobUUID(job.getJobUUID());
		jobLog.setStatus(status);
		jobLog.setMessage(message);
		jobLog.setContext(JsonUtils.mapToJson(executionContext));
		try {
			JobLogRecordDAO.insert(jobLog);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

}
