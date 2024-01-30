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
/**
 * API Process
 */

const BpmFacade = Java.type("org.eclipse.dirigible.components.api.bpm.BpmFacade");

export function getProcessEngine() {
	var processEngine = new ProcessEngine();
	var native = BpmFacade.getProcessEngine();
	processEngine.native = native;
	return processEngine;
}

/**
 * ProcessEngine object
 */
function ProcessEngine() {

}

export function start(key, parameters) {
	var processParameters = parameters ? parameters : {}
	var processInstanceId = BpmFacade.startProcess(key, JSON.stringify(processParameters));
	return processInstanceId;
}

export function getExecutionContext() {
	var data = JSON.parse(__context.get('execution'));
	var execution = new ExecutionContext(data);
	return execution;
}

/**
 * ExecutionContext object
 */
class ExecutionContext {

	constructor(private data) { }

	getId() {
		return this.data.id;
	}
	isActive() {
		return this.data.isActive;
	}
	isScope() {
		return this.data.isScope;
	}
	isConcurrent() {
		return this.data.isConcurrent;
	}
	isEnded() {
		return this.data.isEnded;
	}
	isEventScope() {
		return this.data.isEventScope;
	}
	isMultiInstanceRoot() {
		return this.data.isMultiInstanceRoot;
	}
	isCountEnabled() {
		return this.data.isCountEnabled;
	}
	suspensionState() {
		return this.data.suspensionState;
	}
	startTime() {
		return this.data.startTime;
	}
	eventSubscriptionCount() {
		return this.data.eventSubscriptionCount;
	}
	taskCount() {
		return this.data.taskCount;
	}
	jobCount() {
		return this.data.jobCount;
	}
	timerJobCount() {
		return this.data.timerJobCount;
	}
	suspendedJobCount() {
		return this.data.suspendedJobCount;
	}
	deadLetterJobCount() {
		return this.data.deadLetterJobCount;
	}
	variableCount() {
		return this.data.variableCount;
	}
	identityLinkCount() {
		return this.data.identityLinkCount;
	}
	processDefinitionId() {
		return this.data.processDefinitionId;
	}
	processDefinitionKey() {
		return this.data.processDefinitionKey;
	}
	activityId() {
		return this.data.activityId;
	}
	processInstanceId() {
		return this.data.processInstanceId;
	}
	parentId() {
		return this.data.parentId;
	}
	rootProcessInstanceId() {
		return this.data.rootProcessInstanceId;
	}
	forcedUpdate() {
		return this.data.forcedUpdate;
	}
	revision() {
		return this.data.revision;
	}
	tenantId() {
		return this.data.tenantId;
	}

}

export function getVariable(processInstanceId, variableName) {
	var variableValue = BpmFacade.getVariable(processInstanceId, variableName);
	return variableValue;
}

export function setVariable(processInstanceId, variableName, variableValue) {
	BpmFacade.setVariable(processInstanceId, variableName, variableValue);
}

export function removeVariable(processInstanceId, variableName) {
	BpmFacade.removeVariable(processInstanceId, variableName);
}
