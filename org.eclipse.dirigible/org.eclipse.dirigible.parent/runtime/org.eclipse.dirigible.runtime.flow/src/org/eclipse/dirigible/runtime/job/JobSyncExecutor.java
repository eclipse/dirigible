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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.AbstractScriptExecutor;

public class JobSyncExecutor extends AbstractScriptExecutor {
	
	private static final Logger logger = Logger.getLogger(JobSyncExecutor.class);

	private IRepository repository;
	private String[] rootPaths;
	
	private Gson gson = new Gson();

	public JobSyncExecutor(IRepository repository, String... rootPaths) {
		super();
		logger.debug("entering: constructor()");
		this.repository = repository;
		this.rootPaths = rootPaths;
		if (this.rootPaths == null || this.rootPaths.length == 0) {
			this.rootPaths = new String[] { null, null };
		}
		logger.debug("exiting: constructor()");
	}

	@Override
	public Object executeServiceModule(HttpServletRequest request, HttpServletResponse response,
			Object input, String module, Map<Object, Object> executionContext) throws IOException {

		logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module=" + module); //$NON-NLS-1$
		
		if (module == null) {
			throw new IOException("Job module cannot be null");
		}
		
		String result = null; 
		String jobDefinition = new String(retrieveModule(repository, module, "", rootPaths).getContent());
		
		JsonObject jobDefinitionObject = JobParser.parseJob(jobDefinition);
		
		String jobName = jobDefinitionObject.get(JobParser.NODE_NAME).getAsString(); //$NON-NLS-1$
		String jobType = jobDefinitionObject.get(JobParser.NODE_TYPE).getAsString(); //$NON-NLS-1$
		String jobModule = jobDefinitionObject.get(JobParser.NODE_MODULE).getAsString(); //$NON-NLS-1$
		
		Object inputOutput = null;
		
		inputOutput = processJob(request, response, jobModule, executionContext,
				inputOutput, jobType, jobName);

		result = (inputOutput != null) ? inputOutput.toString() : "";
		
		logger.debug("exiting: executeServiceModule()");
		return result;
	}

	private Object processJob(HttpServletRequest request,
			HttpServletResponse response, String module,
			Map<Object, Object> executionContext, Object inputOutput, String jobType, String jobName)
			throws IOException {
		
		try {
			inputOutput = CronJob.executeByEngineType(request, response, module,
					executionContext, jobName, inputOutput, jobType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return inputOutput;
	}

	@Override
	protected void registerDefaultVariable(Object scope, String name,
			Object value) {
		// do nothing
	}
	
	@Override
	protected String getModuleType(String path) {
		return ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES;
	}

}
