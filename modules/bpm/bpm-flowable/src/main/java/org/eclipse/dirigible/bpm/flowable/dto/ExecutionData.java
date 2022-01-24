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
package org.eclipse.dirigible.bpm.flowable.dto;

import java.util.Date;

import org.flowable.engine.common.impl.db.SuspensionState;

public class ExecutionData {
	
	protected String id;
	protected int revision;
	protected boolean isInserted;
	protected boolean isUpdated;
	protected boolean isDeleted;
	protected String tenantId = "";
	protected String name;
	protected String description;
	protected String localizedName;
	protected String localizedDescription;
	protected Date lockTime;
	protected boolean isActive = true;
	protected boolean isScope = true;
	protected boolean isConcurrent;
	protected boolean isEnded;
	protected boolean isEventScope;
	protected boolean isMultiInstanceRoot;
	protected boolean isCountEnabled;
	protected String eventName;
	protected String deleteReason;
	protected int suspensionState = SuspensionState.ACTIVE.getStateCode();
	protected String startActivityId;
	protected String startUserId;
	protected Date startTime;
	protected int eventSubscriptionCount;
	protected int taskCount;
	protected int jobCount;
	protected int timerJobCount;
	protected int suspendedJobCount;
	protected int deadLetterJobCount;
	protected int variableCount;
	protected int identityLinkCount;
	protected String processDefinitionId;
	protected String processDefinitionKey;
	protected String processDefinitionName;
	protected Integer processDefinitionVersion;
	protected String deploymentId;
	protected String activityId;
	protected String activityName;
	protected String processInstanceId;
	protected String businessKey;
	protected String parentId;
	protected String superExecutionId;
	protected String rootProcessInstanceId;
	protected boolean forcedUpdate;
	protected String callbackId;
	protected String callbackType;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getRevision() {
		return revision;
	}
	public void setRevision(int revision) {
		this.revision = revision;
	}
	public boolean isInserted() {
		return isInserted;
	}
	public void setInserted(boolean isInserted) {
		this.isInserted = isInserted;
	}
	public boolean isUpdated() {
		return isUpdated;
	}
	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocalizedName() {
		return localizedName;
	}
	public void setLocalizedName(String localizedName) {
		this.localizedName = localizedName;
	}
	public String getLocalizedDescription() {
		return localizedDescription;
	}
	public void setLocalizedDescription(String localizedDescription) {
		this.localizedDescription = localizedDescription;
	}
	public Date getLockTime() {
		return lockTime;
	}
	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isScope() {
		return isScope;
	}
	public void setScope(boolean isScope) {
		this.isScope = isScope;
	}
	public boolean isConcurrent() {
		return isConcurrent;
	}
	public void setConcurrent(boolean isConcurrent) {
		this.isConcurrent = isConcurrent;
	}
	public boolean isEnded() {
		return isEnded;
	}
	public void setEnded(boolean isEnded) {
		this.isEnded = isEnded;
	}
	public boolean isEventScope() {
		return isEventScope;
	}
	public void setEventScope(boolean isEventScope) {
		this.isEventScope = isEventScope;
	}
	public boolean isMultiInstanceRoot() {
		return isMultiInstanceRoot;
	}
	public void setMultiInstanceRoot(boolean isMultiInstanceRoot) {
		this.isMultiInstanceRoot = isMultiInstanceRoot;
	}
	public boolean isCountEnabled() {
		return isCountEnabled;
	}
	public void setCountEnabled(boolean isCountEnabled) {
		this.isCountEnabled = isCountEnabled;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getDeleteReason() {
		return deleteReason;
	}
	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
	}
	public int getSuspensionState() {
		return suspensionState;
	}
	public void setSuspensionState(int suspensionState) {
		this.suspensionState = suspensionState;
	}
	public String getStartActivityId() {
		return startActivityId;
	}
	public void setStartActivityId(String startActivityId) {
		this.startActivityId = startActivityId;
	}
	public String getStartUserId() {
		return startUserId;
	}
	public void setStartUserId(String startUserId) {
		this.startUserId = startUserId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public int getEventSubscriptionCount() {
		return eventSubscriptionCount;
	}
	public void setEventSubscriptionCount(int eventSubscriptionCount) {
		this.eventSubscriptionCount = eventSubscriptionCount;
	}
	public int getTaskCount() {
		return taskCount;
	}
	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}
	public int getJobCount() {
		return jobCount;
	}
	public void setJobCount(int jobCount) {
		this.jobCount = jobCount;
	}
	public int getTimerJobCount() {
		return timerJobCount;
	}
	public void setTimerJobCount(int timerJobCount) {
		this.timerJobCount = timerJobCount;
	}
	public int getSuspendedJobCount() {
		return suspendedJobCount;
	}
	public void setSuspendedJobCount(int suspendedJobCount) {
		this.suspendedJobCount = suspendedJobCount;
	}
	public int getDeadLetterJobCount() {
		return deadLetterJobCount;
	}
	public void setDeadLetterJobCount(int deadLetterJobCount) {
		this.deadLetterJobCount = deadLetterJobCount;
	}
	public int getVariableCount() {
		return variableCount;
	}
	public void setVariableCount(int variableCount) {
		this.variableCount = variableCount;
	}
	public int getIdentityLinkCount() {
		return identityLinkCount;
	}
	public void setIdentityLinkCount(int identityLinkCount) {
		this.identityLinkCount = identityLinkCount;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}
	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}
	public String getProcessDefinitionName() {
		return processDefinitionName;
	}
	public void setProcessDefinitionName(String processDefinitionName) {
		this.processDefinitionName = processDefinitionName;
	}
	public Integer getProcessDefinitionVersion() {
		return processDefinitionVersion;
	}
	public void setProcessDefinitionVersion(Integer processDefinitionVersion) {
		this.processDefinitionVersion = processDefinitionVersion;
	}
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getSuperExecutionId() {
		return superExecutionId;
	}
	public void setSuperExecutionId(String superExecutionId) {
		this.superExecutionId = superExecutionId;
	}
	public String getRootProcessInstanceId() {
		return rootProcessInstanceId;
	}
	public void setRootProcessInstanceId(String rootProcessInstanceId) {
		this.rootProcessInstanceId = rootProcessInstanceId;
	}
	public boolean isForcedUpdate() {
		return forcedUpdate;
	}
	public void setForcedUpdate(boolean forcedUpdate) {
		this.forcedUpdate = forcedUpdate;
	}
	public String getCallbackId() {
		return callbackId;
	}
	public void setCallbackId(String callbackId) {
		this.callbackId = callbackId;
	}
	public String getCallbackType() {
		return callbackType;
	}
	public void setCallbackType(String callbackType) {
		this.callbackType = callbackType;
	}
	
}
