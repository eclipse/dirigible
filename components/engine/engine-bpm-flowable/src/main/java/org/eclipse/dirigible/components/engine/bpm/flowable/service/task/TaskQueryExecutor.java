/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.bpm.flowable.service.task;

import org.eclipse.dirigible.components.api.security.UserFacade;
import org.eclipse.dirigible.components.engine.bpm.flowable.service.BpmService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * The TaskQueryExecutor.
 */
@Component
public record TaskQueryExecutor(BpmService bpmService) {

    /**
     * Find tasks.
     *
     * @param processInstanceId the process instance id
     * @param type the type
     * @return the list
     */
    public List<Task> findTasks(String processInstanceId, Type type) {
        List<Task> tasks = Collections.emptyList();
        if (Type.CANDIDATE_GROUPS.equals(type)) {
            tasks = getTaskService().createTaskQuery()
                                    .processInstanceId(processInstanceId)
                                    .taskCandidateGroupIn(UserFacade.getUserRoles())
                                    .list();

        } else if (Type.ASSIGNEE.equals(type)) {
            tasks = getTaskService().createTaskQuery()
                                    .processInstanceId(processInstanceId)
                                    .taskAssignee(UserFacade.getName())
                                    .list();
        }
        return tasks;
    }

    /**
     * Gets the task service.
     *
     * @return the task service
     */
    private TaskService getTaskService() {
        return bpmService.getBpmProviderFlowable()
                         .getProcessEngine()
                         .getTaskService();
    }

    /**
     * The Enum Type.
     */
    public enum Type {

        /** The assignee. */
        ASSIGNEE("assignee"),
        /** The candidate groups. */
        CANDIDATE_GROUPS("groups");

        /** The type. */
        private final String type;

        /**
         * Instantiates a new type.
         *
         * @param type the type
         */
        Type(String type) {
            this.type = type;
        }

        /**
         * From string.
         *
         * @param type the type
         * @return the type
         */
        public static Type fromString(String type) {
            for (Type enumValue : Type.values()) {
                if (enumValue.type.equals(type)) {
                    return enumValue;
                }
            }
            throw new IllegalArgumentException("Unknown enum type: " + type);
        }
    }
}
