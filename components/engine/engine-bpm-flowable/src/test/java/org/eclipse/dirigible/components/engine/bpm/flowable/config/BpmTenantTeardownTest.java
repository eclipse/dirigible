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

import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

/**
 * Deprovisioning cancels the tenant's running instances first, then deletes its whole process
 * history.
 */
class BpmTenantTeardownTest {

    @Test
    void runningInstancesAreCancelledBeforeTheHistoryIsDeleted() {
        ProcessEngine engine = mock(ProcessEngine.class);
        RuntimeService runtime = mock(RuntimeService.class);
        HistoryService history = mock(HistoryService.class);
        when(engine.getRuntimeService()).thenReturn(runtime);
        when(engine.getHistoryService()).thenReturn(history);

        ProcessInstanceQuery running = mock(ProcessInstanceQuery.class, RETURNS_SELF);
        ProcessInstance instance = mock(ProcessInstance.class);
        when(instance.getId()).thenReturn("p1");
        when(running.list()).thenReturn(List.of(instance));
        when(runtime.createProcessInstanceQuery()).thenReturn(running);

        HistoricProcessInstanceQuery finished = mock(HistoricProcessInstanceQuery.class, RETURNS_SELF);
        HistoricProcessInstance historic = mock(HistoricProcessInstance.class);
        when(historic.getId()).thenReturn("p1");
        when(finished.list()).thenReturn(List.of(historic));
        when(history.createHistoricProcessInstanceQuery()).thenReturn(finished);

        new BpmTenantTeardown(engine).teardown("acme");

        InOrder order = inOrder(running, runtime, finished, history);
        order.verify(running)
             .processInstanceTenantId("acme");
        order.verify(runtime)
             .deleteProcessInstance("p1", BpmTenantTeardown.REASON);
        order.verify(finished)
             .processInstanceTenantId("acme");
        order.verify(history)
             .deleteHistoricProcessInstance("p1");
    }
}
