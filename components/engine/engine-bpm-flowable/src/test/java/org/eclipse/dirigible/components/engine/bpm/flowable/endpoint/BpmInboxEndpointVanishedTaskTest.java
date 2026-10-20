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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.dirigible.components.engine.bpm.flowable.dto.ProcessInstanceData;
import org.eclipse.dirigible.components.engine.bpm.flowable.dto.TaskDTO;
import org.eclipse.dirigible.components.engine.bpm.flowable.service.BpmService;
import org.eclipse.dirigible.components.engine.bpm.flowable.service.PrincipalType;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A task completed while the Inbox list is being built must drop out of the list, not fail it with
 * a 500 for everyone loading it at that moment (issue #7575). Since the identity links ride the
 * listing query (issue #7977), the race shows in one place only: the task's process has ended - its
 * LAST task was completed - so the batched instance read no longer finds the instance, and the
 * single read the row falls back to fails.
 */
class BpmInboxEndpointVanishedTaskTest {

    private final BpmService bpmService = mock(BpmService.class);

    private final BpmInboxEndpoint endpoint = new BpmInboxEndpoint(bpmService);

    /** The instances whose process has ended by the time the batched read runs. */
    private final Set<String> ended = new HashSet<>();

    @BeforeEach
    void engine() {
        when(bpmService.getProcessLabelKeys(anyString())).thenReturn(Optional.empty());
        when(bpmService.getListedProcessInstances(anyCollection())).thenAnswer(invocation -> {
            Map<String, ProcessInstanceData> found = new HashMap<>();
            for (Object id : (Collection<?>) invocation.getArgument(0)) {
                if (!ended.contains(id)) {
                    found.put((String) id, new ProcessInstanceData());
                }
            }
            return found;
        });
    }

    private Task task(String id) {
        Task task = mock(Task.class);
        when(task.getId()).thenReturn(id);
        when(task.getProcessInstanceId()).thenReturn("instance-" + id);
        when(task.getProcessVariables()).thenReturn(Map.of());
        when(bpmService.isTaskActive(id)).thenReturn(true);
        return task;
    }

    /**
     * Completing a process's last task ends the process: its instance is gone by the time it is read.
     */
    private void completedMeanwhile(String id) {
        ended.add("instance-" + id);
        when(bpmService.getProcessInstanceById("instance-" + id)).thenThrow(new NullPointerException("processInstance"));
        when(bpmService.isTaskActive(id)).thenReturn(false);
    }

    @Test
    void aTaskWhoseProcessEndedBeforeItWasReadIsOmitted() {
        List<Task> found = List.of(task("t1"), task("t2"), task("t3"));
        completedMeanwhile("t2");
        when(bpmService.findTasksWithProcessVariables(PrincipalType.CANDIDATE_GROUPS)).thenReturn(found);

        List<TaskDTO> tasks = endpoint.getTasks("groups")
                                      .getBody();

        assertEquals(List.of("t1", "t3"), ids(tasks), "the vanished task is omitted, the rest of the list still answers");
    }

    /** The same on a paged listing - the shell's own call. */
    @Test
    void aPagedListingOmitsAVanishedTaskToo() {
        List<Task> found = List.of(task("t1"), task("t2"));
        completedMeanwhile("t1");
        when(bpmService.findTasksWithProcessVariables(PrincipalType.CANDIDATE_GROUPS, 0, 100)).thenReturn(found);

        List<TaskDTO> tasks = endpoint.getTasks("groups", 100, 0)
                                      .getBody();

        assertEquals(List.of("t2"), ids(tasks));
    }

    /** A read that fails while the task is still there is a real failure, not the race. */
    @Test
    void aFailureOnATaskThatStillExistsStillFailsTheList() {
        List<Task> found = List.of(task("t1"));
        ended.add("instance-t1");
        IllegalStateException failure = new IllegalStateException("database unavailable");
        when(bpmService.getProcessInstanceById("instance-t1")).thenThrow(failure);
        when(bpmService.findTasksWithProcessVariables(PrincipalType.ASSIGNEE)).thenReturn(found);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> endpoint.getTasks("assignee"));

        assertSame(failure, thrown);
    }

    /** The listing of one process instance's tasks takes the same path. */
    @Test
    void aProcessInstanceListingOmitsAVanishedTaskToo() {
        List<Task> found = List.of(task("t1"), task("t2"));
        completedMeanwhile("t1");
        when(bpmService.findTasksWithProcessVariables("instance", PrincipalType.ASSIGNEE)).thenReturn(found);

        List<TaskDTO> tasks = endpoint.getProcessInstanceTasks("instance", "assignee")
                                      .getBody();

        assertEquals(List.of("t2"), ids(tasks));
    }

    private static List<String> ids(List<TaskDTO> tasks) {
        return tasks.stream()
                    .map(TaskDTO::getId)
                    .toList();
    }
}
