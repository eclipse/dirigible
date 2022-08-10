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
package org.eclipse.dirigible.api.v3.bpm;

import org.eclipse.dirigible.bpm.api.BpmModule;

/**
 * The Class BpmFacade.
 */
public class BpmFacade {
	
	/**
	 * BPM Engine.
	 *
	 * @return the BPM engine object
	 */
	public static final Object getEngine() {
		return BpmModule.getProcessEngine();
	}
	
	/**
	 * Deploy a BPMN process available in the registry or in the class-path.
	 *
	 * @param location the BPMN resource location
	 * @return the deployment id
	 */
	public static String deployProcess(String location) {
		return BpmModule.getProcessEngineProvider().deployProcess(location);
	}
	
	/**
	 * Undeploy a BPMN process and all its dependencies.
	 *
	 * @param deploymentId the BPMN process definition deployment id
	 */
	public static void undeployProcess(String deploymentId) {
		BpmModule.getProcessEngineProvider().undeployProcess(deploymentId);
	}
	
	/**
	 * Starts a BPMN process by its key and initial parameters.
	 *
	 * @param key the BPMN id of the process
	 * @param parameters the serialized in JSON process initial parameters
	 * @return the process instance id
	 */
	public static String startProcess(String key, String parameters) {
		return BpmModule.getProcessEngineProvider().startProcess(key, parameters);
	}
	
	/**
	 * Get a variable in the process execution context.
	 *
	 * @param processInstanceId the process instance id
	 * @param variableName the variable name
	 * @return the value
	 */
	public static Object getVariable(String processInstanceId, String variableName) {
		return BpmModule.getProcessEngineProvider().getVariable(processInstanceId, variableName);
	}
	
	/**
	 * Set a variable in the process execution context.
	 *
	 * @param processInstanceId the process instance id
	 * @param variableName the variable name
	 * @param value the value object
	 */
	public static void setVariable(String processInstanceId, String variableName, Object value) {
		BpmModule.getProcessEngineProvider().setVariable(processInstanceId, variableName, value);
	}
	
	/**
	 * Remove a variable from the process execution context.
	 *
	 * @param processInstanceId the process instance id
	 * @param variableName the variable name
	 */
	public static void removeVariable(String processInstanceId, String variableName) {
		BpmModule.getProcessEngineProvider().removeVariable(processInstanceId, variableName);
	}
	
	/**
	 * Get all the tasks.
	 *
	 * @return the list of tasks
	 */
	public static String getTasks() {
		return BpmModule.getProcessEngineProvider().getTasks();
	}
	
	/**
	 * Get all the task's variables.
	 *
	 * @param taskId the task id
	 * @return the task's variables
	 */
	public static String getTaskVariables(String taskId) {
		return BpmModule.getProcessEngineProvider().getTaskVariables(taskId);
	}
	
	/**
	 * Set the task's variables.
	 *
	 * @param taskId the task id
	 * @param variables serialized as JSON string
	 */
	public static void getTaskVariables(String taskId, String variables) {
		BpmModule.getProcessEngineProvider().setTaskVariables(taskId, variables);
	}
	
	/**
	 * Complete the task with variables.
	 *
	 * @param taskId the task id
	 * @param variables serialized as JSON string
	 */
	public static void completeTask(String taskId, String variables) {
		BpmModule.getProcessEngineProvider().completeTask(taskId, variables);
	}

}
