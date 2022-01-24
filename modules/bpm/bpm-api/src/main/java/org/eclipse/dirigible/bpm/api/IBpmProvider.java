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
package org.eclipse.dirigible.bpm.api;

/**
 * The Business Process Management interface implemented by all the BPM providers
 *
 */
public interface IBpmProvider {

	public static final String DIRIGIBLE_BPM_PROVIDER = "DIRIGIBLE_BPM_PROVIDER"; //$NON-NLS-1$

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType();

	/**
	 * Getter for the underlying process engine object
	 * 
	 * @return the process engine
	 */
	public Object getProcessEngine();
	
	/**
	 * Deploy a process definition
	 * 
	 * @param location the process definition location
	 * 
	 * @return the deployment id
	 */
	public String deployProcess(String location);
	
	/**
	 * Undeploy a process definition by the deployment id
	 * 
	 * @param deploymentId the process definition deployment id
	 */
	public void undeployProcess(String deploymentId);
	
	/**
	 * Start a process by its key
	 * 
	 * @param key the process definition's key
	 * @param parameters serialized as JSON string
	 * @return the process instance id
	 */
	public String startProcess(String key, String parameters);
	
	/**
	 * Get a variable in the process execution context
	 * 
	 * @param processInstanceId the process instance id
	 * @param variableName the variable name
	 * @return the value
	 */
	public Object getVariable(String processInstanceId, String variableName);
	
	/**
	 * Set a variable in the process execution context
	 * 
	 * @param processInstanceId the process instance id
	 * @param variableName the variable name
	 * @param variableValue the variable value object
	 */
	public void setVariable(String processInstanceId, String variableName, Object variableValue);
	
	/**
	 * Remove a variable from the process execution context
	 * 
	 * @param processInstanceId the process instance id
	 * @param variableName the variable name
	 */
	public void removeVariable(String processInstanceId, String variableName);
	
	/**
	 * Get all the tasks
	 * 
	 * @return the tasks
	 */
	public String getTasks();
	
	/**
	 * Get all the task's variables
	 * 
	 * @param taskId the task id
	 * @return the task's variables
	 */
	public String getTaskVariables(String taskId);
	
	/**
	 * Set the task's variables
	 * 
	 * @param taskId the task id
	 * @param variables serialized as JSON string
	 */
	public void setTaskVariables(String taskId, String variables);
	
	/**
	 * Complete the task with variables
	 * 
	 * @param taskId the task id
	 * @param variables serialized as JSON string
	 */
	public void completeTask(String taskId, String variables);

}
