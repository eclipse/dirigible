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
/**
 * API Process
 */

exports.getProcessEngine = function() {
	var processEngine = new ProcessEngine();
	var native = org.eclipse.dirigible.api.v3.bpm.BpmFacade.getProcessEngine();
	processEngine.native = native;
	return processEngine;
};

/**
 * ProcessEngine object
 */
function ProcessEngine() {
	
}

exports.start = function(key, parameters) {
	var processParameters = parameters ? parameters : {};
	var processInstanceId = org.eclipse.dirigible.api.v3.bpm.BpmFacade.startProcess(key, JSON.stringify(processParameters));
	return processInstanceId;
};

exports.getExecutionContext = function() {
	var data = JSON.parse(__context.get('execution'));
	var execution = new ExecutionContext(data);
	return execution;
};

/**
 * ExecutionContext object
 */
function ExecutionContext(data) {
	
	this.getId = function() {
		return data.id;
	};
	this.isActive = function() {
		return data.isActive;
	};
	this.isScope = function() {
		return data.isScope;
	};
	this.isConcurrent = function() {
		return data.isConcurrent;
	};
	this.isEnded = function() {
		return data.isEnded;
	};
	this.isEventScope = function() {
		return data.isEventScope;
	};
	this.isMultiInstanceRoot = function() {
		return data.isMultiInstanceRoot;
	};
	this.isCountEnabled = function() {
		return data.isCountEnabled;
	};
	this.suspensionState = function() {
		return data.suspensionState;
	};
	this.startTime = function() {
		return data.startTime;
	};
	this.eventSubscriptionCount = function() {
		return data.eventSubscriptionCount;
	};
	this.taskCount = function() {
		return data.taskCount;
	};
	this.jobCount = function() {
		return data.jobCount;
	};
	this.timerJobCount = function() {
		return data.timerJobCount;
	};
	this.suspendedJobCount = function() {
		return data.suspendedJobCount;
	};
	this.deadLetterJobCount = function() {
		return data.deadLetterJobCount;
	};
	this.variableCount = function() {
		return data.variableCount;
	};
	this.identityLinkCount = function() {
		return data.identityLinkCount;
	};
	this.processDefinitionId = function() {
		return data.processDefinitionId;
	};
	this.processDefinitionKey = function() {
		return data.processDefinitionKey;
	};
	this.activityId = function() {
		return data.activityId;
	};
	this.processInstanceId = function() {
		return data.processInstanceId;
	};
	this.parentId = function() {
		return data.parentId;
	};
	this.rootProcessInstanceId = function() {
		return data.rootProcessInstanceId;
	};
	this.forcedUpdate = function() {
		return data.forcedUpdate;
	};
	this.revision = function() {
		return data.revision;
	};
	this.tenantId = function() {
		return data.tenantId;
	};
	
}

exports.getVariable = function(processInstanceId, variableName) {
	var variableValue = org.eclipse.dirigible.api.v3.bpm.BpmFacade.getVariable(processInstanceId, variableName);
	return variableValue;
};

exports.setVariable = function(processInstanceId, variableName, variableValue) {
	org.eclipse.dirigible.api.v3.bpm.BpmFacade.setVariable(processInstanceId, variableName, variableValue);
};

exports.removeVariable = function(processInstanceId, variableName) {
	org.eclipse.dirigible.api.v3.bpm.BpmFacade.removeVariable(processInstanceId, variableName);
};
