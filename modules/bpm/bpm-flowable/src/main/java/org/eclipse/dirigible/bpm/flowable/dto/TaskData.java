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
import org.flowable.task.api.DelegationState;

public class TaskData {
	
	protected String id;
	protected int revision;
	protected String owner;
	protected int assigneeUpdatedCount;
	protected String originalAssignee;
	protected String assignee;
	protected DelegationState delegationState;
	protected String parentTaskId;
	protected String name;
	protected String localizedName;
	protected String description;
	protected String localizedDescription;
	protected int priority = 50;
	protected Date createTime;
	protected Date dueDate;
	protected int suspensionState = SuspensionState.ACTIVE.getStateCode();
	protected String category;
	protected boolean isIdentityLinksInitialized;
	protected String executionId;
	protected String processInstanceId;
	protected String processDefinitionId;
	protected String scopeId;
	protected String subScopeId;
	protected String scopeType;
	protected String scopeDefinitionId;
	protected String taskDefinitionKey;
	protected String formKey;
	protected boolean isDeleted;
	protected boolean isCanceled;
	private boolean isCountEnabled;
	private int variableCount;
	private int identityLinkCount;
	protected Date claimTime;
	protected String tenantId = "";
	protected String eventName;
	protected String eventHandlerId;
	protected boolean forcedUpdate;
	
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
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public int getAssigneeUpdatedCount() {
		return assigneeUpdatedCount;
	}
	public void setAssigneeUpdatedCount(int assigneeUpdatedCount) {
		this.assigneeUpdatedCount = assigneeUpdatedCount;
	}
	public String getOriginalAssignee() {
		return originalAssignee;
	}
	public void setOriginalAssignee(String originalAssignee) {
		this.originalAssignee = originalAssignee;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public DelegationState getDelegationState() {
		return delegationState;
	}
	public void setDelegationState(DelegationState delegationState) {
		this.delegationState = delegationState;
	}
	public String getParentTaskId() {
		return parentTaskId;
	}
	public void setParentTaskId(String parentTaskId) {
		this.parentTaskId = parentTaskId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocalizedName() {
		return localizedName;
	}
	public void setLocalizedName(String localizedName) {
		this.localizedName = localizedName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocalizedDescription() {
		return localizedDescription;
	}
	public void setLocalizedDescription(String localizedDescription) {
		this.localizedDescription = localizedDescription;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public int getSuspensionState() {
		return suspensionState;
	}
	public void setSuspensionState(int suspensionState) {
		this.suspensionState = suspensionState;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public boolean isIdentityLinksInitialized() {
		return isIdentityLinksInitialized;
	}
	public void setIdentityLinksInitialized(boolean isIdentityLinksInitialized) {
		this.isIdentityLinksInitialized = isIdentityLinksInitialized;
	}
	public String getExecutionId() {
		return executionId;
	}
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public String getScopeId() {
		return scopeId;
	}
	public void setScopeId(String scopeId) {
		this.scopeId = scopeId;
	}
	public String getSubScopeId() {
		return subScopeId;
	}
	public void setSubScopeId(String subScopeId) {
		this.subScopeId = subScopeId;
	}
	public String getScopeType() {
		return scopeType;
	}
	public void setScopeType(String scopeType) {
		this.scopeType = scopeType;
	}
	public String getScopeDefinitionId() {
		return scopeDefinitionId;
	}
	public void setScopeDefinitionId(String scopeDefinitionId) {
		this.scopeDefinitionId = scopeDefinitionId;
	}
	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}
	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}
	public String getFormKey() {
		return formKey;
	}
	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public boolean isCanceled() {
		return isCanceled;
	}
	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}
	public boolean isCountEnabled() {
		return isCountEnabled;
	}
	public void setCountEnabled(boolean isCountEnabled) {
		this.isCountEnabled = isCountEnabled;
	}
	public int getVariableCount() {
		return variableCount;
	}
	public void setVariableCount(int variableCount) {
		this.variableCount = variableCount;
	}
	public Date getClaimTime() {
		return claimTime;
	}
	public void setClaimTime(Date claimTime) {
		this.claimTime = claimTime;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventHandlerId() {
		return eventHandlerId;
	}
	public void setEventHandlerId(String eventHandlerId) {
		this.eventHandlerId = eventHandlerId;
	}
	public boolean isForcedUpdate() {
		return forcedUpdate;
	}
	public void setForcedUpdate(boolean forcedUpdate) {
		this.forcedUpdate = forcedUpdate;
	}
	public int getIdentityLinkCount() {
		return identityLinkCount;
	}
	public void setIdentityLinkCount(int identityLinkCount) {
		this.identityLinkCount = identityLinkCount;
	}

}
