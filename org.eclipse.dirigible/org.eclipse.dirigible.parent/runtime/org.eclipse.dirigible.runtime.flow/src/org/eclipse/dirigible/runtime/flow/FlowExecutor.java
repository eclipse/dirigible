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

package org.eclipse.dirigible.runtime.flow;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.AbstractScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.utils.EngineUtils;

public class FlowExecutor extends AbstractScriptExecutor {
	
	private static final String CONDITION_PARAMETER_ANY = "any";

	private static final String CONDITION_PARAMETER_NULL = "null";

	private static final Logger logger = Logger.getLogger(FlowExecutor.class);

	private IRepository repository;
	private String[] rootPaths;
	
	private Gson gson = new Gson();

	public FlowExecutor(IRepository repository, String... rootPaths) {
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
			throw new IOException("Flow module cannot be null");
		}
		
		String result = null; 
		String flowSource = new String(retrieveModule(repository, module, "", rootPaths).getContent());
		
		Flow flow = gson.fromJson(flowSource, Flow.class);
		
		Object inputOutput = null;
		
		inputOutput = processFlow(request, response, module, executionContext,
				flow, inputOutput);

		result = (inputOutput != null) ? inputOutput.toString() : "";
		
		logger.debug("exiting: executeServiceModule()");
		return result;
	}

	private Object processFlow(HttpServletRequest request,
			HttpServletResponse response, String module,
			Map<Object, Object> executionContext, Flow flow, Object inputOutput)
			throws IOException {
		executionContext.putAll(flow.getProperties());
		
		// TODO make extension point
		for (FlowStep flowStep : flow.getSteps()) {
			try {
				inputOutput = executeByEngineType(request, response, module,
						executionContext, flow, inputOutput, flowStep);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		return inputOutput;
	}

	private Object executeByEngineType(HttpServletRequest request,
			HttpServletResponse response, String module,
			Map<Object, Object> executionContext, Flow flow,
			Object inputOutput, FlowStep flowStep) throws IOException {
		
		
		// CONDITION
		if (ICommonConstants.ENGINE_TYPE.CONDITION.equalsIgnoreCase(flowStep.getType())) {
			
			FlowCase[] cases = flowStep.getCases();
			for (FlowCase flowCase : cases) {
				Object value = executionContext.get(flowCase.getKey());
				if (value == null
						&& request != null) {
					// the value is not present in the context - try via request
					value = request.getParameter(flowCase.getKey());
				}
				if ((
						// the value is present and it is equal to the parameter
						value != null
						&& value.equals(flowCase.getValue()))
						||
						// the value is NOT present, but the parameter is "null"
						(flowCase.getValue() != null
						&& flowCase.getValue().equalsIgnoreCase(CONDITION_PARAMETER_NULL)
						&& value == null)
						||
						// the value is present and the parameter is "any"
						(flowCase.getValue() != null
						&& flowCase.getValue().equalsIgnoreCase(CONDITION_PARAMETER_ANY)
						&& value != null)) {
					processFlow(request, response, module, executionContext, flowCase.getFlow(), inputOutput);
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
				if (type != null
						&& type.equalsIgnoreCase(flowStep.getType())) {
					IScriptExecutor scriptExecutor = EngineUtils.createExecutor(type, request);
					scriptExecutor.executeServiceModule(request, response, flowStep.getModule(), executionContext);
					break;
				}
			}
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
