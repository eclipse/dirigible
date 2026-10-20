/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.bpm.flowable.config;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The Inbox's paged listing against a REAL Flowable engine (issue #7977): a page of a
 * candidate-group inbox, with every task's process variables AND identity links joined into the one
 * query, must hold exactly the page's tasks - each with its own variables and its own links - in a
 * stable newest-first order. A joined query that paged over joined ROWS instead of tasks would
 * return fewer tasks than asked, or split one task's variables across pages; only the engine itself
 * can show it does not.
 */
class TaskListingPagingEngineTest {

    private static final String PROCESS_XML =
            """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:flowable="http://flowable.org/bpmn" targetNamespace="http://www.flowable.org/processdef">
                      <process id="approve" name="Approve" isExecutable="true">
                        <startEvent id="start"></startEvent>
                        <userTask id="approveTask" name="Approve" flowable:candidateGroups="Approver,Owner,User"></userTask>
                        <endEvent id="end"></endEvent>
                        <sequenceFlow id="flow_start_task" sourceRef="start" targetRef="approveTask"></sequenceFlow>
                        <sequenceFlow id="flow_task_end" sourceRef="approveTask" targetRef="end"></sequenceFlow>
                      </process>
                    </definitions>
                    """;

    private static final int INSTANCES = 35;

    private static ProcessEngine engine;

    @BeforeAll
    static void startEngine() {
        StandaloneInMemProcessEngineConfiguration configuration = new StandaloneInMemProcessEngineConfiguration();
        configuration.setJdbcUrl("jdbc:h2:mem:task-listing-paging-test;DB_CLOSE_DELAY=-1");
        engine = configuration.buildProcessEngine();
        engine.getRepositoryService()
              .createDeployment()
              .addString("approve.bpmn20.xml", PROCESS_XML)
              .deploy();
        // one open task per instance, each instance with several variables - the joined query has many
        // rows per task, which is exactly what row-paging would get wrong
        for (int i = 0; i < INSTANCES; i++) {
            engine.getRuntimeService()
                  .startProcessInstanceByKey("approve", String.valueOf(i), variables(i));
        }
    }

    @AfterAll
    static void stopEngine() {
        if (engine != null) {
            engine.close();
        }
    }

    private static Map<String, Object> variables(int id) {
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("__entityUrl", "/services/java/billing/gen/billing/api/sales_invoice/SalesInvoiceController");
        variables.put("__entityId", id);
        variables.put("__subjectFields", "Number:text,Total:number");
        variables.put("Customer", "customer-" + id);
        variables.put("Total", id * 10);
        return variables;
    }

    /**
     * The listing query exactly as {@code TaskServiceImpl} builds it for a paged candidate-group inbox.
     */
    private static TaskQuery listing() {
        return (TaskQuery) engine.getTaskService()
                                 .createTaskQuery()
                                 .taskCandidateGroupIn(List.of("Owner", "User"))
                                 .includeProcessVariables()
                                 .includeIdentityLinks()
                                 .orderByTaskCreateTime()
                                 .desc()
                                 .orderByTaskId()
                                 .desc();
    }

    @Test
    void aPageHoldsExactlyItsTasksEachWithItsOwnVariablesAndLinks() {
        List<Task> page = listing().listPage(0, 10);

        assertEquals(10, page.size(), "a page of 10 is 10 tasks, not 10 joined rows");
        for (Task task : page) {
            Map<String, Object> variables = task.getProcessVariables();
            assertEquals(5, variables.size(), "every variable of the task's own instance is loaded");
            String businessKey = engine.getRuntimeService()
                                       .createProcessInstanceQuery()
                                       .processInstanceId(task.getProcessInstanceId())
                                       .singleResult()
                                       .getBusinessKey();
            assertEquals(businessKey, String.valueOf(variables.get("__entityId")), "the variables are the task's own instance's");
            Set<String> groups = task.getIdentityLinks()
                                     .stream()
                                     .map(IdentityLinkInfo::getGroupId)
                                     .collect(Collectors.toSet());
            assertEquals(Set.of("Approver", "Owner", "User"), groups, "the identity links ride the query");
        }
    }

    @Test
    void thePagesCoverTheInboxOnceWithNoOverlap() {
        Set<String> seen = new HashSet<>();
        int pages = 0;
        for (int offset = 0; offset < INSTANCES; offset += 10) {
            List<Task> page = listing().listPage(offset, 10);
            page.forEach(task -> assertTrue(seen.add(task.getId()), "task " + task.getId() + " is on two pages"));
            pages++;
        }
        assertEquals(INSTANCES, seen.size(), "every open task is on exactly one page");
        assertEquals(4, pages);
        assertEquals(INSTANCES, engine.getTaskService()
                                      .createTaskQuery()
                                      .taskCandidateGroupIn(List.of("Owner", "User"))
                                      .count(),
                "the count the X-Total-Count header carries");
    }

    /** The instances of a page come back from ONE query by id. */
    @Test
    void theInstancesOfAPageAreReadTogether() {
        List<Task> page = listing().listPage(0, 10);
        Set<String> ids = page.stream()
                              .map(Task::getProcessInstanceId)
                              .collect(Collectors.toSet());

        List<ProcessInstance> instances = engine.getRuntimeService()
                                                .createProcessInstanceQuery()
                                                .processInstanceIds(ids)
                                                .list();

        assertEquals(ids, instances.stream()
                                   .map(ProcessInstance::getId)
                                   .collect(Collectors.toSet()));
    }
}
