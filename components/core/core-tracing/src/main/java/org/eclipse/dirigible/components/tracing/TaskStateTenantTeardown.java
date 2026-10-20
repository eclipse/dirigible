/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tracing;

import org.eclipse.dirigible.components.base.tenant.TenantTeardownParticipant;
import org.springframework.stereotype.Component;

/**
 * Deprovisioning: deletes the task states traced in the tenant - their input and output may carry
 * the tenant's data.
 */
@Component
class TaskStateTenantTeardown implements TenantTeardownParticipant {

    /** The order of this participant: the tenant's content, after what runs for it. */
    static final int ORDER = 600;

    /** The task states. */
    private final TaskStateRepository taskStates;

    /**
     * Instantiates a new task state teardown.
     *
     * @param taskStates the task states
     */
    TaskStateTenantTeardown(TaskStateRepository taskStates) {
        this.taskStates = taskStates;
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public void teardown(String tenantId) {
        taskStates.deleteByTenant(tenantId);
    }
}
