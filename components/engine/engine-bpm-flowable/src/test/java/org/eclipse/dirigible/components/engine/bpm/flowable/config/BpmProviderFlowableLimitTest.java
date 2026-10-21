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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.repository.api.IRepository;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The Processes perspective asks every monitoring list for 100 rows, and the lists used to answer
 * with all of them - every running instance, every finished one (history only grows), every
 * variable (dirigible #7982). Against a real engine: a limited listing is bounded in the database
 * and ordered, and a listing without a limit stays whole.
 */
class BpmProviderFlowableLimitTest {

    private static final String TENANT_ID = "limit-tenant";

    private static final String WAITING = """
            <?xml version="1.0" encoding="UTF-8"?>
            <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" targetNamespace="http://www.flowable.org/processdef">
              <process id="waiting" name="Waiting" isExecutable="true">
                <startEvent id="start"></startEvent>
                <userTask id="approve" name="Approve"></userTask>
                <endEvent id="end"></endEvent>
                <sequenceFlow id="f1" sourceRef="start" targetRef="approve"></sequenceFlow>
                <sequenceFlow id="f2" sourceRef="approve" targetRef="end"></sequenceFlow>
              </process>
            </definitions>
            """;

    private static final String INSTANT = """
            <?xml version="1.0" encoding="UTF-8"?>
            <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" targetNamespace="http://www.flowable.org/processdef">
              <process id="instant" name="Instant" isExecutable="true">
                <startEvent id="start"></startEvent>
                <endEvent id="end"></endEvent>
                <sequenceFlow id="f1" sourceRef="start" targetRef="end"></sequenceFlow>
              </process>
            </definitions>
            """;

    private static final int RUNNING = 12;
    private static final int FINISHED = 8;

    private static ProcessEngine engine;
    private static BpmProviderFlowable bpmProvider;
    private static String someInstance;

    @BeforeAll
    static void startEngine() {
        StandaloneInMemProcessEngineConfiguration configuration = new StandaloneInMemProcessEngineConfiguration();
        configuration.setJdbcUrl("jdbc:h2:mem:bpm-provider-limit-test;DB_CLOSE_DELAY=-1");
        engine = configuration.buildProcessEngine();
        for (String xml : List.of(WAITING, INSTANT)) {
            engine.getRepositoryService()
                  .createDeployment()
                  .addString((xml == WAITING ? "waiting" : "instant") + ".bpmn20.xml", xml)
                  .tenantId(TENANT_ID)
                  .deploy();
        }
        for (int i = 0; i < RUNNING; i++) {
            ProcessInstance instance = engine.getRuntimeService()
                                             .startProcessInstanceByKeyAndTenantId("waiting", "doc-" + i,
                                                     Map.of("c", i, "a", "first", "b", true), TENANT_ID);
            someInstance = instance.getId();
        }
        for (int i = 0; i < FINISHED; i++) {
            engine.getRuntimeService()
                  .startProcessInstanceByKeyAndTenantId("instant", "done-" + i, TENANT_ID);
        }
        bpmProvider = new BpmProviderFlowable(mock(IRepository.class), tenantContext(), mock(FlowableArtefactsValidator.class), engine);
    }

    @AfterAll
    static void stopEngine() {
        if (engine != null) {
            engine.close();
        }
    }

    @Test
    void theRunningInstancesAreBoundedToTheLimit() {
        assertEquals(5, bpmProvider.getProcessInstances(Optional.empty(), Optional.empty(), 5)
                                   .size());
        assertEquals(RUNNING, bpmProvider.getProcessInstances(Optional.empty(), Optional.empty(), null)
                                         .size(),
                "without a limit every running instance, as before");
        assertEquals(RUNNING, bpmProvider.getProcessInstances(Optional.empty(), Optional.empty())
                                         .size());
    }

    @Test
    void theFinishedInstancesAreBoundedToTheLimit() {
        assertEquals(3, bpmProvider.getCompletedProcessInstances(Optional.empty(), Optional.empty(), 3)
                                   .size());
        assertEquals(FINISHED, bpmProvider.getCompletedProcessInstances(Optional.empty(), Optional.empty(), null)
                                          .size());
    }

    @Test
    void theVariablesAreBoundedToTheLimitInNameOrder() {
        List<VariableInstance> page = bpmProvider.getProcessInstanceVariables(someInstance, Optional.empty(), 2);

        assertEquals(List.of("a", "b"), page.stream()
                                            .map(VariableInstance::getName)
                                            .toList(),
                "the first two by name");
        assertEquals(3, bpmProvider.getProcessInstanceVariables(someInstance, Optional.empty(), null)
                                   .size());
    }

    @Test
    void theDeadLetterJobsListingAcceptsALimit() {
        assertEquals(List.of(), bpmProvider.getDeadLetterJobs(someInstance, 10));
    }

    private static TenantContext tenantContext() {
        Tenant tenant = mock(Tenant.class);
        when(tenant.getId()).thenReturn(TENANT_ID);
        TenantContext tenantContext = mock(TenantContext.class);
        when(tenantContext.getCurrentTenant()).thenReturn(tenant);
        return tenantContext;
    }
}
