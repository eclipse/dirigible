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

	constructor(public data: any) {
		this.data = data;
	}

	public getId(): number {
		return this.data.id;
	}
	
	public isActive(): boolean {
		return this.data.isActive;
	}
	
	public isScope(): boolean {
		return this.data.isScope;
	}

	public isConcurrent(): boolean {
		return this.data.isConcurrent;
	}

	public isEnded(): boolean {
		return this.data.isEnded;
	}

	public isEventScope(): boolean {
		return this.data.isEventScope;
	}

	public isMultiInstanceRoot(): boolean {
		return this.data.isMultiInstanceRoot;
	}

	public isCountEnabled(): boolean {
		return this.data.isCountEnabled;
	}

	public suspensionState() {
		return this.data.suspensionState;
	}
	
	public startTime() {
		return this.data.startTime;
	}
	
	public eventSubscriptionCount(): number {
		return this.data.eventSubscriptionCount;
	}
	
	public taskCount():number {
		return this.data.taskCount;
	}
	
	public jobCount(): number {
		return this.data.jobCount;
	}
	
	public timerJobCount(): number {
		return this.data.timerJobCount;
	}
	
	public suspendedJobCount(): number {
		return this.data.suspendedJobCount;
	}
	
	public deadLetterJobCount(): number {
		return this.data.deadLetterJobCount;
	}

	public variableCount(): number {
		return this.data.variableCount;
	}

	public identityLinkCount(): number {
		return this.data.identityLinkCount;
	}

	public processDefinitionId(): number {
		return this.data.processDefinitionId;
	}

	public processDefinitionKey() {
		return this.data.processDefinitionKey;
	}

	public activityId(): number {
		return this.data.activityId;
	}

	public processInstanceId(): number {
		return this.data.processInstanceId;
	}

	public parentId(): number {
		return this.data.parentId;
	}

	public rootProcessInstanceId(): number {
		return this.data.rootProcessInstanceId;
	}

	public forcedUpdate() {
		return this.data.forcedUpdate;
	}

	public revision() {
		return this.data.revision;
	}

	public tenantId() {
		return this.data.tenantId;
	}

}

export function getVariable(processInstanceId: string, variableName: string): object {
	var variableValue = BpmFacade.getVariable(processInstanceId, variableName);
	return variableValue;
}

export function setVariable(processInstanceId: string, variableName: string, variableValue: object): void {
	BpmFacade.setVariable(processInstanceId, variableName, variableValue);
}

export function removeVariable(processInstanceId: string, variableName: string): void {
	BpmFacade.removeVariable(processInstanceId, variableName);
}
