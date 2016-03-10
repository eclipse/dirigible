/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.flow;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.utils.InstanceUtils;
import org.eclipse.dirigible.repository.ext.utils.JsonUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.flow.log.FlowLog;
import org.eclipse.dirigible.runtime.flow.log.FlowLogRecordDAO;
import org.eclipse.dirigible.runtime.scripting.AbstractScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.utils.EngineUtils;

import com.google.gson.Gson;

public class FlowExecutor extends AbstractScriptExecutor {

	private static final String CONDITION_PARAMETER_ANY = "any";

	private static final String CONDITION_PARAMETER_NULL = "null";

	private static final Logger logger = Logger.getLogger(FlowExecutor.class);

	private IRepository repository;
	private String[] rootPaths;

	private static Gson gson = new Gson();

	public FlowExecutor(IRepository repository, String... rootPaths) {
		super();
		logger.debug("entering: constructor()");
		this.repository = repository;
		this.rootPaths = rootPaths;
		if ((this.rootPaths == null) || (this.rootPaths.length == 0)) {
			this.rootPaths = new String[] { null, null };
		}
		logger.debug("exiting: constructor()");
	}

	@Override
	public Object executeServiceModule(HttpServletRequest request, HttpServletResponse response, Object input, String module,
			Map<Object, Object> executionContext) throws IOException {

		logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module=" + module); //$NON-NLS-1$

		if (module == null) {
			throw new IOException("Flow module cannot be null");
		}

		String result = null;
		String flowSource = new String(retrieveModule(repository, module, "", rootPaths).getContent());

		Flow flow = gson.fromJson(flowSource, Flow.class);

		Object resultObject = processFlow(request, response, module, executionContext, flow);

		result = (resultObject != null) ? resultObject.toString() : "";

		logger.debug("exiting: executeServiceModule()");
		return result;
	}

	private Object processFlow(HttpServletRequest request, HttpServletResponse response, String module, Map<Object, Object> executionContext,
			Flow flow) throws IOException {
		executionContext.putAll(flow.getProperties());

		logFlowStep(flow, null, executionContext, FlowLog.STATUS_STARTED, "");

		Object resultObject = null;

		// TODO make extension point
		for (FlowStep flowStep : flow.getSteps()) {
			logFlowStep(flow, flowStep, executionContext, FlowLog.STATUS_STEP_STARTED, "");
			try {
				resultObject = executeByEngineType(request, response, module, executionContext, flow, flowStep);
				logFlowStep(flow, flowStep, executionContext, FlowLog.STATUS_STEP_COMPLETED, "");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logFlowStep(flow, flowStep, executionContext, FlowLog.STATUS_STEP_FAILED, e.getMessage());
				logFlowStep(flow, null, executionContext, FlowLog.STATUS_FAILED, e.getMessage());
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}

		logFlowStep(flow, null, executionContext, FlowLog.STATUS_COMPLETED, "");

		return resultObject;
	}

	private Object executeByEngineType(HttpServletRequest request, HttpServletResponse response, String module, Map<Object, Object> executionContext,
			Flow flow, FlowStep flowStep) throws IOException {

		Object resultObject = null;

		// CONDITION
		if (ICommonConstants.ENGINE_TYPE.CONDITION.equalsIgnoreCase(flowStep.getType())) {

			FlowCase[] cases = flowStep.getCases();
			for (FlowCase flowCase : cases) {
				Object value = executionContext.get(flowCase.getKey());
				if ((value == null) && (request != null)) {
					// the value is not present in the context - try via request
					value = request.getParameter(flowCase.getKey());
				}
				if ((
				// the value is present and it is equal to the parameter
				(value != null) && value.equals(flowCase.getValue())) ||
						// the value is NOT present, but the parameter is "null"
						((flowCase.getValue() != null) && flowCase.getValue().equalsIgnoreCase(CONDITION_PARAMETER_NULL) && (value == null)) ||
						// the value is present and the parameter is "any"
						((flowCase.getValue() != null) && flowCase.getValue().equalsIgnoreCase(CONDITION_PARAMETER_ANY) && (value != null))) {
					resultObject = processFlow(request, response, module, executionContext, flowCase.getFlow());
					break;
				}
			}

			// OUTPUT
		} else if (ICommonConstants.ENGINE_TYPE.OUTPUT.equalsIgnoreCase(flowStep.getType())) {
			if (response != null) {
				response.getWriter().print(flowStep.getMessage());
			} else {
				System.out.println(flowStep.getMessage());
			}
			// ENGINE BY TYPE
		} else {
			Set<String> types = EngineUtils.getTypes();
			for (String type : types) {
				if ((type != null) && type.equalsIgnoreCase(flowStep.getType())) {
					IScriptExecutor scriptExecutor = EngineUtils.createExecutor(type, request);
					scriptExecutor.executeServiceModule(request, response, flowStep.getModule(), executionContext);
					break;
				}
			}
		}

		return resultObject;
	}

	private void logFlowStep(Flow flow, FlowStep flowStep, Map<Object, Object> executionContext, int status, String message) {
		FlowLog flowLog = new FlowLog();
		flowLog.setInstance(InstanceUtils.getInstanceName());
		flowLog.setFlowName(flow.getName());
		flowLog.setFlowUUID(flow.getFlowUUID());
		flowLog.setStepName((flowStep != null) ? flowStep.getName() : "");
		flowLog.setStatus(status);
		flowLog.setMessage(message);
		flowLog.setContext(JsonUtils.mapToJson(executionContext));
		try {
			FlowLogRecordDAO.insert(flowLog);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	protected void registerDefaultVariable(Object scope, String name, Object value) {
		// do nothing
	}

	@Override
	protected String getModuleType(String path) {
		return ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES;
	}
}
