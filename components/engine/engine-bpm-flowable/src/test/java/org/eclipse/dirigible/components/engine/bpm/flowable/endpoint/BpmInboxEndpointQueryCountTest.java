/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.bpm.flowable.endpoint;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.dirigible.components.engine.bpm.flowable.dto.ProcessInstanceData;
import org.eclipse.dirigible.components.engine.bpm.flowable.dto.TaskDTO;
import org.eclipse.dirigible.components.engine.bpm.flowable.service.BpmService;
import org.eclipse.dirigible.components.engine.bpm.flowable.service.PrincipalType;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

/**
 * What one Inbox listing costs, in reads against the engine. A row used to cost a statement of its
 * own for its identity links and two for its process instance, so an inbox of ~3 000 open tasks was
 * ~9 000 statements in one request - past the load balancer's timeout, and the Inbox showed nothing
 * (issue #7977). A listing now costs the task query, ONE batched instance read and a label catalog
 * per process definition, however many rows it has; and a paged listing is bounded in the database.
 */
class BpmInboxEndpointQueryCountTest {

    private final BpmService bpmService = mock(BpmService.class);

    private final BpmInboxEndpoint endpoint = new BpmInboxEndpoint(bpmService);

    @BeforeEach
    void noLabels() {
        when(bpmService.getProcessLabelKeys(anyString())).thenReturn(Optional.empty());
        // the batched read answers for every instance it is asked about
        when(bpmService.getListedProcessInstances(anyCollection())).thenAnswer(invocation -> {
            Map<String, ProcessInstanceData> found = new HashMap<>();
            for (Object id : (Collection<?>) invocation.getArgument(0)) {
                ProcessInstanceData instance = new ProcessInstanceData();
                instance.setBusinessKey("key-" + id);
                found.put((String) id, instance);
            }
            return found;
        });
    }

    /** A task of the given process instance. */
    private Task task(String id, String instanceId) {
        Task task = mock(Task.class);
        when(task.getId()).thenReturn(id);
        when(task.getProcessInstanceId()).thenReturn(instanceId);
        when(task.getProcessVariables()).thenReturn(Map.of());
        return task;
    }

    /**
     * The issue's shape: an approval inbox is one open task per instance. Every instance is read in ONE
     * call, never one at a time.
     */
    @Test
    void theProcessInstancesOfAListingAreReadInOneCall() {
        List<Task> found = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            found.add(task("t" + i, "instance-" + i));
        }
        when(bpmService.findTasksWithProcessVariables(PrincipalType.CANDIDATE_GROUPS)).thenReturn(found);

        List<TaskDTO> tasks = endpoint.getTasks("groups")
                                      .getBody();

        assertEquals(300, tasks.size(), "every task is still listed");
        assertEquals("key-instance-299", tasks.get(299)
                                              .getProcessInstanceBusinessKey(),
                "each row carries its own instance");
        verify(bpmService, times(1)).getListedProcessInstances(anyCollection());
        verify(bpmService, never()).getProcessInstanceById(anyString());
    }

    /** A row's candidate users and groups come off the task the listing query returned. */
    @Test
    void aListedTasksIdentityLinksCostNoReadOfTheirOwn() {
        List<Task> found = List.of(task("t1", "instance-1"), task("t2", "instance-2"));
        when(bpmService.findTasksWithProcessVariables(PrincipalType.ASSIGNEE)).thenReturn(found);

        endpoint.getTasks("assignee");

        verify(bpmService, never()).getListedTaskIdentityLinks(anyString());
        verify(bpmService, never()).getTaskIdentityLinks(anyString());
    }

    /** Several tasks of one instance: the instance is asked for once. */
    @Test
    void aProcessInstanceListingReadsItsInstanceOnce() {
        List<Task> found = List.of(task("t1", "instance-1"), task("t2", "instance-1"), task("t3", "instance-1"));
        when(bpmService.findTasksWithProcessVariables("instance-1", PrincipalType.ASSIGNEE)).thenReturn(found);

        endpoint.getProcessInstanceTasks("instance-1", "assignee");

        verify(bpmService, times(1)).getListedProcessInstances(argThat(ids -> ids.size() == 1 && ids.contains("instance-1")));
        verify(bpmService, never()).getProcessInstanceById(anyString());
    }

    /**
     * The limit both shell clients send is honoured: the page is asked of the engine, never the whole
     * inbox, and the size of the whole inbox travels in a header for the count.
     */
    @Test
    void aLimitedListingAsksTheEngineForOnePageAndCountsTheRest() {
        List<Task> page = List.of(task("t1", "instance-1"), task("t2", "instance-2"));
        when(bpmService.findTasksWithProcessVariables(PrincipalType.CANDIDATE_GROUPS, 0, 100)).thenReturn(page);
        when(bpmService.countTasks(PrincipalType.CANDIDATE_GROUPS)).thenReturn(2940L);

        ResponseEntity<List<TaskDTO>> response = endpoint.getTasks("groups", 100, null);

        assertEquals(2, response.getBody()
                                .size());
        assertEquals("2940", response.getHeaders()
                                     .getFirst(BpmInboxEndpoint.TOTAL_COUNT_HEADER));
        verify(bpmService, never()).findTasksWithProcessVariables(PrincipalType.CANDIDATE_GROUPS);
    }

    /** A page beyond the cap is clamped, and an offset is passed through. */
    @Test
    void anOversizedLimitIsClampedAndTheOffsetPassedThrough() {
        when(bpmService.findTasksWithProcessVariables(any(PrincipalType.class), any(Integer.class), any(Integer.class))).thenReturn(
                List.of());

        endpoint.getTasks("groups", 1_000_000, 200);

        verify(bpmService).findTasksWithProcessVariables(PrincipalType.CANDIDATE_GROUPS, 200, BpmInboxEndpoint.MAX_PAGE_SIZE);
    }
}
