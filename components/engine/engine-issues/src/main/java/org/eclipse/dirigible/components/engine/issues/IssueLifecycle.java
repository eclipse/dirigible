/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.issues;

import java.util.Map;

import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * The issue lifecycle on the platform's Flowable engine (#7948).
 *
 * <p>
 * The process is the platform's, not a tenant's: it is deployed once, into the default tenant, and
 * every issue's instance runs there whichever tenant filed it - its tasks are the instance
 * operators' work, and they work in the default tenant. The deployment filters duplicates, so a
 * restart deploys a new version only when the definition changed.
 */
@Component
class IssueLifecycle implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueLifecycle.class);

    /** The process definition key. */
    static final String PROCESS_KEY = "dirigible-issue";

    /** The triage task. */
    static final String TRIAGE_TASK = "triage";

    /** The work task. */
    static final String WORK_TASK = "work";

    /** The wait for the reporter's answer. */
    static final String AWAIT_REPORTER = "awaitReporter";

    /** The issue id variable. */
    static final String VARIABLE_ISSUE_ID = "issueId";

    /** The variable naming the tenant the issue was filed in. */
    static final String VARIABLE_ISSUE_TENANT = "issueTenantId";

    /** The variable a task's outcome is completed with. */
    static final String VARIABLE_DECISION = "decision";

    private static final String DEPLOYMENT_NAME = "dirigible-issues";

    private static final String RESOURCE = "dirigible-issues/issue-lifecycle.bpmn";

    private final ProcessEngine processEngine;

    private final Tenant defaultTenant;

    IssueLifecycle(ProcessEngine processEngine, @DefaultTenant Tenant defaultTenant) {
        this.processEngine = processEngine;
        this.defaultTenant = defaultTenant;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String deploymentId = processEngine.getRepositoryService()
                                           .createDeployment()
                                           .name(DEPLOYMENT_NAME)
                                           .key(DEPLOYMENT_NAME)
                                           .tenantId(defaultTenant.getId())
                                           .addClasspathResource(RESOURCE)
                                           .enableDuplicateFiltering()
                                           .deploy()
                                           .getId();
        LOGGER.info("The issue lifecycle [{}] is deployment [{}] in tenant [{}]", PROCESS_KEY, deploymentId, defaultTenant.getId());
    }

    /**
     * Starts an issue's lifecycle.
     *
     * @param issue the issue, already saved
     * @return the process instance id
     */
    String start(Issue issue) {
        return processEngine.getRuntimeService()
                            .startProcessInstanceByKeyAndTenantId(PROCESS_KEY, "issue-" + issue.getId(),
                                    Map.of(VARIABLE_ISSUE_ID, issue.getId(), VARIABLE_ISSUE_TENANT, issue.getTenantId()),
                                    defaultTenant.getId())
                            .getId();
    }

    /**
     * Completes the issue's waiting task with an outcome.
     *
     * @param issue the issue
     * @param taskKey the task the issue must be waiting in
     * @param decision the outcome
     */
    void complete(Issue issue, String taskKey, IssueDecision decision) {
        Task task = activeTask(issue, taskKey);
        IssueTaskCompletionGuard.allow(() -> {
            processEngine.getTaskService()
                         .complete(task.getId(), Map.of(VARIABLE_DECISION, decision.name()));
            return null;
        });
    }

    /**
     * Assigns the issue's waiting task, so it moves into the engineer's own Inbox.
     *
     * @param issue the issue
     * @param assignee the engineer
     */
    void assign(Issue issue, String assignee) {
        Task task = processEngine.getTaskService()
                                 .createTaskQuery()
                                 .processInstanceId(issue.getProcessInstanceId())
                                 .taskTenantId(defaultTenant.getId())
                                 .singleResult();
        if (task != null) {
            processEngine.getTaskService()
                         .setAssignee(task.getId(), assignee);
        }
    }

    /**
     * Hands the issue back to work after the reporter answered.
     *
     * @param issue the issue, waiting for its reporter
     */
    void resume(Issue issue) {
        Execution waiting = processEngine.getRuntimeService()
                                         .createExecutionQuery()
                                         .processInstanceId(issue.getProcessInstanceId())
                                         .activityId(AWAIT_REPORTER)
                                         .singleResult();
        if (waiting == null) {
            throw new IllegalStateException("The process of issue [" + issue.getId() + "] is not waiting for its reporter");
        }
        processEngine.getRuntimeService()
                     .trigger(waiting.getId());
    }

    /**
     * Ends the lifecycle of a withdrawn issue. The history keeps the instance.
     *
     * @param issue the issue
     * @param reason why
     */
    void cancel(Issue issue, String reason) {
        LOGGER.info("Deleting the process instance [{}] of issue [{}]: {}", issue.getProcessInstanceId(), issue.getId(), reason);
        processEngine.getRuntimeService()
                     .deleteProcessInstance(issue.getProcessInstanceId(), reason);
    }

    private Task activeTask(Issue issue, String taskKey) {
        Task task = processEngine.getTaskService()
                                 .createTaskQuery()
                                 .processInstanceId(issue.getProcessInstanceId())
                                 .taskDefinitionKey(taskKey)
                                 .taskTenantId(defaultTenant.getId())
                                 .singleResult();
        if (task == null) {
            throw new IllegalStateException("The process of issue [" + issue.getId() + "] has no active [" + taskKey + "] task");
        }
        return task;
    }
}
